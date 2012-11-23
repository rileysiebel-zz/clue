package reasoner;

/**
 * ClueReasoner.java - project skeleton for a propositional reasoner
 * for the game of Clue.  Unimplemented portions have the comment "TO
 * BE IMPLEMENTED AS AN EXERCISE".  The reasoner does not include
 * knowledge of how many cards each player holds.  See
 * http://cs.gettysburg.edu/~tneller/nsf/clue/ for details.
 *
 * @author Todd Neller
 * @version 1.0
 *

Copyright (C) 2005 Todd Neller

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

Information about the GNU General Public License is available online at:
  http://www.gnu.org/licenses/
To receive a copy of the GNU General Public License, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
02111-1307, USA.

 */

import java.io.*;
import java.util.*;

import board.Constants;

public class ClueReasoner 
{
	private int numPlayers;
	private List<String> players;
	private int playerNum;
	private int numCards;
	private int numSymbols;
	private SATSolver solver;    
	private String[] cards;
	private Sample[] particles;
	private Random r = new Random();

	public ClueReasoner(List<String> players)
	{
		this.players = players;
		numPlayers = players.size();

		// Initialize card info
		cards = new String[Constants.suspects.length + 
		                   Constants.weapons.length + 
		                   Constants.rooms.length];
		int i = 0;
		for (String card : Constants.suspects)
			cards[i++] = card;
		for (String card : Constants.weapons)
			cards[i++] = card;
		for (String card : Constants.rooms)
			cards[i++] = card;
		numCards = i;

		// Initialize solver
		solver = new SATSolver();
		addInitialClauses();
		particles = new Sample[Constants.particles];
		generateSamples();
	}

	public ClueReasoner(ClueReasoner c) {
		players = c.players;
		numPlayers = c.numPlayers;

		particles = new Sample[c.particles.length];
		for(int i = 0; i < particles.length; i++) {
			particles[i] = new Sample(c.particles[i]);
		}

		cards = new String[c.cards.length];
		for(int i = 0; i < c.cards.length; i++) {
			cards[i] = new String(c.cards[i]);
		}
		numCards = c.cards.length;

		solver = new SATSolver(c.solver);
	}

	public int numSymbols() {
		return getPairNum(getPlayerNum(Constants.caseFile), numCards - 1) + 1;
	}

	private int getPlayerNum(String player) 
	{
		if (player.equals(Constants.caseFile))
			return numPlayers;
		for (int i = 0; i < numPlayers; i++)
			if (player.equals(players.get(i)))
				return i;
		System.out.println("Illegal player: " + player);
		return -1;
	}

	private int getCardNum(String card)
	{
		for (int i = 0; i < numCards; i++)
			if (card.equals(cards[i]))
				return i;
		System.out.println("Illegal card: " + card);
		return -1;
	}

	public int getPairNum(String player, String card) 
	{
		return getPairNum(getPlayerNum(player), getCardNum(card));
	}

	private int getPairNum(int playerNum, int cardNum)
	{
		return playerNum * numCards + cardNum + 1;
	}    

	public void addInitialClauses() 
	{
		// Each card is in at least one place (including case file).
		for (int c = 0; c < numCards; c++) {
			Literal[] clause = new Literal[numPlayers + 1];
			for (int p = 0; p <= numPlayers; p++)
				clause[p] = new Literal(getPairNum(p, c), true);
			solver.addClause(clause);
		}    

		// If a card is one place, it cannot be in another place.
		for (int c = 0; c < numCards; c++) {
			for (int p1 = 0; p1 <= numPlayers; p1++) {
				for(int p2 = p1 + 1; p2 <= numPlayers; p2++) {
					Literal[] clause = new Literal[2];
					clause[0] = new Literal(getPairNum(p1, c), false);
					clause[1] = new Literal(getPairNum(p2, c), false);
					solver.addClause(clause);
				}
			}
		}

		// At least one card of each category is in the case file.
		// Suspects
		Literal[] clause = new Literal[Constants.suspects.length];
		for (int i = 0; i < Constants.suspects.length; i++) {
			clause[i] = new Literal(getPairNum(Constants.caseFile, Constants.suspects[i]), true);
		}    
		solver.addClause(clause);

		// Weapons
		clause = new Literal[Constants.weapons.length];
		for (int i = 0; i < Constants.weapons.length; i++) {
			clause[i] = new Literal(getPairNum(Constants.caseFile, Constants.weapons[i]), true);
		}    
		solver.addClause(clause);

		// Rooms
		clause = new Literal[Constants.rooms.length];
		for (int i = 0; i < Constants.rooms.length; i++) {
			clause[i] = new Literal(getPairNum(Constants.caseFile, Constants.rooms[i]), true);
		}    
		solver.addClause(clause);

		// No two cards in each category can both be in the case file.
		// Suspects
		for(int i = 0; i < Constants.suspects.length; i++) {
			for(int j = i + 1; j < Constants.suspects.length; j++) {
				clause = new Literal[2];
				clause[0] = new Literal(getPairNum(Constants.caseFile, Constants.suspects[i]), false);
				clause[1] = new Literal(getPairNum(Constants.caseFile, Constants.suspects[j]), false);
				solver.addClause(clause);
			}
		}

		// Weapons
		for(int i = 0; i < Constants.weapons.length; i++) {
			for(int j = i + 1; j < Constants.weapons.length; j++) {
				clause = new Literal[2];
				clause[0] = new Literal(getPairNum(Constants.caseFile, Constants.weapons[i]), false);
				clause[1] = new Literal(getPairNum(Constants.caseFile, Constants.weapons[j]), false);
				solver.addClause(clause);
			}
		}

		// Rooms
		for(int i = 0; i < Constants.rooms.length; i++) {
			for(int j = i + 1; j < Constants.rooms.length; j++) {
				clause = new Literal[2];
				clause[0] = new Literal(getPairNum(Constants.caseFile, Constants.rooms[i]), false);
				clause[1] = new Literal(getPairNum(Constants.caseFile, Constants.rooms[j]), false);
				solver.addClause(clause);
			}
		}
	}

	public void hand(String player, String[] cards) 
	{
		for(String card : cards) {
			for(String p : players) {
				if(p == player) continue;
				Literal[] clause = new Literal[1];
				clause[0] = new Literal(getPairNum(player, card), false);
				solver.addClause(clause);
			}
		}

		filter();
	}

	public void suggest(String suggester, String card1, String card2, 
			String card3, String refuter, String cardShown) 
	{
		if (refuter == null) {
			// For each card 1,2,3 either the suggester has it or its
			// in the case file

			Literal[] clause = new Literal[2];

			clause[0] = new Literal(getPairNum(suggester, card1), true);
			clause[1] = new Literal(getPairNum(Constants.caseFile, card1), true);
			solver.addClause(clause);

			clause = new Literal[2];
			clause[0] = new Literal(getPairNum(suggester, card2), true);
			clause[1] = new Literal(getPairNum(Constants.caseFile, card2), true);
			solver.addClause(clause);

			clause = new Literal[2];
			clause[0] = new Literal(getPairNum(suggester, card3), true);
			clause[1] = new Literal(getPairNum(Constants.caseFile, card3), true);
		}
		else {
			if(cardShown == null) {
				// The refuter must have at least one of the cards
				Literal[] clause = new Literal[3];
				clause[0] = new Literal(getPairNum(refuter, card1), true);
				clause[1] = new Literal(getPairNum(refuter, card2), true);
				clause[2] = new Literal(getPairNum(refuter, card3), true);
				solver.addClause(clause);

				// Of the refuter and all the players between
				// the refuter and the suggester, one of them
				// or the case file has the card
				List<String> betweens = playersBetween(refuter, suggester);

				// 1 for each card/
				Literal[] clause1 = new Literal[betweens.size() + 3]; // +3 (refuter, suggester, casefile)
				Literal[] clause2 = new Literal[betweens.size() + 3]; 
				Literal[] clause3 = new Literal[betweens.size() + 3]; 

				int i = 0;
				for(String plyr : betweens) {
					clause1[i] = new Literal(getPairNum(plyr, card1), true);
					clause2[i] = new Literal(getPairNum(plyr, card2), true);
					clause3[i] = new Literal(getPairNum(plyr, card3), true);
					i++;
				}

				clause1[i] = new Literal(getPairNum(refuter, card1), true);
				clause2[i] = new Literal(getPairNum(refuter, card2), true);
				clause3[i] = new Literal(getPairNum(refuter, card3), true);
				i++;

				clause1[i] = new Literal(getPairNum(suggester, card1), true);
				clause2[i] = new Literal(getPairNum(suggester, card2), true);
				clause3[i] = new Literal(getPairNum(suggester, card3), true);
				i++;

				clause1[i] = new Literal(getPairNum(Constants.caseFile, card1), true);
				clause2[i] = new Literal(getPairNum(Constants.caseFile, card2), true);
				clause3[i] = new Literal(getPairNum(Constants.caseFile, card3), true);

				solver.addClause(clause1);
				solver.addClause(clause2);
				solver.addClause(clause3);
			}
			else {
				// Of all the players between
				// the suggester and the refuter, one of them
				// or the case file has the card
				List<String> betweens = playersBetween(refuter, suggester);

				// 1 for each card
				// The clause above covers the fact that we actually
				// know where one of these cards are
				Literal[] clause1 = new Literal[betweens.size() + 3];
				Literal[] clause2 = new Literal[betweens.size() + 3]; 
				Literal[] clause3 = new Literal[betweens.size() + 3]; 

				int i = 0;
				for(String plyr : betweens) {
					clause1[i] = new Literal(getPairNum(plyr, card1), true);
					clause2[i] = new Literal(getPairNum(plyr, card2), true);
					clause3[i] = new Literal(getPairNum(plyr, card3), true);
					i++;
				}

				clause1[i] = new Literal(getPairNum(suggester, card1), true);
				clause2[i] = new Literal(getPairNum(suggester, card2), true);
				clause3[i] = new Literal(getPairNum(suggester, card3), true);
				i++;

				clause1[i] = new Literal(getPairNum(refuter, card1), true);
				clause2[i] = new Literal(getPairNum(refuter, card2), true);
				clause3[i] = new Literal(getPairNum(refuter, card3), true);
				i++;

				clause1[i] = new Literal(getPairNum(Constants.caseFile, card1), true);
				clause2[i] = new Literal(getPairNum(Constants.caseFile, card2), true);
				clause3[i] = new Literal(getPairNum(Constants.caseFile, card3), true);

				solver.addClause(clause1);
				solver.addClause(clause2);
				solver.addClause(clause3);
			}
		}
		filter();
	}

	public void accuse(String accuser, String card1, String card2, 
			String card3, boolean isCorrect)
	{
		if(!isCorrect) {
			Literal clause1[] = new Literal[1];
			Literal clause2[] = new Literal[1];
			Literal clause3[] = new Literal[1];

			clause1[0] = new Literal(getPairNum(Constants.caseFile, card1), false);
			clause2[0] = new Literal(getPairNum(Constants.caseFile, card2), false);
			clause3[0] = new Literal(getPairNum(Constants.caseFile, card3), false);
			solver.addClause(clause1);
			solver.addClause(clause2);
			solver.addClause(clause3);
		}
		else {
			// doesn't really matter, GAME OVER
		}
		filter();
	}

	// Returns a list of players after the refuter but before the suggester
	private List<String> playersBetween(String suggester, String refuter) {
		List<String> plyrs = new ArrayList<String>();

		int p = getPlayerNum(suggester);
		int r = getPlayerNum(refuter);

		int i = (r + 1) % numPlayers;
		while(i != p) {
			plyrs.add(players.get(i));
			i = (i + 1) % numPlayers;
		}

		return plyrs;
	}





	// Particle Filtering Stuff
	// We should have n valid samples after this call
	private void generateSamples() {
		for(int i = 0; i < particles.length; i++) {
			particles[i] = new Sample(players, this);
			
			while(!particles[i].Satisfies()) {	
				particles[i] = new Sample(players, this);
			}
		}
	}

	public void filter() {
		// Indices into 'particles'
		List<Integer> good = new ArrayList<Integer>();
		List<Integer> bad = new ArrayList<Integer>();

		for(int i = 0; i < particles.length; i++) {
			if(particles[i].Satisfies())
				good.add(i);
			else
				bad.add(i);
		}

		for(int i = 0; i < bad.size(); i++) {
			// Pick a random entry in good to mutate
			int range = 2;
			Sample s = particles[bad.get(i)];
			while(!s.Satisfies()) {	
				double p = r.nextDouble();
				
				if((p < 0.5) && (good.size() > 0))
					s = new Sample(particles[good.get(r.nextInt(good.size()))]);
				else
					s = new Sample(players, this);
				int x = range;
				while(x > 0) {	
					if(s.Satisfies()) 
						break;
					s.mutate();
					x--;
				}
				range *= 2;
			}			
			particles[bad.get(i)] = s;
		}
	}

	public double probability(String player, String card) {
		int count = 0;
		for(int i = 0; i < particles.length; i++) {
			Sample s = particles[i];
			List<String> h = s.getHand(player);
			if(h.contains(card))
				count++;
		}
		double p = ((double) count) / ((double) particles.length);

		// never really 0
		return Math.max(Double.MIN_VALUE, p);
	}

	// The entropy of the case file
	public double cfEntropy() {
		double H_room = 0;
		for(String room : Constants.rooms) {
			double p = probability(Constants.caseFile, room);
			H_room += p * Math.log(p);
		}

		double H_suspect = 0;
		for(String suspect : Constants.suspects) {
			double p = probability(Constants.caseFile, suspect);
			H_suspect += p * Math.log(p);
		}

		double H_weapon = 0;
		for(String weapon : Constants.weapons) {
			double p = probability(Constants.caseFile, weapon);
			H_weapon += p * Math.log(p);
		}

		double H = H_room + H_suspect + H_weapon;
		return Math.abs(H);
	}

	public boolean test(boolean[] symbols) {
		return solver.Satisfies(symbols);
	}
}