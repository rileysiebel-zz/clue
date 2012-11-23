package players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

import reasoner.ClueReasoner;
import reasoner.Sample;

import board.Card;
import board.Clue_Board;
import board.Constants;
import board.Space;
import game.Clue_Game;


public class AIPlayer extends Player {
	private ClueReasoner cr;
	private Random r;
	private List<String> players;

	List<List<Space>> map;
	private double[][] scores; 
	// entry (i, 0) is best suspect in room i
	// entry (i, 1) is best weapon in room i
	private int[][] best_suggestions;

	private int numPlayers;
	public AIPlayer(String character, List<String> players) {
		super(character, true);

		scores = new double[Constants.BOARD_X][Constants.BOARD_Y];
		cr = new ClueReasoner(players);
		r = new Random();

		best_suggestions = new int[Constants.rooms.length][2];
		this.numPlayers = players.size();
		this.players = players;
	}


	public boolean makeSuggestion() {
		// TODO something better than this
		return true;
	}

	// MUST CALL THIS AFTER DEALING *ALL* CARDS
	public void doneDealing() {
		String[] hand_a = new String[hand.size()];
		int i = 0;
		for(Card c : hand.getCards()) {
			hand_a[i] = c.getFace();
			i++;
		}
		cr.hand(this.character, hand_a);
		value_iterate();
	}

	// Only make an accusation if you know the answer
	public boolean makeAccusation() {
		// TODO
		return false;
	}

	public void move(int roll, Clue_Board board) {		
		
		for(int j = 0; j < scores[0].length; j++){
			for(int i = 0; i < scores.length; i++) {
				System.out.printf("%f ", scores[i][j]);
			}
			System.out.println();
		}	
		
		// Choose a next move until roll is 0 
		
		int newx = this.x;
		int newy = this.y;
		while(roll > 0) {
			// Get the best neighbor
			double max_score = 0;
			int best_x = -1;
			int best_y = -1;
			for(Space n : board.map.get(newx).get(newy).getNeighbors()) {
				if(!n.isReachable())
					continue;
				double score = scores[n.getXPos()][n.getYPos()];
				if(score > max_score) {
					max_score = score;
						best_x = n.getXPos();
						best_y = n.getYPos();
				}
			}
			
			if(best_x == -1) 
				break;
			else if(best_y == -1)
				break;
			else {
				newx = best_x;
				newy = best_y;
				if(map.get(newx).get(newy).getType() != Constants.SPACE)
					board.move(newx, newy);
				roll--;
			}
		}
		board.move(newx, newy);
	}

	public void suggest(String suggester, 
			String card1, String card2, String card3, 
			String refuter, String cardShown) {
		cr.suggest(suggester, card1, card2, card3, refuter, cardShown);
		value_iterate();
	}

	public void accuse(String accuser,
			String card1, String card2, String card3,
			boolean isCorrect) {
		cr.accuse(accuser, card1, card2, card3, isCorrect);
		value_iterate();
	}

	private void value_iterate() {
		double[] room_scores = new double[Constants.rooms.length];
		for(int i = 0; i < room_scores.length; i++) {
			room_scores[i] = expected_value(i);
		}

		// The first thing is to set the value of all the rooms
		// as calculated above	
		for(List<Space> column : map) {
			for(Space sp : column) {
				if((sp.getType() != Constants.BLOCKED) && 
						(sp.getType() != Constants.SPACE)) {
					scores[sp.getXPos()][sp.getYPos()] = room_scores[sp.getType()];
				}
			}
		}

		// Value Iteration
		double delta;
		do{
			double scores_p[][] = new double[scores.length][scores[0].length];
			delta = 0;
			for(x = 0; x < scores_p.length; x++) {
				for(int y = 0; y < scores_p[0].length; y++) {
					if(map.get(x).get(y).getType() == Constants.SPACE) {
						double max_score = 0;
						for(Space n : map.get(x).get(y).getNeighbors()) {
							if(scores[n.getXPos()][n.getYPos()] > max_score)
								max_score = scores[n.getXPos()][n.getYPos()];
						}

						if(max_score * Constants.discount > scores[x][y]) {
							scores_p[x][y] = max_score * Constants.discount;
							if(Math.abs(scores_p[x][y] - scores[x][y]) > delta)
								delta = Math.abs(scores_p[x][y] - scores[x][y]);
						}
						else
							scores_p[x][y] = scores[x][y];
					}
					else if(map.get(x).get(y).getType() == Constants.BLOCKED) {
						scores_p[x][y] = 0;
					}
					else {
						scores_p[x][y] = scores[x][y];
					}	
				}		
			}
			this.scores = scores_p;
		} while(delta >= Constants.precision);
		
	}


	// Expected entropy reduction from making the best suggestion
	// in room R
	private double expected_value(int r) {
		// For each possible suggestion in this room
		int best_suspect = -1;
		int best_weapon = -1;
		double best_score = Double.MIN_NORMAL;

		for(int s = 0; s < Constants.suspects.length; s++) {
			for(int w = 0; w < Constants.weapons.length; w++) {
				double score = suggestion_value(r, s, w);
				if(score > best_score) {
					best_score = score;
					best_suspect = s;
					best_weapon = w;
				}
			}
		}

		best_suggestions[r][0] = best_suspect;
		best_suggestions[r][1] = best_weapon;

		return best_score;
	}

	// The expected reduction in entropy from making this suggestion
	private double suggestion_value(int r, int s, int w) {
		double H = cr.cfEntropy();
		double H_exp = 0;

		// Iterate over all possible refuters
		double factor = 1;

		for(int ref = 1; ref < numPlayers; ref++) {
			String refuter = Constants.players[(this.player_num + ref) % numPlayers];

			double a = cr.probability(refuter, Constants.rooms[r]);
			double b = cr.probability(refuter, Constants.suspects[s]);
			double c = cr.probability(refuter, Constants.weapons[w]);
			
			double A = (a * (1-b) * (1-c)) + 0.5 * (a * b * (1-c)) + 0.5 * (a * (1-b) * c) + (1/3) * (a * b * c);
			double B = ((1-a) * b * (1-c)) + 0.5 * (a * b * (1-c)) + 0.5 * ((1-a) * b * c) + (1/3) * (a * b * c);
			double C = ((1-a) * (1-b) * c) + 0.5 * (a * (1-b) * c) + 0.5 * ((1-a) * b * c) + (1/3) * (a * b * c);
			
			ClueReasoner possible_room = new ClueReasoner(cr);
			possible_room.suggest(Constants.players[player_num], 
					Constants.rooms[r], Constants.suspects[s], 
					Constants.weapons[w], refuter, 
					Constants.rooms[r]);
			double H1 = possible_room.cfEntropy();

			ClueReasoner possible_suspect = new ClueReasoner(cr);
			possible_suspect.suggest(Constants.players[player_num], 
					Constants.rooms[r], Constants.suspects[s], 
					Constants.weapons[w], refuter, 
					Constants.suspects[s]);
			double H2 = possible_suspect.cfEntropy();

			ClueReasoner possible_weapon = new ClueReasoner(cr);
			possible_weapon.suggest(Constants.players[player_num], 
					Constants.rooms[r], Constants.suspects[s], 
					Constants.weapons[w], refuter, 
					Constants.weapons[w]);
			double H3 = possible_weapon.cfEntropy();

			H_exp += factor * ((H1 * A) + (H2 * B) + (H3 * C));		
			factor *= (1 - a) * (1 - b) * (1 - c);
		}
		return H - H_exp;
	}

	public void addMap(List<List<Space>> map) {
		this.map = map;
	}
	
	private boolean movable(Space a, Space b) {
		if((a.getType() == Constants.SPACE) && (b.getType() == Constants.SPACE))
			return true;
		if((a.getType() == Constants.BLOCKED) || (b.getType() == Constants.BLOCKED)) {
			return false;
		}
		if(a.getType() == Constants.SPACE) {
			if(b.door == true)
				return true;
			return false;
		}
		if(b.getType() == Constants.SPACE) {
			if(a.door == true)
				return true;
			return false;
		}
		return false;
	}


	public String suggestion_suspect() {
		return Constants.suspects[best_suggestions[map.get(x).get(y).getType()][0]];
	}
	
	public String suggestion_weapon() {
		return Constants.weapons[best_suggestions[map.get(x).get(y).getType()][1]];
	}


	public String refute_suggestion(String character, String weapon, String room) {
		List<String> refutable = new ArrayList<String>();
		for(Card c : hand.getCards()) {
			if(c.getFace() == character)
				refutable.add(character);
			if(c.getFace() == weapon)
				refutable.add(weapon);
			if(c.getFace() == room)	
				refutable.add(room);
		}
		return refutable.get(r.nextInt(refutable.size()));
	}


	public Player accusationSuspect() {
		// TODO Auto-generated method stub
		return null;
	}
}
