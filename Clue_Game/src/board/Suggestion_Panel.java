package board;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class Suggestion_Panel extends JPanel {
	JComboBox suspects, weapons;
	String room;
	
	public Suggestion_Panel(int r) {
		this.room = Constants.rooms[r];
		this.suspects = new JComboBox(Constants.players);
		this.weapons = new JComboBox(Constants.weapons);
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel row1 = new JPanel();
		row1.setAlignmentX(LEFT_ALIGNMENT);
		row1.setLayout(new BoxLayout(row1, BoxLayout.LINE_AXIS));
		row1.add(new JLabel("It was "));
		row1.add(suspects);
		
		JPanel row2 = new JPanel();
		row2.setAlignmentX(LEFT_ALIGNMENT);
		row2.setLayout(new BoxLayout(row2, BoxLayout.LINE_AXIS));
		row2.add(new JLabel("with the "));
		row2.add(weapons);
				
		JPanel row3 = new JPanel();
		row3.setAlignmentX(LEFT_ALIGNMENT);
		row3.setLayout(new BoxLayout(row3, BoxLayout.LINE_AXIS));
		row3.add(new JLabel("in the " + this.room + "."));
		
		this.add(row1);
		this.add(row2);
		this.add(row3);
	}
	
	public String getSuspect() {
		return (String) suspects.getSelectedItem();
	}
	
	public String getWeapon() {
		return (String) weapons.getSelectedItem();
	}

	public String getRoom() {
		return room;
	}
}
