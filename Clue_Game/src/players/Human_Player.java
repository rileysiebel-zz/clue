package players;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import board.Card;
import board.Constants;
import game.Clue_Game;

 public class Human_Player extends Player {
	 private JPanel hand_panel;
		
		public Human_Player(String character) {
			super(character, true);
			
			hand_panel = new JPanel();
		}
		
		public void deal(Card t) {
			super.deal(t);
			hand_panel.add(t);
		}
		
		public JPanel getHand() {
			return hand_panel;
		}
		
		public List<Card> refute(String suspect, String weapon, String room) {
			List<Card> re = new ArrayList<Card>();
			for(Card c : hand.getCards()) {
				if(suspect.equals(c.getFace())) 
					re.add(c);
				if(weapon.equals(c.getFace()))
					re.add(c);
				if(room.equals(c.getFace()))
					re.add(c);
			}
			return re;
		}
}
