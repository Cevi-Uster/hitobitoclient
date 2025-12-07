package ch.cevi.db.client;

import javax.swing.SwingUtilities;

import ch.cevi.db.client.gui.MainWindow;

public class DBClient {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow mainWindow = new MainWindow();
				mainWindow.start();
			}
		});
	}
}
