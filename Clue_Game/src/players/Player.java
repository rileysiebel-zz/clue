package players;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import board.Card;
import board.Constants;
import board.Hand;
import game.Clue_Game;

public class Player {
	protected int x;
	protected int y;
	protected String character;
	protected int player_num;
	private boolean moved;
	private boolean playing;

	protected Hand hand;

	public Player(String character, boolean playing) {
		this.character = character;
		this.playing = playing;
		
		if(character.equals("Colonel Mustard")) {
			this.player_num = 1;
			this.setX(23);
			this.setY(7);
		}
		else if(character.equals("Miss Scarlet")) {
			this.player_num = 0;
			this.setX(23);
			this.setY(17);
		}
		else if(character.equals("Professor Plum")) {
			this.player_num = 5;
			this.setX(0);
			this.setY(5);
		}
		else if(character.equals("Mr. Green")) {
			this.player_num = 3;
			this.setX(9);
			this.setY(24);
		}
		else if(character.equals("Ms. White")) {
			this.player_num = 2;
			this.setX(14);
			this.setY(24);
		}
		else if(character.equals("Ms. Peacock")) {
			this.player_num = 4;
			this.setX(0);
			this.setY(18);
		}
		else {
			// WTF
		}
		this.hand = new Hand();
		this.moved = false;
	}

	public void deal(Card temp) {
		hand.deal(temp);		
	}

	public boolean canRefute(String suspect, String weapon, String room) {
		for(Card c : hand.getCards()) {
			if(suspect.equals(c.getFace()))
				return true;
			if(weapon.equals(c.getFace()))
				return true;
			if(room.equals(c.getFace()))
				return true;
		}
		return false;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public String getCharacter() {
		return character;
	}
	
	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	
	public boolean isMoved() {
		return moved; 
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}
}
