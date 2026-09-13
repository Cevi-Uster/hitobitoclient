package ch.cevi.db.client.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.StyleRange;
import com.jidesoft.swing.StyledLabel;

import ch.cevi.db.client.configuration.Configuration;
import ch.cevi.db.client.configuration.Configuration.BaseUrl;
import ch.cevi.db.client.configuration.Texts;

public class ConnectDialog extends AbstractDialog {

	private static final long serialVersionUID = -1203123283502076606L;

	private MainWindow mainWindow;

	private Configuration configuration;

	private String errorMessage;

	private JComboBox<BaseUrl> baseUrlComboBox;

	private JCheckBox usePrimaryGroupAsRootCheckBox;

	private JButton okButton;

	private JButton cancelButton;

	public ConnectDialog(MainWindow mainWindow, Configuration configuration) {
		this(mainWindow, configuration, null);
	}

	public ConnectDialog(MainWindow mainWindow, Configuration configuration, String errorMessage) {
		super(mainWindow, ModalityType.DOCUMENT_MODAL);
		this.mainWindow = mainWindow;
		this.errorMessage = errorMessage;
		this.setTitle(Texts.getTranslatedText(Texts.CONNECT_DIALOG_TITLE));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.configuration = configuration;
		this.initGui();
	}

	@SuppressWarnings("unchecked")
	private void initGui() {
		CellConstraints cc = new CellConstraints();
		this.setLayout(new FormLayout("5px, left:pref:none, 5px, fill:280px:grow, 5px",
				"5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px"));
		int row = 2;

		if (errorMessage != null) {
			StyledLabel errorMessageLabel = new StyledLabel(errorMessage, StyledLabel.CENTER);
			errorMessageLabel.addStyleRange(new StyleRange(Color.RED));
			this.add(errorMessageLabel, cc.xyw(2, row, 3));
			row += 2;
		}

		ValueModel baseUrlValueModel = new PropertyAdapter<Configuration>(configuration, Configuration.BASE_URL_PROPERTY, true);
		baseUrlComboBox = new JComboBox<BaseUrl>();
		baseUrlComboBox.setModel(new ComboBoxAdapter<BaseUrl>(Configuration.BaseUrl.values(), baseUrlValueModel));
		baseUrlComboBox.setSelectedItem(BaseUrl.Live);
		this.add(new JLabel(Texts.getTranslatedText(Texts.CONNECT_DIALOG_BASE_URL_LABEL)), cc.xy(2, row));
		this.add(baseUrlComboBox, cc.xy(4, row));
		row += 2;

		this.add(new JLabel(Texts.getTranslatedText(Texts.CONNECT_DIALOG_OAUTH_INFO_LABEL)), cc.xyw(2, row, 3));
		row += 2;

		ValueModel usePrimaryGroupAsRootValueModel = new PropertyAdapter<Configuration>(configuration, Configuration.USE_PRIMARY_GROUP_AS_ROOT_PROPERY, true);
		usePrimaryGroupAsRootCheckBox = BasicComponentFactory.createCheckBox(usePrimaryGroupAsRootValueModel, Texts.getTranslatedText(Texts.CONNECT_DIALOG_USE_PRIMARY_GROUP_AS_ROUTE));
		this.add(usePrimaryGroupAsRootCheckBox, cc.xy(4, row));
		row += 2;

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton(new OkAction());
		cancelButton = new JButton(new CancelAction());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		this.add(buttonPanel, cc.xyw(2, row, 3));
		this.getRootPane().setDefaultButton(okButton);
		this.pack();
	}

	class OkAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public OkAction() {
			super(Texts.getTranslatedText(Texts.CONNECT_DIALOG_OK_BUTTON_LABEL));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mainWindow.getMainController().connect();
			ConnectDialog.this.dispatchEvent(new WindowEvent(ConnectDialog.this, WindowEvent.WINDOW_CLOSING));
		}

	}

	class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CancelAction() {
			super(Texts.getTranslatedText(Texts.CONNECT_DIALOG_CANCEL_BUTTON_LABEL));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ConnectDialog.this.dispatchEvent(new WindowEvent(ConnectDialog.this, WindowEvent.WINDOW_CLOSING));
		}

	}
}
