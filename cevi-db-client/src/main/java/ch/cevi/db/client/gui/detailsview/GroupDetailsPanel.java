package ch.cevi.db.client.gui.detailsview;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.communication.ServerFacade;
import ch.cevi.db.client.configuration.Texts;
import ch.cevi.db.client.gui.MainWindow;

public class GroupDetailsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ServerFacade serverFacade;

	private RoleCountStatisticsTableModel roleCountStatisticsTableModel;

	private RoleCountStatisticsTable roleCountStatisticsTable;

	private MainWindow mainWindow;

	public GroupDetailsPanel(MainWindow mainWindow, ServerFacade serverFacade) {
		this.mainWindow = mainWindow;
		this.serverFacade = serverFacade;
		roleCountStatisticsTableModel = new RoleCountStatisticsTableModel();
		roleCountStatisticsTable = new RoleCountStatisticsTable(roleCountStatisticsTableModel);
	}

	public void initAndBindGui(YGroup currentGroup) {
		this.removeAll();
		this.setLayout(new FormLayout("5px, left:pref:none, 5px, fill:200px:grow, 5px",
				"5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, top:pref:none, 5px, fill:50px:grow, 5px, top:pref:none, 5px"));
		CellConstraints cc = new CellConstraints();

		ValueModel idValueModel = new PropertyAdapter<YGroup>(currentGroup, YGroup.ID_PROPERTY, true);
		JTextField idTextField = BasicComponentFactory.createTextField(idValueModel);
		this.add(new JLabel(Texts.getTranslatedText(Texts.GROUP_DETAILS_ID)), cc.xy(2, 2));
		this.add(idTextField, cc.xy(4, 2));
		idTextField.setEditable(false);
		idTextField.setAlignmentX(RIGHT_ALIGNMENT);

		ValueModel nameValueModel = new PropertyAdapter<YGroup>(currentGroup, YGroup.NAME_PROPERTY, true);
		JTextField nameTextField = BasicComponentFactory.createTextField(nameValueModel);
		this.add(new JLabel(Texts.getTranslatedText(Texts.GROUP_DETAILS_NAME)), cc.xy(2, 4));
		this.add(nameTextField, cc.xy(4, 4));
		nameTextField.setEditable(false);

		ValueModel groupTypeValueModel = new PropertyAdapter<YGroup>(currentGroup, YGroup.GROUP_TYPE_PROPERTY, true);
		JTextField groupTypeTextField = BasicComponentFactory.createTextField(groupTypeValueModel);
		this.add(new JLabel(Texts.getTranslatedText(Texts.GROUP_DETAILS_GROUP_TYPE)), cc.xy(2, 6));
		this.add(groupTypeTextField, cc.xy(4, 6));
		groupTypeTextField.setEditable(false);

		ValueModel isLayerValueModel = new PropertyAdapter<YGroup>(currentGroup, YGroup.IS_LAYER_PROPERY, true);
		JCheckBox isLayerCheckBox = BasicComponentFactory.createCheckBox(isLayerValueModel, "");
		this.add(new JLabel(Texts.getTranslatedText(Texts.GROUP_DETAILS_IS_LAYER)), cc.xy(2, 8));
		this.add(isLayerCheckBox, cc.xy(4, 8));
		isLayerCheckBox.setEnabled(false);

		ValueModel typeValueModel = new PropertyAdapter<YGroup>(currentGroup, YGroup.TYPE_PROPERY, true);
		JTextField typeTextField = BasicComponentFactory.createTextField(typeValueModel);
		this.add(new JLabel(Texts.getTranslatedText(Texts.GROUP_DETAILS_TYPE)), cc.xy(2, 10));
		this.add(typeTextField, cc.xy(4, 10));
		typeTextField.setEditable(false);

		roleCountStatisticsTableModel.setGroup(currentGroup);
		JScrollPane roleCountStatisticsScrollPane = new JScrollPane();
		roleCountStatisticsScrollPane.getViewport().add(roleCountStatisticsTable);
		this.add(roleCountStatisticsScrollPane, cc.xyw(2, 12, 3));

		JPanel buttonPanel = new JPanel();
		
		JButton exportButton = new JButton(new OpenExportDialogAction(mainWindow, serverFacade, currentGroup));
		buttonPanel.add(exportButton);
		this.add(buttonPanel, cc.xy(2, 14));
		this.revalidate();
		this.repaint();
	}
}
