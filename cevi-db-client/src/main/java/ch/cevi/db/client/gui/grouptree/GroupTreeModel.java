package ch.cevi.db.client.gui.grouptree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.communication.ServerFacade;

public class GroupTreeModel extends LazyTreeModel {

	private static final long serialVersionUID = -937096217727326376L;

	private static Logger LOGGER = LoggerFactory.getLogger(GroupTreeModel.class.getName());

	private ServerFacade serverFacade;

	public GroupTreeModel(GroupTree tree, TreeNode root, ServerFacade serverFacade) {
		super(root, tree);
		this.serverFacade = serverFacade;
	}

	@Override
	public LazyTreeNode[] loadChildren(LazyTreeNode parentNode) {
		YGroup parentGroup = (YGroup) parentNode.getUserObject();
		if (parentGroup != null) {
			try {
				List<YGroup> loadedChildren = new ArrayList<YGroup>();
				List<LazyTreeNode> newTreeNodes = new ArrayList<LazyTreeNode>(loadedChildren.size());
				for (YGroup childGroupToComplete : parentGroup.getChildren()) {
					serverFacade.completeNotFullyLoadedGroup(childGroupToComplete);
					if (childGroupToComplete.getDeletedAt() == null) {
						loadedChildren.add(childGroupToComplete);
						newTreeNodes.add(new LazyTreeNode(childGroupToComplete.getId(), childGroupToComplete, true));
					}
				}
				return newTreeNodes.toArray(new LazyTreeNode[0]);
			} catch (Exception e) {
				LOGGER.error("Could not load childnodes: " + e);
			}
		}
		return null;
	}

}
