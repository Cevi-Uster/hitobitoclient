package ch.cevi.db.client.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.lang.management.ManagementFactory;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import ch.cevi.db.client.configuration.Constants;
import ch.cevi.db.client.configuration.Texts;
import ch.cevi.db.client.configuration.Version;

public class AboutDialog extends AbstractDialog {

	private static final long serialVersionUID = -1203123283502076606L;

	private JButton okButton;

	public AboutDialog(MainWindow mainWindow) {
		super(mainWindow, ModalityType.DOCUMENT_MODAL);
		this.setTitle(Texts.getTranslatedText(Texts.ABOUT_DIALOG_TITLE));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.initGui();
	}

	private void initGui() {
		CellConstraints cc = new CellConstraints();
		this.setLayout(new FormLayout("5px, left:pref:none, 5px, fill:600px, 5px", "5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:260px:none, 5px, top:pref:none, 5px"));

		this.add(new JLabel(Texts.getTranslatedText(Texts.ABOUT_DIALOG_VERSION_LABEL)), cc.xy(2, 2));

		this.add(new JLabel(Version.VERSION), cc.xy(4, 2));

		this.add(new JLabel(Texts.getTranslatedText(Texts.ABOUT_DIALOG_RELEASE_DATE_LABEL)), cc.xy(2, 4));

		this.add(new JLabel(Version.RELEASE_DATE), cc.xy(4, 4));

		this.add(new JLabel(Texts.getTranslatedText(Texts.ABOUT_DIALOG_JVM_NAME_AND_VERSION_LABEL)), cc.xy(2, 6));

		
		this.add(new JLabel(ManagementFactory.getRuntimeMXBean().getVmName() + " " + ManagementFactory.getRuntimeMXBean().getVmVersion()), cc.xy(4, 6));
		
		
		this.add(new JLabel(Texts.getTranslatedText(Texts.ABOUT_DIALOG_LICENSE_LABEL)), cc.xy(2, 8));

		JTextArea licenseTextArea = new JTextArea(Constants.ABOUT_DIALOG_LICENSE);
		licenseTextArea.setWrapStyleWord(true);
		licenseTextArea.setLineWrap(true);
		licenseTextArea.setEditable(false);
		licenseTextArea.setEnabled(true);
		this.add(licenseTextArea, cc.xyw(2, 10, 3));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton(new OkAction());
		buttonPanel.add(okButton);
		this.add(buttonPanel, cc.xyw(2, 12, 3));
		this.getRootPane().setDefaultButton(okButton);
		this.pack();
		this.setResizable(false);
	}

	class OkAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public OkAction() {
			super(Texts.getTranslatedText(Texts.ABOUT_DIALOG_OK_BUTTON_LABEL));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			AboutDialog.this.dispatchEvent(new WindowEvent(AboutDialog.this, WindowEvent.WINDOW_CLOSING));
		}

	}
}
