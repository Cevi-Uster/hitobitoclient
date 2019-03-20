package ch.cevi.db.client.gui.grouptree;

import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import ch.cevi.db.client.business.entities.YGroup;

public class GroupTree extends JTree {

	private static final long serialVersionUID = 67620900845508347L;

	public GroupTree() {
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	@Override
	public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value instanceof LazyTreeNode && ((LazyTreeNode) value).getUserObject() instanceof YGroup) {
			return ((YGroup) ((LazyTreeNode) value).getUserObject()).getName();
		}
		return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
	}

}
