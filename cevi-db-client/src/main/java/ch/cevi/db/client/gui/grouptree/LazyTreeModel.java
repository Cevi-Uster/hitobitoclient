package ch.cevi.db.client.gui.grouptree;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * A tree model that supports lazy loading. Copied from
 * http://stackoverflow.com/questions/1974670/java-dynamic-jtree Thanks to
 * adrian.tarau
 * 
 * @author developer
 *
 */
public abstract class LazyTreeModel extends DefaultTreeModel implements TreeWillExpandListener {

	private Logger LOGGER = Logger.getLogger(LazyTreeModel.class.getName());

	private static final long serialVersionUID = -3134581120355800903L;

	private static final int THREAD_POOL_SIZE = 4;

	private ExecutorService executorService;

	public LazyTreeModel(TreeNode root, JTree tree) {
		super(root);
		executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		setAsksAllowsChildren(true);
		tree.addTreeWillExpandListener(this);
		tree.setModel(this);
	}

	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		LazyTreeNode node = (LazyTreeNode) event.getPath().getLastPathComponent();
		if (node.isLoaded()) {
			return;
		}
		setLoading(node, false);
		executorService.execute(new LoadNodesWorker(node));
	}

	public void reloadNode(String id) {
		LazyTreeNode node = findNode(id);
		if (node != null) {
			node.setLoaded(false);
			setLoading(node, true);
			executorService.execute(new LoadNodesWorker(node));
		}
	}

	public void reloadParentNode(String id) {
		LazyTreeNode node = findParent(id);
		if (node != null) {
			node.setLoaded(false);
			setLoading(node, true);
			executorService.execute(new LoadNodesWorker(node));
		}
	}

	public LazyTreeNode findParent(String id) {
		LazyTreeNode node = findNode(id);
		if (node != null && node.getParent() != null) {
			return (LazyTreeNode) node.getParent();
		}
		return null;
	}

	public void loadFirstLevel() {
		setLoading((LazyTreeNode) getRoot(), false);
		executorService.execute(new LoadNodesWorker((LazyTreeNode) getRoot()));
	}

	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
	}

	protected void setChildren(LazyTreeNode parentNode, LazyTreeNode... nodes) {
		if (nodes == null) {
			return;
		}
		int childCount = parentNode.getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				removeNodeFromParent((MutableTreeNode) parentNode.getChildAt(0));
			}
		}
		for (int i = 0; i < nodes.length; i++) {
			insertNodeInto(nodes[i], parentNode, i);
		}
	}

	private void setLoading2(final LazyTreeNode parentNode, final boolean reload) {
		if (reload) {
			setChildren(parentNode, createReloadingNode());
		} else {
			setChildren(parentNode, createLoadingNode());
		}
	}

	private void setLoading(final LazyTreeNode parentNode, final boolean reload) {
		if (SwingUtilities.isEventDispatchThread()) {
			setLoading2(parentNode, reload);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						setLoading2(parentNode, reload);
					}
				});
			} catch (Exception e) {
				LOGGER.severe("Cannot create loading node" + e);
			}
		}
	}

	private LazyTreeNode findNode(String id) {
		return findNode(id, (LazyTreeNode) getRoot());
	}

	private LazyTreeNode findNode(String id, LazyTreeNode parent) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			LazyTreeNode node = (LazyTreeNode) parent.getChildAt(i);
			if (id.equals(node.getId())) {
				return node;
			}
			if (node.isLoaded()) {
				node = findNode(id, node);
				if (node != null) {
					return node;
				}
			}
		}
		return null;
	}

	public abstract LazyTreeNode[] loadChildren(LazyTreeNode parentNode);

	protected LazyTreeNode createLoadingNode() {
		return new LazyTreeNode(null, "Loading...", false);
	}

	protected LazyTreeNode createReloadingNode() {
		return new LazyTreeNode(null, "Refreshing...", false);
	}

	class LoadNodesWorker implements Runnable {

		private LazyTreeNode parentNode;

		LoadNodesWorker(LazyTreeNode parent) {
			this.parentNode = parent;
		}

		public String getName() {
			return "Lazy loading of node " + parentNode.getId();
		}

		public void run() {
			final LazyTreeNode[] treeNodes = loadChildren(parentNode);
			if (treeNodes == null) {
				return;
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					parentNode.setLoaded(true);
					setChildren(parentNode, treeNodes);
				}
			});
		}
	}
}