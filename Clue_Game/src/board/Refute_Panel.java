package board;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


public class Refute_Panel extends JPanel {
	ButtonGroup bg;

	public Refute_Panel(List<Card> cards) {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.bg = new ButtonGroup();
		
		for(Card c : cards) {
			JRadioButton rb = new JRadioButton(c.getFace());
			bg.add(rb);
			this.add(rb);
		} 
	}	
	
	public String getRefutation() {
		String refute;
		for(Enumeration<AbstractButton> e = bg.getElements(); e.hasMoreElements(); ) {
			JRadioButton rb = (JRadioButton) e.nextElement();
			if(rb.isSelected()) {
				refute = rb.getText();
				return refute;
			}
		}
		return null;
	}
}
