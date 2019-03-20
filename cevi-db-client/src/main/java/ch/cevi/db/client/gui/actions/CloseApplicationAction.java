package ch.cevi.db.client.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;

import ch.cevi.db.client.configuration.Texts;
import ch.cevi.db.client.gui.MainWindow;

public class CloseApplicationAction extends AbstractAction {

	private static final long serialVersionUID = 7720672914222472305L;

	private MainWindow mainWindow;

	public CloseApplicationAction(MainWindow mainWindow) {
		super(Texts.getTranslatedText(Texts.MENU_FILE_MENU_EXIT_ITEM));
		this.mainWindow = mainWindow;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
	}

}
