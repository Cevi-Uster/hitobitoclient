package ch.cevi.db.client.gui.mainmenu;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ch.cevi.db.client.configuration.Texts;

public class MainMenuBar extends JMenuBar {

	private static final long serialVersionUID = 3277922362768684545L;

	JMenu fileMenu;

	JMenu helpMenu;

	JMenuItem exitItem;

	JMenuItem connectItem;

	JMenuItem aboutItem;

	public MainMenuBar() {
		buildFileMenu();
		buildHelpMenu();
	}

	private void buildFileMenu() {
		this.fileMenu = new JMenu(Texts.getTranslatedText(Texts.MENU_FILE_MENU_TITLE));
		this.add(fileMenu);

		connectItem = new JMenuItem();
		fileMenu.add(connectItem);

		exitItem = new JMenuItem();
		fileMenu.add(exitItem);
	}

	private void buildHelpMenu() {
		this.helpMenu = new JMenu(Texts.getTranslatedText(Texts.MENU_HELP_MENU_TITLE));
		this.add(helpMenu);

		aboutItem = new JMenuItem();
		helpMenu.add(aboutItem);

	}

	public void setExitAction(Action exitAction) {
		this.exitItem.setAction(exitAction);
	}

	public void setConnectAction(Action connectAction) {
		this.connectItem.setAction(connectAction);
	}

	public void setAboutAction(Action aboutAction) {
		this.aboutItem.setAction(aboutAction);
	}
}
