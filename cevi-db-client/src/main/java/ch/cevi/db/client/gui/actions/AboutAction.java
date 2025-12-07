package ch.cevi.db.client.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ch.cevi.db.client.configuration.Texts;
import ch.cevi.db.client.gui.AboutDialog;
import ch.cevi.db.client.gui.MainWindow;

public class AboutAction extends AbstractAction {

	private static final long serialVersionUID = 7720672914222472305L;

	private MainWindow mainWindow;

	public AboutAction(MainWindow mainWindow) {
		super(Texts.getTranslatedText(Texts.MENU_HELP_MENU_ABOUT_ITEM));
		this.mainWindow = mainWindow;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AboutDialog connectDialog = new AboutDialog(mainWindow);
		connectDialog.setLocationRelativeTo(mainWindow);
		connectDialog.setVisible(true);
	}

}
