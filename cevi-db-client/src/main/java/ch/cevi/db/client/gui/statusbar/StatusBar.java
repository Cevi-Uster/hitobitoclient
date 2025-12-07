package ch.cevi.db.client.gui.statusbar;

import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StatusBar extends JPanel {
	
	public StatusBar() {
		initGui();
	}
	
	private void initGui() {
		FormLayout layout = new FormLayout("5px, left:0px:none, 5px, right:pref:grow, 5px", "pref, 2px");
		CellConstraints cc = new CellConstraints();
		this.setLayout(layout);
		this.add(new MemoryPanelProgressBar(), cc.xy(4, 1));
	}
}
