package board;

import game.Clue_Game;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import players.AIPlayer;
import players.Human_Player;
import players.Player;




public class Clue_Board extends JPanel implements MouseListener {
	private JPanel board;

	private List<Player> players;
	private int current_player = 0;

	public List<List<Space>> map;

	private List<Space> current_reachable;
	private int[][] room_location;

	private Deck deck;

	private Random r;

	// CaseFile
	private Card case_suspect;
	private Card case_weapon;
	private Card case_room;


	public Clue_Board(List<Player> players) {
		this.players = players;
		this.deck = new Deck();
		this.r = new Random();
		this.current_reachable = new ArrayList<Space>();

		initialize_map();
		initialize_neighbors();
		initialize_start_positions();
		initialize_listeners();

		this.board = new JPanel();
		board.setLayout(new GridLayout(Constants.BOARD_Y, Constants.BOARD_X));

		for(int i = 0; i < Constants.BOARD_Y; i++) {
			for(int j = 0; j < Constants.BOARD_X; j++) {
				//System.out.println("(" + j + ", " + i + ")");
				map.get(j).get(i).initPanel();
				board.add(map.get(j).get(i));
			}
		}
		this.setLayout(new BorderLayout());
		this.add(board, BorderLayout.CENTER);
		deal();
	}

	public void play() {			
			while(!players.get(current_player).isPlaying())
				current_player = (current_player + 1) % players.size();
			Player player = players.get(current_player);
			Space loc = map.get(player.getX()).get(player.getY());
			
			if(player.isMoved()) {
				if(make_suggestion(player)) {
					suggestion(map.get(player.getX()).get(player.getY()));
					current_player = (current_player + 1) % players.size();
				}
			}
			
			int roll = r.nextInt(6) + 1;
			setReachables(loc, roll, false);

			BorderLayout layout = (BorderLayout) this.getLayout();

			if(player instanceof Human_Player) {
				if(layout.getLayoutComponent(BorderLayout.PAGE_END) != null)
					this.remove(layout.getLayoutComponent(BorderLayout.PAGE_END));
				this.add(((Human_Player) player).getHand(), BorderLayout.PAGE_END);
				this.revalidate();
				this.repaint();

				JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(),
						"It's your turn!\nYou have rolled a " + roll, player.getCharacter(),
						JOptionPane.INFORMATION_MESSAGE);
			}
			else if(players.get(current_player) instanceof AIPlayer){
				AIPlayer ap = (AIPlayer) players.get(current_player);

				JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), 
						ap.getCharacter() + " has rolled a " + roll);
				ap.move(roll, this);
			}
			current_player = (current_player + 1) % players.size();
	}


	private boolean make_suggestion(Player player) {
		if(player instanceof Human_Player) {
			int o = JOptionPane.showConfirmDialog(Clue_Game.getSingletonObject(), 
					"You have been moved to a room by another player,\n" +
					"would you like to make a suggestion?",
					player.getCharacter(),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(o == JOptionPane.YES_OPTION)
				return true;
			return false;
		}
		else {
			AIPlayer ap = (AIPlayer) player;
			if(ap.makeSuggestion())
				return true;
			return false;
		}
	}

	private boolean make_accusation(Player player) {
		if(player instanceof Human_Player) {
			int o = JOptionPane.showConfirmDialog(Clue_Game.getSingletonObject(), 
					"Would you like to make a suggestion?",
					player.getCharacter(),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(o == JOptionPane.YES_OPTION)
				return true;
			return false;
		}
		else {
			AIPlayer ap = (AIPlayer) player;
			if(ap.makeAccusation())
				return true;
			return false;
		}
	}

	// flag = true means this path has already entered a room, and so
	// cannot propogate further
	private void setReachables(Space origin, int roll, boolean flag) {
		if(roll <= 0)
			return;

		if((origin.type == Constants.SPACE)) {
			for(Space s : origin.getNeighbors()) {
				if(s.getPlayer() == Constants.DEFAULT) {
					if(s.getType() != Constants.SPACE) {
						// If we haven't been in a room yet we can enter
						if(flag == false) {
							s.setReachable(true);
							current_reachable.add(s);
						}
					}
					else {
						s.setReachable(true);
						current_reachable.add(s);
						setReachables(s, roll - 1, flag);
					}
				}
			}
		}
		else {
			if(flag == false) {
				for(Space s : origin.getNeighbors()) {
					s.setReachable(true);
					current_reachable.add(s);
					setReachables(s, roll -1, true);
				}
			}
		}
	}

	private void deal() {
		this.case_room = deck.getRoom();
		this.case_suspect = deck.getSuspect();
		this.case_weapon = deck.getWeapon();

		int player_index = 0;
		Card temp;
		while((temp = deck.getSuspect()) != null) {
			players.get(player_index).deal(temp);
			player_index = (player_index + 1) % players.size();
		}
		while((temp = deck.getRoom()) != null) {
			players.get(player_index).deal(temp);
			player_index = (player_index + 1) % players.size();
		}
		while((temp = deck.getWeapon()) != null) {
			players.get(player_index).deal(temp);
			player_index = (player_index + 1) % players.size();
		}

		// Alert the AI's that we're done dealing
		for(Player p : players) {
			if(p instanceof AIPlayer)
				((AIPlayer) p).doneDealing();
		}
	}

	private void initialize_listeners() {
		for(List<Space> list : map) {
			for(Space sp : list) {
				sp.addMouseListener(this);
			}
		}
	}

	private void initialize_start_positions() {
		for(List<Space> column : map)
			for(Space sp : column) 
				sp.setPlayer(Constants.DEFAULT);

		for(Player p : players) {
			// Use this opportunity to give the players the board map
			if(p instanceof AIPlayer)
				((AIPlayer) p).addMap(map);
			this.map.get(p.getX()).get(p.getY()).setPlayer(p.getCharacter());
		}
	}

	private void initialize_neighbors() {
		for(int x = 0; x < Constants.BOARD_X; x++) {
			for(int y = 0; y < Constants.BOARD_Y; y++) {
				Space temp = map.get(x).get(y);
				switch(temp.type) {
				case Constants.BALLROOM:
					temp.addNeighbor(map.get(7).get(19));
					temp.addNeighbor(map.get(9).get(16));
					temp.addNeighbor(map.get(14).get(16));
					temp.addNeighbor(map.get(16).get(19));
					break;
				case Constants.BILLIARD_ROOM:
					temp.addNeighbor(map.get(1).get(11));
					temp.addNeighbor(map.get(6).get(15));
					break;
				case Constants.LIBRARY:
					temp.addNeighbor(map.get(3).get(11));
					temp.addNeighbor(map.get(7).get(8));
					break;
				case Constants.CONSERVATORY:
					temp.addNeighbor(map.get(4).get(18));
					temp.addNeighbor(map.get(5).get(19));
					temp.addNeighbor(map.get(6).get(20));
					// Lounge
					temp.addNeighbor(map.get(17).get(5));
					break;
				case Constants.STUDY:
					temp.addNeighbor(map.get(6).get(4));
					// Kitchen
					temp.addNeighbor(map.get(19).get(18));
					break;
				case Constants.KITCHEN:
					temp.addNeighbor(map.get(19).get(17));
					// Study
					temp.addNeighbor(map.get(6).get(3));
					break;
				case Constants.DINING_ROOM:
					temp.addNeighbor(map.get(15).get(12));
					temp.addNeighbor(map.get(17).get(8));
					break;
				case Constants.LOUNGE:
					temp.addNeighbor(map.get(17).get(6));
					// Conservatory
					temp.addNeighbor(map.get(4).get(19));
					temp.addNeighbor(map.get(5).get(20));
					break;
				case Constants.HALL:
					temp.addNeighbor(map.get(8).get(4));
					temp.addNeighbor(map.get(11).get(7));
					temp.addNeighbor(map.get(12).get(7));
					break;
				case Constants.SPACE:
					// Dealt with below
					break;
				default:
					break;
				}
			}
		}

		Space temp;
		for(int i = 0; i < Constants.BOARD_X; i++) {
			for(int j = 0; j < Constants.BOARD_Y; j++) {
				temp = map.get(i).get(j);
				if(temp.type != Constants.SPACE) continue;
				Space nbr;
				// Checking left
				if(temp.xPos > 0) {
					nbr = map.get(temp.xPos-1).get(temp.yPos);
					if(nbr.type == Constants.SPACE)
						temp.addNeighbor(nbr);
					// Its a room
					else if (nbr.type != Constants.BLOCKED) {
						if(nbr.neighbors.contains(temp)) {
							temp.addNeighbor(nbr);
						}
					}
				}
				// Checking right
				if(temp.xPos < 23) {
					nbr = map.get(temp.xPos+1).get(temp.yPos);
					if(nbr.type == Constants.SPACE)
						temp.addNeighbor(nbr);
					// Its a room
					else if (nbr.type != Constants.BLOCKED) {
						if(nbr.neighbors.contains(temp)) {
							temp.addNeighbor(nbr);
						}
					}
				}
				// Checking up
				if(temp.yPos > 0) {
					nbr = map.get(temp.xPos).get(temp.yPos-1);
					if(nbr.type == Constants.SPACE)
						temp.addNeighbor(nbr);
					// Its a room
					else if (nbr.type != Constants.BLOCKED) {
						if(nbr.neighbors.contains(temp)) {
							temp.addNeighbor(nbr);
						}
					}
				}
				// Checking down
				if(temp.yPos < 24) {
					nbr = map.get(temp.xPos).get(temp.yPos+1);
					if(nbr.type == Constants.SPACE)
						temp.addNeighbor(nbr);
					// Its a room
					else if (nbr.type != Constants.BLOCKED) {
						if(nbr.neighbors.contains(temp)) {
							temp.addNeighbor(nbr);
						}
					}
				}
			}
		}

	}

	private void initialize_map() {
		// Set up our standard room location
		this.room_location = new int[9][2];
		for(int i = 0; i <= 8; i++) {
			switch(i) {
			case Constants.STUDY:
				room_location[i][0] = 0;
				room_location[i][1] = 0;
				break;
			case Constants.HALL:
				room_location[i][0] = 9;
				room_location[i][1] = 0;
				break;
			case Constants.LOUNGE:
				room_location[i][0] = 17;
				room_location[i][1] = 1;
				break;
			case Constants.DINING_ROOM:
				room_location[i][0] = 16;
				room_location[i][1] = 10;
				break;
			case Constants.KITCHEN:
				room_location[i][0] = 18;
				room_location[i][1] = 19;
				break;
			case Constants.BALLROOM:
				room_location[i][0] = 9;
				room_location[i][1] = 20;
				break;
			case Constants.CONSERVATORY:
				room_location[i][0] = 0;
				room_location[i][1] = 20;
				break;
			case Constants.BILLIARD_ROOM:
				room_location[i][0] = 0;
				room_location[i][1] = 13;
				break;
			case Constants.LIBRARY:
				room_location[i][0] = 0;
				room_location[i][1] = 7;
				break;
			case Constants.SPACE:
			default: break;
			}
		}

		map = new ArrayList<List<Space>>(Constants.BOARD_X);

		// Initialize to null
		for(int i = 0; i < Constants.BOARD_X; i++) {
			map.add(new ArrayList<Space>(Constants.BOARD_Y));
			for(int j = 0; j < Constants.BOARD_Y; j++)
				map.get(i).add(j, null);
		}

		// First the Blocked spaces
		// upper
		map.get(6).set(0, new Space(6, 0, Constants.BLOCKED));
		map.get(8).set(0, new Space(8, 0, Constants.BLOCKED));
		map.get(15).set(0, new Space(15, 0, Constants.BLOCKED));
		map.get(17).set(0, new Space(17, 0, Constants.BLOCKED));
		// left
		map.get(0).set(4, new Space(0, 4, Constants.BLOCKED));
		map.get(0).set(6, new Space(0, 6, Constants.BLOCKED));
		map.get(0).set(10, new Space(0, 10, Constants.BLOCKED));
		map.get(0).set(11, new Space(0, 11, Constants.BLOCKED));
		map.get(0).set(17, new Space(0, 17, Constants.BLOCKED));
		map.get(0).set(19, new Space(0, 19, Constants.BLOCKED));
		// right
		map.get(23).set(6, new Space(23, 6, Constants.BLOCKED));
		map.get(23).set(8, new Space(23, 8, Constants.BLOCKED));
		map.get(23).set(16, new Space(23, 16, Constants.BLOCKED));
		map.get(23).set(18, new Space(23, 18, Constants.BLOCKED));
		// bottom
		map.get(6).set(23, new Space(6, 23, Constants.BLOCKED));
		map.get(17).set(23, new Space(17, 23, Constants.BLOCKED));
		map.get(6).set(24, new Space(6, 24, Constants.BLOCKED));
		map.get(7).set(24, new Space(7, 24, Constants.BLOCKED));
		map.get(8).set(24, new Space(8, 24, Constants.BLOCKED));
		map.get(15).set(24, new Space(15, 24, Constants.BLOCKED));
		map.get(16).set(24, new Space(16, 24, Constants.BLOCKED));
		map.get(17).set(24, new Space(17, 24, Constants.BLOCKED));
		// center block
		for(int i = 9; i < 14; i++) {
			for(int j = 8; j < 15; j++) {
				map.get(i).set(j, new Space(i, j, Constants.BLOCKED));
			}
		}

		// Study
		for(int i = 0; i < 7; i++) {
			for(int j = 0; j < 4; j++) {
				if(map.get(i).get(j) == null)
					map.get(i).set(j, new Space(i, j, Constants.STUDY));
			}
		}

		// Hall;
		for(int i = 9; i < 15; i++) {
			for(int j = 0; j < 7; j++) {
				if(map.get(i).get(j) == null)
					map.get(i).set(j, new Space(i, j, Constants.HALL));
			}
		}

		// Lounge
		for(int i = 17; i < 24; i++) {
			for(int j = 0; j < 6; j++) {
				if(map.get(i).get(j) == null) 
					map.get(i).set(j, new Space(i, j, Constants.LOUNGE));
			}
		}

		// Library
		for(int i = 0; i < 7; i++) {
			for(int j = 6; j < 11; j++) {
				if(map.get(i).get(j) == null) {
					if((i == 6) && ((j == 6) ||  (j == 10))) ;
					else
						map.get(i).set(j, new Space(i, j, Constants.LIBRARY));
				}
			}
		}

		// Billiard Room
		for(int i = 0; i < 6; i++) {
			for(int j = 12; j < 17; j++) {
				if(map.get(i).get(j) == null) {
					map.get(i).set(j, new Space(i, j, Constants.BILLIARD_ROOM));
				}
			}
		}

		// Conservatory
		for(int i = 0; i < 6; i++) {
			for(int j = 19; j < 25; j++) {
				if(map.get(i).get(j) == null) {
					if((i == 5) && (j == 19)) ;
					else
						map.get(i).set(j, new Space(i, j, Constants.CONSERVATORY));
				}
			}
		}

		// Ballroom
		for(int i = 8; i < 16; i++) {
			for(int j = 17; j < 23; j++) {
				if(map.get(i).get(j) == null) {
					map.get(i).set(j, new Space(i, j, Constants.BALLROOM));
				}
			}
		}

		for(int i = 10; i < 14; i++) {
			for(int j = 22; j < 25; j++) {
				if(map.get(i).get(j) == null)
					map.get(i).set(j, new Space(i, j, Constants.BALLROOM));
			}
		}

		// Dining Room	
		for(int i = 16; i < 24; i++) {
			for(int j = 9; j < 16; j++) {
				if(map.get(i).get(j) == null) {
					if((j == 15) && ((i == 16) || (i == 17) || (i == 18)))
						continue;
					map.get(i).set(j, new Space(i, j, Constants.DINING_ROOM));
				}
			}
		}

		// Kitchen
		for(int i = 18; i < 24; i++) {
			for(int j = 18; j < 25; j++) {
				if(map.get(i).get(j) == null) {
					map.get(i).set(j, new Space(i, j, Constants.KITCHEN));
				}
			}
		}

		// And now everything else is a space
		for(int i = 0; i < Constants.BOARD_X; i++) {
			for(int j = 0; j < Constants.BOARD_Y; j++) {
				if(map.get(i).get(j) == null)
					map.get(i).set(j, new Space(i, j, Constants.SPACE));
			}
		}
	}

	public void move(int X, int Y) {
		Space clicked = map.get(X).get(Y);
		if(clicked.isReachable()) {
			Player plyr = players.get(current_player);

			Space old = map.get(plyr.getX()).get(plyr.getY());

			if(clicked.type != Constants.SPACE) {
				clicked = map.get(room_location[clicked.type][0]).get(room_location[clicked.type][1]);
				while(clicked.getPlayer() != Constants.DEFAULT)
					clicked = map.get(clicked.xPos+1).get(clicked.yPos);
			}

			old.setPlayer(Constants.DEFAULT);
			clicked.setPlayer(plyr.getCharacter());
			// TODO are these necessary (repaint parent lower down)
			old.repaint();
			clicked.repaint();

			plyr.setX(clicked.xPos);
			plyr.setY(clicked.yPos);

			for(Space s : current_reachable) {
				s.setReachable(false);
			}
			current_reachable.clear();

			// Suggestion
			if(clicked.type != Constants.SPACE) {
				if(plyr instanceof Human_Player) {
					int s = JOptionPane.showConfirmDialog(Clue_Game.getSingletonObject(), 
							"You have entered a room,\n would you like to make a suggestion?", 
							plyr.getCharacter(), JOptionPane.YES_NO_OPTION);

					if(s == JOptionPane.YES_OPTION) {	
						suggestion(clicked);
					}
				}
				else if(plyr instanceof AIPlayer) {
					boolean s = ((AIPlayer) plyr).makeSuggestion();
					if(s)
						suggestion(clicked);
				}
			}
			this.repaint();
			play();
		}
		else {
			JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), 
					"This space is not currently reachable",
					"Error",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	public void mouseClicked(MouseEvent e) {
		move(((Space) e.getSource()).xPos, ((Space) e.getSource()).yPos);
	}

	private void suggestion(Space clicked) {
		Player s = null;
		String w = null;
		String r = Constants.rooms[clicked.getType()];

		if(players.get(current_player) instanceof Human_Player) {
			Suggestion_Panel sp = new Suggestion_Panel(clicked.type);
			JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), sp,
					players.get(current_player).getCharacter(), 
					JOptionPane.QUESTION_MESSAGE);
			s = getPlayer(sp.getSuspect());
			w = sp.getWeapon();
		}
		else if (players.get(current_player) instanceof AIPlayer) {
			s = getPlayer(((AIPlayer) players.get(current_player)).suggestion_suspect());
			w = ((AIPlayer) players.get(current_player)).suggestion_weapon();

			JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), "AI: " + 
					players.get(current_player).getCharacter() +
					" has suggested " + s.getCharacter() + " in the " + r +
					" with the " + w + "\n", "Suggestion", JOptionPane.INFORMATION_MESSAGE);
		}


		// Move the suggested player to our room
		if(s != null) {
			// Make sure s isn't already in the room
			if(map.get(s.getX()).get(s.getY()).getType() != clicked.getType()) {
				Space old = map.get(s.getX()).get(s.getY());
				old.setPlayer(Constants.DEFAULT);

				while(clicked.getPlayer() != Constants.DEFAULT)
					clicked = map.get(clicked.xPos+1).get(clicked.yPos);
				clicked.setPlayer(s.getCharacter());

				s.setX(clicked.getXPos());
				s.setY(clicked.getYPos());
				s.setMoved(true);
				old.repaint();
				clicked.repaint();
			}
		}


		// Get refuter
		boolean refuted = false;
		for(int i = 1; i < players.size(); i++) {
			Player p = players.get((current_player + i) % players.size());
			if(p.canRefute(s.getCharacter(), w, r)) {
				String refutation;
				if(p instanceof Human_Player) {
					Human_Player hp = (Human_Player) p;

					Refute_Panel rp = new Refute_Panel(hp.refute(
							s.getCharacter(), w, r));
					JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), rp, 
							p.getCharacter() + ", refute" + players.get(current_player).getCharacter() +
							"'s suggestion!", JOptionPane.PLAIN_MESSAGE);

					// Make sure they don't click null
					while(rp.getRefutation() == null) {
						JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), rp, 
								p.getCharacter() + ", refute" + players.get(current_player).getCharacter() +
								"'s suggestion! \n" + "MAKE A CHOICE!", JOptionPane.PLAIN_MESSAGE);
					}

					// Show the player the card that was refuted
					JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), 
							p.getCharacter() + " has refuted your suggestion"
							+ " by showing you " + rp.getRefutation() + ".",
							players.get(current_player).getCharacter(),
							JOptionPane.INFORMATION_MESSAGE);
					refutation = rp.getRefutation();
				}
				else if(p instanceof AIPlayer) {
					refutation = ((AIPlayer) p).refute_suggestion(s.getCharacter(), w, r);
				}
				else
					refutation = "";

				// Alert all the AI's
				for(Player np : players) {
					if(np instanceof AIPlayer) {
						if(np.equals(players.get(current_player)) || np.equals(p.getCharacter()))
							((AIPlayer) np).suggest(players.get(current_player).getCharacter(), 
									s.getCharacter(), w, r,
									p.getCharacter(), refutation);
						else
							((AIPlayer) np).suggest(players.get(current_player).getCharacter(), 
									s.getCharacter(), w, r,
									p.getCharacter(), null);
					}
				}

				refuted = true;
				break;	
			}
		}

		if(!refuted) {
			// Alert all the AI's
			for(Player p : players) {
				if(p instanceof AIPlayer) {
					((AIPlayer) p).suggest(players.get(current_player).getCharacter(), 
							s.getCharacter(), w, r,
							null, null);
				}
			}
			if(make_accusation(players.get(current_player))) {
				accusation(map.get(players.get(current_player).getX()).get(players.get(current_player).getY()));
			}
		}
	}



	private void accusation(Space space) {
		Player s = null;
		String r = null;
		String w = null;

		Player player = players.get(current_player);
		if(player instanceof Human_Player) {
			Suggestion_Panel ap = new Suggestion_Panel(space.type);
			JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), ap,
					player.getCharacter(), 
					JOptionPane.QUESTION_MESSAGE);
			s = getPlayer(ap.getSuspect());
			r = ap.getRoom();
			w = ap.getWeapon();

		}
		else {
			AIPlayer ap = ((AIPlayer) player);
			//	s = ap.accusationSuspect();
			//r = ap.accusationRoom();
			//w = ap.accusationWeapon();
		}
		if(s.getCharacter().equals(this.case_suspect.getFace()) 
				&& r.equals(this.case_room.getFace())
				&& w.equals(this.case_weapon.getFace())) {
			JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(), 
					"You Win!", 
					players.get(current_player).getCharacter(), 
					JOptionPane.INFORMATION_MESSAGE);

			Clue_Game.getSingletonObject().dispose();
			System.exit(0);
		}
		else {
			JOptionPane.showMessageDialog(Clue_Game.getSingletonObject(),
					"You Lose!",
					players.get(current_player).getCharacter(),
					JOptionPane.INFORMATION_MESSAGE);
			players.get(current_player).setPlaying(false);
		}
	}
	private Player getPlayer(String character) {
		for(Player p : players) {
			if(p.getCharacter().equals(character))
				return p;
		}
		return null;
	}

	public void mouseEntered(MouseEvent arg0) {
		return;	
	}

	public void mouseExited(MouseEvent arg0) {
		return;
	}

	public void mousePressed(MouseEvent arg0) {
		return;
	}

	public void mouseReleased(MouseEvent arg0) {
		return;		
	}
}
