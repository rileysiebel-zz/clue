package game.start;
import game.Clue_Game;

import java.util.Arrays;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import board.Constants;


public class Start_Screen extends JPanel{
	
	private JPanel center;
	private JButton start;

	int num_players;
	private List<String> players;
	private List<String> interfaces;

	public Start_Screen() {
		
		this.setLayout(new BorderLayout());

		this.players = new ArrayList<String>();
		this.interfaces = new ArrayList<String>();

		center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
		for(int i = 0; i < 6; i++) {
			center.add(new JSeparator(SwingConstants.HORIZONTAL));
			center.add(new PlayerPanel(Constants.players[i], i));
		}

		this.start = new JButton();
		start.setText("Start Game");
		start.addActionListener(Clue_Game.getSingletonObject());
		start.setActionCommand("START");
		
		this.add(center, BorderLayout.CENTER);
		this.add(start, BorderLayout.PAGE_END);
	}
	// Makes sure not two players have the same character
	// As a side effect fills in the players and file_paths lists
	public boolean checkInfo() {
		int np = 0;
		for(int i = 1; i < center.getComponentCount(); i+=2) {
			PlayerPanel playerPanel = (PlayerPanel) center.getComponent(i);
			
			if(playerPanel.getPath().equals("Not Playing"))
				np++;
			else {
				players.add(playerPanel.getPlayerName());
				interfaces.add(playerPanel.getPath());
			}
			
			if(np > 3) {
				players.clear();
				interfaces.clear();
				return false;
			}
		}
		return true;
	}
	
	public List<String> getCharacters() {
		return players;
	}
	
	public List<String> getInterfaces() {
		return interfaces;
	}
}

