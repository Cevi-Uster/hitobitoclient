package ch.cevi.db.client.gui.detailsview;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.communication.ServerFacade;
import ch.cevi.db.client.configuration.Texts;
import ch.cevi.db.client.gui.MainWindow;

public class DetailsTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 7768716023855682730L;

	private YGroup currentGroup;

	private GroupDetailsPanel groupDetailsPanel;

	private PersonListPanel personListPanel;

	private ServerFacade serverFacade;

	private MainWindow mainWindow;

	public DetailsTabbedPane(MainWindow mainWindow, ServerFacade serverFacade) {
		this.serverFacade = serverFacade;
		this.mainWindow = mainWindow;
		initGui();
	}

	private void initGui() {
		JScrollPane groupDetailsScrollPane = new JScrollPane(createGroupDetailsPanel());
		groupDetailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		groupDetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.addTab(Texts.getTranslatedText(Texts.TAB_TITLE_GROUP_DETAILS), groupDetailsScrollPane);
		
		JScrollPane personListScrollPane = new JScrollPane(createPersonListPanel());
		personListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		personListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.addTab(Texts.getTranslatedText(Texts.TAB_TITLE_PERSON_LIST), personListScrollPane);
	}

	private JPanel createGroupDetailsPanel() {
		groupDetailsPanel = new GroupDetailsPanel(mainWindow, serverFacade);
		return groupDetailsPanel;
	}

	private JPanel createPersonListPanel() {
		personListPanel = new PersonListPanel();
		return personListPanel;
	}

	public YGroup getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(YGroup currentGroup) {
		this.currentGroup = currentGroup;
		groupDetailsPanel.initAndBindGui(currentGroup);
		personListPanel.initAndBindGui(currentGroup);
	}

}
