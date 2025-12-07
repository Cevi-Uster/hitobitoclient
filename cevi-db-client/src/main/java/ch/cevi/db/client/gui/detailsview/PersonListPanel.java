package ch.cevi.db.client.gui.detailsview;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import ch.cevi.db.client.business.entities.YGroup;

public class PersonListPanel extends JPanel {

	private static final long serialVersionUID = 6850729853416191295L;

	private PersonListTableModel personListTableModel;

	private PersonListTable personListTable;

	public PersonListPanel() {
		this.personListTableModel = new PersonListTableModel();
		this.personListTable = new PersonListTable(personListTableModel);
	}

	public void initAndBindGui(YGroup currentGroup) {
		this.removeAll();
		this.setLayout(new FormLayout("5px, fill:200px:grow, 5px", "5px, fill:50px:grow, 5px"));
		CellConstraints cc = new CellConstraints();

		personListTableModel.setGroup(currentGroup);
		JScrollPane personListScrollPane = new JScrollPane();
		personListScrollPane.getViewport().add(personListTable);
		this.add(personListScrollPane, cc.xy(2, 2));

		this.revalidate();
		this.repaint();
	}

}
