package ch.cevi.db.client.gui.logpane;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LogPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextArea textArea;

	@SuppressWarnings("unused")
	private JTextAreaAppender appender;
	
	public LogPane() {
		super(true);
		initGui();
		appender = new JTextAreaAppender(textArea);
	}

	private void initGui() {
		this.setLayout(new FormLayout("fill:0px:grow", "fill:150px:grow"));
		CellConstraints cc = new CellConstraints();
		this.textArea = new JTextArea();
		this.textArea.setLineWrap(true);
		this.textArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scrollPane, cc.xy(1, 1));
	}

	public void append(String string) {
		textArea.append(string);
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}

}
