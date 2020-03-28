package ch.cevi.db.client.gui.statusbar;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * A panel which shows the current heap usage. Taken from 
 * https://stackoverflow.com/questions/1346105/java-swing-free-memory-component-needed
 * @author mbaumgar
 *
 */
public class MemoryPanelProgressBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JProgressBar progressBar = new JProgressBar();

	public MemoryPanelProgressBar() {
		super(new FlowLayout());
		this.progressBar.setStringPainted(true);
		this.progressBar.setString("");
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(100);
		this.progressBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent ev) {
				if (ev.getClickCount() == 2) {
					System.gc();
					update();
				}
			}
		});
		add(this.progressBar);
		Timer t = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				update();
			}
		});
		t.start();
		update();
	}

	private void update() {
		Runtime jvmRuntime = Runtime.getRuntime();
		long totalMemory = jvmRuntime.totalMemory();
		long maxMemory = jvmRuntime.maxMemory();
		long usedMemory = totalMemory - jvmRuntime.freeMemory();
		long totalMemoryInMebibytes = totalMemory / (1024 * 1024);
		long maxMemoryInMebibytes = maxMemory / (1024 * 1024);
		long usedMemoryInMebibytes = usedMemory / (1024 * 1024);
		int usedPct = (int) ((100 * usedMemory) / totalMemory);
		String textToShow = usedMemoryInMebibytes + "MiB of " + totalMemoryInMebibytes + "MiB";
		String toolTipToShow = "Heap size: " + usedMemoryInMebibytes + "MiB of total: " + totalMemoryInMebibytes
				+ "MiB max: " + maxMemoryInMebibytes + "MiB";
		this.progressBar.setValue(usedPct);
		this.progressBar.setString(textToShow);
		this.progressBar.setToolTipText(toolTipToShow);
		this.progressBar.setPreferredSize(new Dimension(200, 12));
	}
}