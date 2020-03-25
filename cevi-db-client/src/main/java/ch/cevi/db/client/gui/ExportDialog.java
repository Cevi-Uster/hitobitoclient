package ch.cevi.db.client.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import ch.cevi.db.client.business.LoadingListner;
import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.business.export.CSVExporter;
import ch.cevi.db.client.business.export.Excel97Exporter;
import ch.cevi.db.client.business.export.ExportDescription;
import ch.cevi.db.client.business.export.GeneralExporter;
import ch.cevi.db.client.communication.ServerFacade;
import ch.cevi.db.client.configuration.Texts;

public class ExportDialog extends AbstractDialog {

	private static final long serialVersionUID = -1203123283502076606L;

	private MainWindow mainWindow;

	private ExportDescription exportDescription;

	private JComboBox<ExportDescription.ExportFormat> exportFormatComboBox;

	private JTextField pathTextField;

	private JButton browseButton;

	private JButton okButton;

	private JButton cancelButton;

	private YGroup currentGroup;

	private ServerFacade serverFacade;

	public ExportDialog(MainWindow mainWindow, YGroup currentGroup, ServerFacade serverFacade) {
		super(mainWindow, ModalityType.DOCUMENT_MODAL);
		this.mainWindow = mainWindow;
		this.currentGroup = currentGroup;
		this.serverFacade = serverFacade;
		this.setTitle(Texts.getTranslatedText(Texts.EXPORT_DIALOG_TITLE));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.exportDescription = new ExportDescription(true);
		this.initGui();
	}

	private void initGui() {
		CellConstraints cc = new CellConstraints();
		this.setLayout(new FormLayout("5px, fill:pref:none, 5px", "5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px"));

		this.add(createGeneralSelectionPanel(), cc.xy(2, 2));

		this.add(createAttributeSelectionPanel(), cc.xy(2, 4));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton(new OkAction());
		cancelButton = new JButton(new CancelAction());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		this.add(buttonPanel, cc.xy(2, 12));
		this.getRootPane().setDefaultButton(okButton);
		this.pack();
		this.setLocationRelativeTo(mainWindow);
	}

	private JPanel createGeneralSelectionPanel() {
		JPanel generalSelectionPanel = new JPanel();
		generalSelectionPanel.setBorder(BorderFactory.createTitledBorder(Texts.getTranslatedText(Texts.EXPORT_DIALOG_GENERAL_INFO_TITLE)));
		generalSelectionPanel.setLayout(new FormLayout("5px, fill:pref:none, 5px, fill:pref:grow, 5px, left:pref:none, 5px",
				"5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px"));
		CellConstraints cc = new CellConstraints();

		ValueModel exportFormatValueModel = new PropertyAdapter<ExportDescription>(exportDescription, ExportDescription.FORMAT_EXPORT_FORMAT_PROPERTY, true);
		exportFormatComboBox = new JComboBox<ExportDescription.ExportFormat>();
		exportFormatComboBox.setModel(new ComboBoxAdapter<ExportDescription.ExportFormat>(ExportDescription.ExportFormat.values(), exportFormatValueModel));
		exportFormatComboBox.setSelectedItem(ExportDescription.ExportFormat.CSV);

		generalSelectionPanel.add(new JLabel(Texts.getTranslatedText(Texts.EXPORT_DIALOG_FORMAT_LABEL)), cc.xy(2, 2));
		generalSelectionPanel.add(exportFormatComboBox, cc.xy(4, 2));

		ValueModel exportPathValueModel = new PropertyAdapter<ExportDescription>(exportDescription, ExportDescription.FORMAT_EXPORT_PATH_PROPERTY, true);
		pathTextField = BasicComponentFactory.createTextField(exportPathValueModel);
		pathTextField.setEditable(false);
		pathTextField.setEnabled(true);
		generalSelectionPanel.add(new JLabel(Texts.getTranslatedText(Texts.EXPORT_DIALOG_PATH_LABEL)), cc.xy(2, 4));
		generalSelectionPanel.add(pathTextField, cc.xy(4, 4));

		browseButton = new JButton(new BrowseAction());
		generalSelectionPanel.add(browseButton, cc.xy(6, 4));

		addCheckBox(generalSelectionPanel, cc, 2, 6, ExportDescription.FORMAT_EXPORT_HIERARCHICAL_PROPERTY, Texts.EXPORT_HIERARCHICAL);
		addCheckBox(generalSelectionPanel, cc, 2, 8, ExportDescription.FORMAT_ADD_HEADER_INFO_PROPERTY, Texts.EXPORT_HEADER);
		addCheckBox(generalSelectionPanel, cc, 2, 10, ExportDescription.FORMAT_GROUP_INFO_AS_HEADER_PROPERTY, Texts.EXPORT_HEADER_FORMAT_GROUP_INFO_AS_HEADER);

		return generalSelectionPanel;
	}

	private JPanel createAttributeSelectionPanel() {
		JPanel attributeSelectionPanel = new JPanel();
		attributeSelectionPanel.setBorder(BorderFactory.createTitledBorder(Texts.getTranslatedText(Texts.EXPORT_DIALOG_ATTRIBUTE_TITLE)));
		attributeSelectionPanel.setLayout(new FormLayout("5px, left:pref:grow, 5px, left:pref:grow, 5px, left:pref:grow, 5px, left:pref:grow, 5px, left:pref:grow, 5px, left:pref:grow, 5px",
				"5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px"));
		CellConstraints cc = new CellConstraints();

		addCheckBox(attributeSelectionPanel, cc, 2, 2, ExportDescription.EXPORT_ATTRIBUTE_GROUP, Texts.EXPORT_HEADER_GROUP_NAME);
		addCheckBox(attributeSelectionPanel, cc, 2, 4, ExportDescription.EXPORT_ATTRIBUTE_ID_PROPERTY, Texts.EXPORT_HEADER_ID);
		addCheckBox(attributeSelectionPanel, cc, 2, 6, ExportDescription.EXPORT_ROLE_PROPERTY, Texts.EXPORT_HEADER_ROLE_TYPE);
		addCheckBox(attributeSelectionPanel, cc, 2, 8, ExportDescription.EXPORT_ATTRIBUTE_TYPE_PROPERTY, Texts.EXPORT_HEADER_TYPE);
		addCheckBox(attributeSelectionPanel, cc, 2, 10, ExportDescription.EXPORT_ATTRIBUTE_HREF_PROPERTY, Texts.EXPORT_HEADER_HREF);
		addCheckBox(attributeSelectionPanel, cc, 2, 12, ExportDescription.EXPORT_ATTRIBUTE_SALUTATION_PROPERTY, Texts.EXPORT_HEADER_SALUTATION);
		
		addCheckBox(attributeSelectionPanel, cc, 4, 2, ExportDescription.EXPORT_ATTRIBUTE_TITLE_PROPERTY, Texts.EXPORT_HEADER_TITLE);
		addCheckBox(attributeSelectionPanel, cc, 4, 4, ExportDescription.EXPORT_ATTRIBUTE_FIRST_NAME_PROPERTY, Texts.EXPORT_HEADER_FIRST_NAME);
		addCheckBox(attributeSelectionPanel, cc, 4, 6, ExportDescription.EXPORT_ATTRIBUTE_LAST_NAME_PROPERTY, Texts.EXPORT_HEADER_LAST_NAME);
		addCheckBox(attributeSelectionPanel, cc, 4, 8, ExportDescription.EXPORT_ATTRIBUTE_NICKNAME_PROPERTY, Texts.EXPORT_HEADER_NICKNAME);
		addCheckBox(attributeSelectionPanel, cc, 4, 10, ExportDescription.EXPORT_ATTRIBUTE_GENDER_PROPERTY, Texts.EXPORT_HEADER_GENDER);
		addCheckBox(attributeSelectionPanel, cc, 4, 12, ExportDescription.EXPORT_ATTRIBUTE_COMPANY_NAME_PROPERTY, Texts.EXPORT_HEADER_COMPANY_NAME);
		
		addCheckBox(attributeSelectionPanel, cc, 6, 2, ExportDescription.EXPORT_ATTRIBUTE_IS_COMPANY_PROPERTY, Texts.EXPORT_HEADER_IS_COMPANY_NAME);
		addCheckBox(attributeSelectionPanel, cc, 6, 4, ExportDescription.EXPORT_ATTRIBUTE_ZIP_CODE_PROPERTY, Texts.EXPORT_HEADER_ZIP_CODE);		
		addCheckBox(attributeSelectionPanel, cc, 6, 6, ExportDescription.EXPORT_ATTRIBUTE_TOWN_PROPERTY, Texts.EXPORT_HEADER_TOWN);
		addCheckBox(attributeSelectionPanel, cc, 6, 8, ExportDescription.EXPORT_ATTRIBUTE_CANTON_PROPERTY, Texts.EXPORT_HEADER_CANTON);
		addCheckBox(attributeSelectionPanel, cc, 6, 10, ExportDescription.EXPORT_ATTRIBUTE_COUNTRY_PROPERTY, Texts.EXPORT_HEADER_COUNTRY);
		addCheckBox(attributeSelectionPanel, cc, 6, 12, ExportDescription.EXPORT_ATTRIBUTE_CORRESPONDENCE_LANGUAGE_PROPERTY, Texts.EXPORT_HEADER_CORRESPONDENCE_LANG);

		addCheckBox(attributeSelectionPanel, cc, 8, 2, ExportDescription.EXPORT_ATTRIBUTE_EMAIL_PROPERTY, Texts.EXPORT_HEADER_EMAIL);
		addCheckBox(attributeSelectionPanel, cc, 8, 4, ExportDescription.EXPORT_PUBLIC_EMAIL_ONLY_PROPERTY, Texts.EXPORT_DIALOG_PUBLIC_EMAIL_ONLY);		
		addCheckBox(attributeSelectionPanel, cc, 8, 6, ExportDescription.EXPORT_PUBLIC_PHONE_NUMBER_PROPERTY, Texts.EXPORT_HEADER_PHONE);
		addCheckBox(attributeSelectionPanel, cc, 8, 8, ExportDescription.EXPORT_PUBLIC_MOBILE_NUMBER_PROPERTY, Texts.EXPORT_HEADER_MOBILE_NUMBER);
		addCheckBox(attributeSelectionPanel, cc, 8, 10, ExportDescription.EXPORT_ATTRIBUTE_SALUTATION_PARENTS_PROPERTY, Texts.EXPORT_HEADER_SALUTATION_PARENTS);
		addCheckBox(attributeSelectionPanel, cc, 8, 12, ExportDescription.EXPORT_ATTRIBUTE_NAME_PARENTS_PROPERTY, Texts.EXPORT_HEADER_NAME_PARENTS);
		
		addCheckBox(attributeSelectionPanel, cc, 10, 2, ExportDescription.EXPORT_ATTRIBUTE_JS_NUMBER_PROPERTY, Texts.EXPORT_HEADER_JS_NUMBER);
		addCheckBox(attributeSelectionPanel, cc, 10, 4, ExportDescription.EXPORT_ATTRIBUTE_NATIONALITY_JS_PROPERTY, Texts.EXPORT_HEADER_NATIONALITY_JS);
		addCheckBox(attributeSelectionPanel, cc, 10, 6, ExportDescription.EXPORT_ATTRIBUTE_AHV_NUMBER_PROPERTY, Texts.EXPORT_HEADER_AHV_NUMBER);
		addCheckBox(attributeSelectionPanel, cc, 10, 8, ExportDescription.EXPORT_ATTRIBUTE_AHV_NUMBER_OLD_PROPERTY, Texts.EXPORT_HEADER_AHV_NUMBER_OLD);
		addCheckBox(attributeSelectionPanel, cc, 10, 10, ExportDescription.EXPORT_ATTRIBUTE_PROFESSION_PROPERTY, Texts.EXPORT_HEADER_PROFESSION);
		addCheckBox(attributeSelectionPanel, cc, 10, 12, ExportDescription.EXPORT_ATTRIBUTE_JOINED_PROPERTY, Texts.EXPORT_HEADER_JOINED_DATE);
		
		addCheckBox(attributeSelectionPanel, cc, 12, 2, ExportDescription.EXPORT_ATTRIBUTE_MEMBER_CARD_NUMBER_PROPERTY, Texts.EXPORT_HEADER_MEMBER_CARD_NUMBER);
		addCheckBox(attributeSelectionPanel, cc, 12, 4, ExportDescription.EXPORT_ATTRIBUTE_NATIONALITY_PROPERTY, Texts.EXPORT_HEADER_NATIONALITY);
		addCheckBox(attributeSelectionPanel, cc, 12, 6, ExportDescription.EXPORT_ATTRIBUTE_CONFESSION_PROPERTY, Texts.EXPORT_HEADER_CONFESSION);
		addCheckBox(attributeSelectionPanel, cc, 12, 8, ExportDescription.EXPORT_ATTRIBUTE_BIRTHDAY_PROPERTY, Texts.EXPORT_HEADER_BIRTHDAY);
		addCheckBox(attributeSelectionPanel, cc, 12, 10, ExportDescription.EXPORT_ATTRIBUTE_PRIMARY_GROUP_ID_PROPERTY, Texts.EXPORT_HEADER_PRIMARY_GROUP);
		addCheckBox(attributeSelectionPanel, cc, 12, 12, ExportDescription.EXPORT_ATTRIBUTE_ADDITIONAL_INFORMATION_PROPERTY, Texts.EXPORT_HEADER_ADDITIONAL_INFORMATION);
		
		return attributeSelectionPanel;
	}

	private void addCheckBox(JPanel targetPanel, CellConstraints cc, int xPos, int yPos, String propertyName, String textKey) {
		ValueModel valueModel = new PropertyAdapter<ExportDescription>(exportDescription, propertyName, true);
		JCheckBox checkBox = BasicComponentFactory.createCheckBox(valueModel, Texts.getTranslatedText(textKey));
		targetPanel.add(checkBox, cc.xy(xPos, yPos));
	}

	class BrowseAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public BrowseAction() {
			super(Texts.getTranslatedText(Texts.EXPORT_DIALOG_BROWSE_BUTTON));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			if (exportDescription.getExportPath() != null && !exportDescription.getExportPath().isEmpty()) {
				fileChooser.setSelectedFile(new File(exportDescription.getExportPath()));
			}
			if (fileChooser.showSaveDialog(ExportDialog.this) == JFileChooser.APPROVE_OPTION) {
				// exportDescription.setExportPath(fileChooser.getSelectedFile().getAbsolutePath());
				pathTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				exportDescription.setExportPath(pathTextField.getText());
			}
		}

	}

	class OkAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public OkAction() {
			super(Texts.getTranslatedText(Texts.EXPORT_DIALOG_OK_BUTTON_LABEL));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GeneralExporter exporter = null;
			switch (exportDescription.getExportFormat()) {
			case CSV:
				exporter = new CSVExporter(exportDescription, serverFacade, currentGroup);
				break;
			case XLS:
				exporter = new Excel97Exporter(exportDescription, serverFacade, currentGroup);
				break;
			default:
				break;
			}
			if (exporter != null) {
				ExportDialog.this.setEnabled(false);
				exporter.exportData(new LoadingListner<Object>() {

					@Override
					public void loadingFinished(Object result, Exception e) {
						ExportDialog.this.dispatchEvent(new WindowEvent(ExportDialog.this, WindowEvent.WINDOW_CLOSING));
					}

					@Override
					public void displayCurrentAction(String currentAction) {

					}
				});
			}
		}
	}

	class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CancelAction() {
			super(Texts.getTranslatedText(Texts.EXPORT_DIALOG_CANCEL_BUTTON_LABEL));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ExportDialog.this.dispatchEvent(new WindowEvent(ExportDialog.this, WindowEvent.WINDOW_CLOSING));
		}

	}
}
