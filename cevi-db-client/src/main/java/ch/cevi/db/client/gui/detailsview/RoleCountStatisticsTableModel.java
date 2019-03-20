package ch.cevi.db.client.gui.detailsview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.business.entities.Person;
import ch.cevi.db.client.business.entities.Role;
import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.configuration.Texts;

public class RoleCountStatisticsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -8938603953164722545L;

	private static Logger LOGGER = LoggerFactory.getLogger(RoleCountStatisticsTableModel.class.getName());

	public static final int COLUMN_NAME = 0;

	public static final int COLUMN_COUNT = 1;

	private static final String[] COLUMNS = { Texts.getTranslatedText(Texts.ROLE_COUNT_STATISTICS_COLUMN_NAME), Texts.getTranslatedText(Texts.ROLE_COUNT_STATISTICS_COLUMN_COUNT) };

	private static final Class<?>[] COLUMN_CLASSES = { String.class, Integer.class };

	private YGroup group;

	private List<RoleCountTableRow> dataList;

	public RoleCountStatisticsTableModel() {
		this.dataList = new ArrayList<RoleCountTableRow>();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_CLASSES[columnIndex];
	}

	@Override
	public int getRowCount() {
		return dataList.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;
		RoleCountTableRow row = dataList.get(rowIndex);
		switch (columnIndex) {
		case COLUMN_NAME:
			value = row.getRoleName();
			break;
		case COLUMN_COUNT:
			value = row.getCount();
			break;
		default:
			LOGGER.error("Column out of range in RoleCountStatisticsTableModel");
			break;
		}

		return value;
	}

	public YGroup getGroup() {
		return group;
	}

	public void setGroup(YGroup group) {
		this.group = group;
		dataList.clear();
		if (group != null) {
			Map<String, RoleCountTableRow> createdRowsMap = new HashMap<String, RoleCountTableRow>();
			for (Person person : group.getGroupMembers()) {
				for (Role role : person.getRoles()) {
					// Only functions witch belong to the current group
					if (role.getGroupId().equals(group.getId())) {
						RoleCountTableRow roleCountTableRow = createdRowsMap.get(role.getRoleType());
						if (roleCountTableRow == null) {
							roleCountTableRow = new RoleCountTableRow(role.getRoleType(), 1);
							createdRowsMap.put(role.getRoleType(), roleCountTableRow);
							dataList.add(roleCountTableRow);
						} else {
							roleCountTableRow.incrementCount();
						}
					}
				}
			}
		}
		fireTableDataChanged();
	}

	private class RoleCountTableRow {
		private String roleName;

		private int count;

		public RoleCountTableRow(String roleName, int count) {
			super();
			this.roleName = roleName;
			this.count = count;
		}

		public String getRoleName() {
			return roleName;
		}

		public void incrementCount() {
			count++;
		}

		public int getCount() {
			return count;
		}
	}
}
