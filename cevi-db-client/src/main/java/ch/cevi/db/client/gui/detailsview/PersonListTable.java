package ch.cevi.db.client.gui.detailsview;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class PersonListTable extends JTable {

	private static final long serialVersionUID = 12928345928390L;

	public PersonListTable(PersonListTableModel model) {
		super(model);
		this.setAutoCreateRowSorter(true);
		RowSorter<?> sorter = getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(PersonListTableModel.COLUMN_LASTNAME, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(PersonListTableModel.COLUMN_FIRSTNAME, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
	}

}
