package board;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Card extends JPanel {
	private int type;
	private String face;
	
	public Card(Card c) {
		new Card(c.type, c.getFace());
	}
	
	public Card(int type, String face) {
		this.type = type;
		this.setFace(face);
		
		this.add(new JLabel(Integer.toString(type)));
		this.add(new JLabel(face));
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getFace() {
		return face;
	}
}
