package board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import board.Clue_Board;
import board.Constants;

public class Space extends JPanel {
	int xPos;
	int yPos;
	int type;
	boolean reachable;

	private int player;

	public enum Direction {NONE, LEFT, RIGHT, DOWN, UP};
	public boolean door;

	List<Space> neighbors;

	public Space(int x, int y, int type) {
		this.xPos = x;
		this.yPos = y;
		this.type = type;
		this.neighbors = new ArrayList<Space>();

		this.player = Constants.DEFAULT;

		Dimension size = new Dimension(Constants.SQUARE_SIZE, Constants.SQUARE_SIZE);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setPreferredSize(size);
		this.setSize(size);

		this.reachable = false;
		this.door = false;
	}

	// Must be called after all neighbors are initialized
	public void initPanel() {

		this.setMinimumSize(new Dimension(Constants.SQUARE_SIZE, Constants.SQUARE_SIZE));
		Color color = Color.white;
		switch(this.type) {
		case Constants.STUDY: color = Color.blue; break;
		case Constants.HALL: color = Color.green; break;
		case Constants.LOUNGE: color = Color.cyan; break;
		case Constants.DINING_ROOM: color = Color.yellow; break;
		case Constants.KITCHEN: color = Color.gray; break;
		case Constants.BALLROOM: color = Color.orange; break;
		case Constants.CONSERVATORY: color = Color.pink; break;
		case Constants.BILLIARD_ROOM: color = Color.darkGray; break;
		case Constants.LIBRARY: color = Color.magenta; break;
		case Constants.SPACE: {
			color = Color.white; 
			this.setBorder(new LineBorder(Color.black)); 
			break;
		}
		case Constants.BLOCKED: color = Color.black; break;
		default: break;
		}
		if(this.door == true)
			color = Color.red;

		this.setBackground(color);
	}

	public int getType() {
		return this.type;
	}

	public void addNeighbor(Space s) {
		if(this.type == Constants.SPACE) {
			if(s.getNeighbors().contains(this))
				if((s.type != Constants.SPACE) && (s.type != Constants.BLOCKED)) {
					s.door = true;
				}
		}
		this.neighbors.add(s);
	}

	public List<Space> getNeighbors()	 {
		return this.neighbors;
	}


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(this.player == Constants.DEFAULT)
			return;

		//		g.setColor(Color.black);
		//		g.drawOval(this.getSize().width / 4, this.getSize().height / 4, 
		//				this.getSize().width / 2, this.getSize().height / 2);
		switch (this.player) {
		case Constants.PLUM:
			g.setColor(Color.pink);
			g.fillOval(this.getSize().width / 4, this.getSize().height / 4, 
					this.getSize().width / 2, this.getSize().height / 2);
			g.setColor(Color.black);
			g.drawOval(this.getSize().width / 4, this.getSize().height / 4,
					this.getSize().width / 2, this.getSize().height / 2);
			//			g.setColor(Color.black);
			//			g.drawString("P", this.getSize().width / 4, this.getSize().height / 2);
			break;
		case Constants.WHITE:
			g.setColor(Color.gray);
			g.fillOval(this.getSize().width / 4, this.getSize().height / 4, 
					this.getSize().width / 2, this.getSize().height / 2);
			g.setColor(Color.black);
			g.drawOval(this.getSize().width / 4, this.getSize().height / 4,
					this.getSize().width / 2, this.getSize().height / 2);
			break;
		case Constants.SCARLET:
			g.setColor(Color.red);
			g.fillOval(this.getSize().width / 4, this.getSize().height / 4, 
					this.getSize().width / 2, this.getSize().height / 2);
			g.setColor(Color.black);
			g.drawOval(this.getSize().width / 4, this.getSize().height / 4,
					this.getSize().width / 2, this.getSize().height / 2);
			break;
		case Constants.MUSTARD:
			g.setColor(Color.orange);
			g.fillOval(this.getSize().width / 4, this.getSize().height / 4, 
					this.getSize().width / 2, this.getSize().height / 2);
			g.setColor(Color.black);
			g.drawOval(this.getSize().width / 4, this.getSize().height / 4,
					this.getSize().width / 2, this.getSize().height / 2);
			break;
		case Constants.GREEN:
			g.setColor(Color.green);
			g.fillOval(this.getSize().width / 4, this.getSize().height / 4, 
					this.getSize().width / 2, this.getSize().height / 2);
			g.setColor(Color.black);
			g.drawOval(this.getSize().width / 4, this.getSize().height / 4,
					this.getSize().width / 2, this.getSize().height / 2);
			break;
		case Constants.PEACOCK:
			g.setColor(Color.blue);
			g.fillOval(this.getSize().width / 4, this.getSize().height / 4, 
					this.getSize().width / 2, this.getSize().height / 2);
			g.setColor(Color.black);
			g.drawOval(this.getSize().width / 4, this.getSize().height / 4,
					this.getSize().width / 2, this.getSize().height / 2);
			
			break;
		default:
			break;
		}
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public void setPlayer(String character) {

		if (character.equals("Colonel Mustard")) {
			this.player = Constants.MUSTARD;
		}
		else if(character.equals("Miss Scarlet")) {
			this.player = Constants.SCARLET;
		}
		else if(character.equals("Professor Plum")) {
			this.player = Constants.PLUM;
		}
		else if(character.equals("Mr. Green")) {
			this.player = Constants.GREEN;
		}
		else if(character.equals("Ms. White")) {
			this.player = Constants.WHITE;
		}
		else if(character.equals("Ms. Peacock")) {
			this.player = Constants.PEACOCK;
		}
		else if (character.equals("")){
			this.player = Constants.DEFAULT;
		}
	}


	public boolean isReachable() {
		return reachable;
	}

	public void setReachable(boolean reachable) {
		this.reachable = reachable;
		if(reachable == true)
			this.setBorder(new LineBorder(Color.green));
		else
			this.setBorder(new LineBorder(Color.black));
	}
	
	public int getXPos() {
		return xPos;
	}
	
	public int getYPos() {
		return yPos;
	}
}
