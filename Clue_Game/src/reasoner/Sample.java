package reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Iterator;

import board.Card;
import board.Constants;
import board.Deck;

public class Sample {
	ClueReasoner cr;
	Map<String, List<String>> hands;
	Random r;

	public Sample(Sample s) {
		this.cr = s.cr;
		this.r = new Random();

		this.hands = new HashMap<String, List<String>>();
		for(int i = 0; i < s.hands.size(); i++) {
			List<String> temp = new ArrayList<String>();

			List<String> old = s.hands.get(s.hands.keySet().toArray()[i]);
			for(int j = 0; j < old.size(); j++) {
				temp.add(new String(old.get(j)));
			}
			hands.put((String) (s.hands.keySet().toArray()[i]), temp);
		}
	}	

	public Sample(List<String> players, ClueReasoner cr) {
		this.cr = cr;
		this.hands = new HashMap<String, List<String>>();
		this.r = new Random();
		int numplayers = players.size();

		// Sample for Casefile
		Deck dk = new Deck();
		List<String> cf = new ArrayList<String>();
		hands.put(Constants.caseFile, cf);
		hands.get(Constants.caseFile).add(dk.getRoom().getFace());
		hands.get(Constants.caseFile).add(dk.getSuspect().getFace());
		hands.get(Constants.caseFile).add(dk.getWeapon().getFace());

		for(int i = 0; i < numplayers; i++) {
			List<String> temp = new ArrayList<String>();
			hands.put(players.get(i), temp);
		}
		int player_index = 0;
		Card temp;
		while((temp = dk.getSuspect()) != null) {
			hands.get(players.get(player_index)).add(temp.getFace());
			player_index = (player_index + 1) % numplayers;
		}
		while((temp = dk.getRoom()) != null) {
			hands.get(players.get(player_index)).add(temp.getFace());
			player_index = (player_index + 1) % numplayers;
		}
		while((temp = dk.getWeapon()) != null) {
			hands.get(players.get(player_index)).add(temp.getFace());
			player_index = (player_index + 1) % numplayers;
		}
	}

	public List<String> getHand(String player) {
		return hands.get(player);
	}

	public boolean Satisfies() {
		// Turn the sample into an array of booleans'
		boolean symbols[] = new boolean[cr.numSymbols()];
		for(String player : hands.keySet()) {
			List<String> hand = getHand(player);
			for(String card : hand) {
				symbols[cr.getPairNum(player, card)] = true;
			}
		}
		return cr.test(symbols);
	}

	public void mutate() {
		// Pick 2 random players
		String player1 = null;
		String player2 = null;

		int item1 = r.nextInt(hands.keySet().size());
		int item2 = r.nextInt(hands.keySet().size());
		while(item2 == item1)
			item2 = r.nextInt(hands.keySet().size());

		int i = 0;
		String[] keys = hands.keySet().toArray(new String[0]);
		player1 = keys[item1];
		player2 = keys[item2];

		item1 = r.nextInt(hands.get(player1).size());
		item2 = r.nextInt(hands.get(player2).size());

		String temp = hands.get(player1).get(item1);
		String temp2 = hands.get(player2).get(item2);

		hands.get(player1).remove(item1);
		hands.get(player2).remove(item2);

		hands.get(player1).add(temp2);
		hands.get(player2).add(temp);
	}
}
