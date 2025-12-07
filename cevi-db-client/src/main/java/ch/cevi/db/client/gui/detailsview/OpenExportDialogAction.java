package ch.cevi.db.client.gui.detailsview;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.communication.ServerFacade;
import ch.cevi.db.client.configuration.Texts;
import ch.cevi.db.client.gui.ExportDialog;
import ch.cevi.db.client.gui.MainWindow;

/**
 * Action to export the selected group and all subgroups as CSV file.
 * 
 * @author developer
 *
 */
public class OpenExportDialogAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private MainWindow mainWindow;

	private YGroup currentGroup;

	private ServerFacade serverFacade;

	public OpenExportDialogAction(MainWindow mainWindow, ServerFacade serverFacade, YGroup currentGroup) {
		super(Texts.getTranslatedText(Texts.GROUP_DETAILS_EXPORT_DIALOG_ACTION));
		this.mainWindow = mainWindow;
		this.currentGroup = currentGroup;
		this.serverFacade = serverFacade;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ExportDialog exportDialog = new ExportDialog(mainWindow, currentGroup, serverFacade);
		exportDialog.setVisible(true);
	}

}
