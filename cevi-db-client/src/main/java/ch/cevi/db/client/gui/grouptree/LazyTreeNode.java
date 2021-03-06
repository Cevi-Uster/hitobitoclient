package ch.cevi.db.client.gui.grouptree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A tree node that supports lazy loading. Copied from
 * http://stackoverflow.com/questions/1974670/java-dynamic-jtree Thanks to
 * adrian.tarau
 * 
 * @author developer
 *
 */
public class LazyTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 5314303788515075706L;

	private boolean loaded;
	private String id;

	public LazyTreeNode(String id) {
		this(id, null);
	}

	public LazyTreeNode(String id, Object userObject) {
		this(id, userObject, true);
	}

	public LazyTreeNode(String id, Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	protected boolean isLoaded() {
		return loaded;
	}

	protected void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public boolean isLeaf() {
		return !getAllowsChildren();
	}
}