package board;

public class Constants {
	public static final int particles = 2;
	public static final double precision = 0.1;
	public static final double discount = 0.95;
	public static final int SQUARE_SIZE = 25;
	public static final int BOARD_X = 24;
	public static final int BOARD_Y = 25;
	public static final long MAX_TIME = 60000;
	
	public static final String caseFile = "cf";
	public static final String[] players = {"Miss Scarlet", "Colonel Mustard", 
		"Mr. Green", "Ms. White", "Ms. Peacock", "Professor Plum"};
	public static final String[] suspects = {"Colonel Mustard", "Professor Plum", "Ms. Peacock", "Mr. Green", "Miss Scarlet", "Ms. White"};
	
	public static final String[] weapons = {"Knife", "Candlestick", "Revolver",
		"Rope", "Lead Pipe", "Wrench"};
	public static final String[] rooms = {"Hall", "Lounge", "Dining Room", 
		"Kitchen", "Ballroom", "Conservatory", "Billiard Room", "Library", 
		"Study"};
	

	
	public static final String wdir = "/Users/riley/Desktop/Princeton/Thesis/CLueGame/src/";
	
	// Card Types
	public static final int SUSPECT = 1;
	public static final int WEAPON = 2;
	public static final int ROOM = 3;
	
	//Room Designations
	public static final int BLOCKED = -1;
	public static final int HALL = 0;
	public static final int LOUNGE = 1;
	public static final int DINING_ROOM = 2;
	public static final int KITCHEN = 3;
	public static final int BALLROOM = 4;
	public static final int CONSERVATORY = 5;
	public static final int BILLIARD_ROOM = 6;
	public static final int LIBRARY = 7;
	public static final int STUDY = 8;
	public static final int SPACE = 9;
	
	// Characters & Suspects
	public static final int DEFAULT = 0; // Used to show that a space has NO character in it
	public static final int SCARLET = 1;
	public static final int MUSTARD = 2;
	public static final int WHITE = 3;
	public static final int GREEN = 4;
	public static final int PEACOCK = 5;
	public static final int PLUM = 6;
}
