package game;

import game.start.Start_Screen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import players.AIPlayer;
import players.Human_Player;
import players.Player;

import board.Card;
import board.Clue_Board;
import board.Constants;
import board.Deck;

public class Clue_Game extends JFrame implements ActionListener {
	private Clue_Board board;
	private Start_Screen start;

	private List<Player> players;

	private Clue_Game() {
		this.setTitle("Clue by Riley");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		this.players = new ArrayList<Player>();
	}

	public static Clue_Game getSingletonObject()
	{
		if (ref == null)
			ref = new Clue_Game();
		return ref;
	}

	private static Clue_Game ref;


	private void initBoard() {
		for(int i = 0; i < start.getCharacters().size(); i++) {
			if(start.getInterfaces().get(i).equals("Human")) {
				players.add(new Human_Player(start.getCharacters().get(i)));
			}
			else if(start.getInterfaces().get(i).equals("Computer")) {
				players.add(new AIPlayer(start.getCharacters().get(i), start.getCharacters()));
			}
			else {
				//players.add(new Player(start.getCharacters().get(i), false));
			}
		}
		board = new Clue_Board(players);

		this.remove(start);
		this.setMinimumSize(board.getMinimumSize());
		this.setMaximumSize(board.getMaximumSize());
		this.setPreferredSize(board.getPreferredSize());
		this.setSize(board.getSize());

		this.getContentPane().removeAll();
		this.add(board, BorderLayout.CENTER);
		//	this.add(players.get(current_player).getControlPanel(), BorderLayout.PAGE_END);
		this.pack();
		board.play();
	}

	private void start() {
		start = new Start_Screen();

		this.add(start);
		this.pack();
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("START")) {
			if(start.checkInfo() == true) {
				initBoard();
			}
			else {
				JOptionPane.showMessageDialog(this,
						"Make Sure Each Player Selects a Different Character\n" +
						"And Each Player Selects An Interface File.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}

		}		
	}

	public static void main(String[] args) {

		Clue_Game game = getSingletonObject();
		game.start();

	}
}
