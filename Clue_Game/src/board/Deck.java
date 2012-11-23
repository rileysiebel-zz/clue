package board;

import java.util.List;

import players.Player;

public class Deck {
	private Card[] suspects;
	private Card[] weapons;
	private Card[] rooms;

	private int sIndex;
	private int wIndex;
	private int rIndex;
	public Deck() {
		this.sIndex = this.wIndex = this.rIndex = 0;
		
		init();
		shuffle();
	}
	
	public Card getSuspect() {
		if(sIndex >= suspects.length)
			return null;
		return suspects[sIndex++];
	}
	
	public Card getWeapon() {
		if(wIndex >= weapons.length)
			return null;
		return weapons[wIndex++];
	}
	
	public Card getRoom() {
		if(rIndex >= rooms.length)
			return null;
		return rooms[rIndex++];
	}

	private void init() {
		suspects = new Card[Constants.suspects.length];
		int i = 0;
		for(String sus : Constants.suspects) {
			suspects[i] = new Card(Constants.SUSPECT, sus);
			i++;
		}
		
		weapons = new Card[Constants.weapons.length];
		i = 0;
		for(String wep : Constants.weapons) {
			weapons[i] = new Card(Constants.WEAPON, wep);
			i++;
		}

		rooms = new Card[Constants.rooms.length];
		i = 0;
		for(String rom : Constants.rooms) {
			rooms[i] = new Card(Constants.ROOM, rom);
			i++;
		}
	}

	private void shuffle() {
		for ( int i = 0; i < suspects.length; i++ )   {
			int j = ( int ) ( Math.random() * suspects.length );
			Card temp = suspects[ i ]; // swap
			suspects[ i ] = suspects[ j ]; // the
			suspects[ j ] = temp; // cards
		}

		for ( int i = 0; i < weapons.length; i++ )   {
			int j = ( int ) ( Math.random() * weapons.length );
			Card temp = weapons[ i ]; // swap
			weapons[ i ] = weapons[ j ]; // the
			weapons[ j ] = temp; // cards
		}

		for ( int i = 0; i < rooms.length; i++ )   {
			int j = ( int ) ( Math.random() * rooms.length );
			Card temp = rooms[ i ]; // swap
			rooms[ i ] = rooms[ j ]; // the
			rooms[ j ] = temp; // cards
		}
	}
}
