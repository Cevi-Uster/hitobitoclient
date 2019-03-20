package ch.cevi.db.client.gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * This class extends JDialog with the capability of being closed with a tap on
 * the ESC key.
 * 
 * @author Marc Baumgartner
 *
 */
public abstract class AbstractDialog extends JDialog {

	public static final String dispatchWindowClosingActionMapKey = "ch.cevi.db.client.dispatch:WINDOW_CLOSING";

	private static final long serialVersionUID = -7293445566833624625L;

	private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog() {
		super();
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Dialog owner, String title) {
		super(owner, title);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Dialog owner) {
		super(owner);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Frame owner, boolean modal) {
		super(owner, modal);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Frame owner, String title) {
		super(owner, title);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Frame owner) {
		super(owner);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Window owner, String title) {
		super(owner, title);
		installEscapeCloseOperation();
	}

	/**
	 * {@inheritDoc}
	 */
	public AbstractDialog(Window owner) {
		super(owner);
		installEscapeCloseOperation();
	}

	public void installEscapeCloseOperation() {
		Action dispatchClosing = new AbstractAction() {
			private static final long serialVersionUID = -3798069232772664875L;

			public void actionPerformed(ActionEvent event) {
				AbstractDialog.this.dispatchEvent(new WindowEvent(AbstractDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		};

		JRootPane root = this.getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, dispatchWindowClosingActionMapKey);
		root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
	}
}
