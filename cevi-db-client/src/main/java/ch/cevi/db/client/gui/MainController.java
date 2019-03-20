package ch.cevi.db.client.gui;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.communication.ServerFacade;
import ch.cevi.db.client.communication.Session;
import ch.cevi.db.client.configuration.Configuration;
import ch.cevi.db.client.configuration.Texts;

public class MainController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class.getName());

	private MainWindow mainWindow;

	private Session session;

	private ServerFacade serverFacade;

	private Configuration configuration;

	public MainController(Configuration configuration, MainWindow mainWindow) {
		this.configuration = configuration;
		this.mainWindow = mainWindow;
	}

	public void connect() {
		session = new Session(configuration.getBaseUrl().toString(), configuration.getUsePrimaryGroupAsRoot());
		SwingWorker<Object, Object> loginWorker = new SwingWorker<Object, Object>() {

			boolean loginSuccessfull = false;

			@Override
			protected Object doInBackground() throws Exception {
				try {
					session.login(configuration.getUsername(), configuration.getPassword());
					loginSuccessfull = true;
				} catch (Exception e) {
					LOGGER.error("Login failed: " + e.getMessage());
				}
				return null;
			}

			@Override
			protected void done() {
				if (!loginSuccessfull) {
					loginFailed();
				} else {
					loginSucceded();
				}
			}

		};
		loginWorker.execute();
	}

	private void loginSucceded() {
		LOGGER.info("Login succeded");
		LOGGER.info("Try to load root group");
		serverFacade = new ServerFacade(session);
		SwingWorker<Object, Object> loadFirstGroupWorker = new SwingWorker<Object, Object>() {

			YGroup rootGroup = null;

			@Override
			protected Object doInBackground() throws Exception {
				try {
					rootGroup = serverFacade.loadMainGroupWithDetails();
				} catch (Exception e) {
					LOGGER.error("Loading root group failed: " + e.getMessage());
				}
				return null;
			}

			@Override
			protected void done() {
				if (rootGroup != null) {
					loadRootGroupSucceded(rootGroup);
				} else {
					loadRootGroupFailed();
				}
			}

		};
		loadFirstGroupWorker.execute();
	}

	private void loginFailed() {
		LOGGER.info("Login failed");
		ConnectDialog connectDialog = new ConnectDialog(mainWindow, configuration, Texts.getTranslatedText(Texts.ERROR_MSG_LOGIN_FAILED));
		connectDialog.setLocationRelativeTo(mainWindow);
		connectDialog.setVisible(true);
	}

	private void loadRootGroupSucceded(YGroup rootGroup) {
		mainWindow.initGroupTree(rootGroup);
	}

	private void loadRootGroupFailed() {
		LOGGER.info("Load root category failed");
	}

	public ServerFacade getServerFacade() {
		return serverFacade;
	}

	public void closeApplication() {
		System.exit(0);
	}

}
