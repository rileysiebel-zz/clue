package game.start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PlayerPanel extends JPanel implements ActionListener{
	private JButton browse;
	private JComboBox player_interface;
	private final String[] possible_interfaces = {"Human", "Computer", "Not Playing"};

	private String name, path;

	public PlayerPanel(String nm, int player) {
		this.name = nm;
		
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		JPanel temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.LINE_AXIS));

		JLabel name = new JLabel();
		name.setText(nm);

		player_interface = new JComboBox(possible_interfaces);
		player_interface.addActionListener(this);
		player_interface.setActionCommand("PATH");
		this.path = "Human";

		temp.add(name);
		temp.add(Box.createHorizontalGlue());
		temp.add(player_interface);

		this.add(name);
		this.add(temp);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("PATH")) {
			this.path = (String) player_interface.getSelectedItem();
		}
	}

	public String getPlayerName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

}

