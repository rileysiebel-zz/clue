package board;

import java.util.ArrayList;
import java.util.List;

public class Hand {
	private List<Card> hand;

	public Hand() {
		this.hand = new ArrayList<Card>();
	}

	public void deal(Card c) {
		this.hand.add(c);
	}

	public int size() {
		return this.hand.size();
	}

	public List<Card> getCards() {
		return hand;
	}

}
