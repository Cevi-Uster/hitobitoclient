package ch.cevi.db.client.gui.grouptree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.gui.detailsview.DetailsTabbedPane;

public class GroupTreeSelectionListener implements TreeSelectionListener {

	private DetailsTabbedPane detailsTabbedPane;
	private GroupTree groupTree;

	public GroupTreeSelectionListener(GroupTree groupTree, DetailsTabbedPane detailsTabbedPane) {
		this.groupTree = groupTree;
		this.detailsTabbedPane = detailsTabbedPane;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		LazyTreeNode node = (LazyTreeNode) groupTree.getLastSelectedPathComponent();
		if (node == null) {
			this.detailsTabbedPane.setCurrentGroup(null);
		} else {
			this.detailsTabbedPane.setCurrentGroup((YGroup) node.getUserObject());
		}
	}

}
