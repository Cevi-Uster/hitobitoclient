package ch.cevi.db.client.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ch.cevi.db.client.configuration.Configuration;
import ch.cevi.db.client.configuration.Texts;
import ch.cevi.db.client.gui.ConnectDialog;
import ch.cevi.db.client.gui.MainWindow;

public class ConnectAction extends AbstractAction {

	private static final long serialVersionUID = 7720672914222472305L;

	private MainWindow mainWindow;

	private Configuration configuration;

	public ConnectAction(MainWindow mainWindow, Configuration configuration) {
		super(Texts.getTranslatedText(Texts.MENU_FILE_MENU_CONNECT_ITEM));
		this.mainWindow = mainWindow;
		this.configuration = configuration;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ConnectDialog connectDialog = new ConnectDialog(mainWindow, configuration);
		connectDialog.setLocationRelativeTo(mainWindow);
		connectDialog.setVisible(true);
	}

}
