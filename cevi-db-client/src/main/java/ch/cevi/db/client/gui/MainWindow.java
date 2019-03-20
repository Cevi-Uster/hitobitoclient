package ch.cevi.db.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cevi.db.client.business.entities.YGroup;
import ch.cevi.db.client.configuration.Configuration;
import ch.cevi.db.client.configuration.Texts;
import ch.cevi.db.client.gui.actions.AboutAction;
import ch.cevi.db.client.gui.actions.CloseApplicationAction;
import ch.cevi.db.client.gui.actions.ConnectAction;
import ch.cevi.db.client.gui.detailsview.DetailsTabbedPane;
import ch.cevi.db.client.gui.grouptree.GroupTree;
import ch.cevi.db.client.gui.grouptree.GroupTreeModel;
import ch.cevi.db.client.gui.grouptree.GroupTreeSelectionListener;
import ch.cevi.db.client.gui.grouptree.LazyTreeNode;
import ch.cevi.db.client.gui.logpane.LogPane;
import ch.cevi.db.client.gui.mainmenu.MainMenuBar;

public class MainWindow extends JFrame {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class.getName());

	private static final long serialVersionUID = 1109381891234L;

	private static final String WINDOW_WIDTH_PREF_KEY = "MainWindow.width";

	private static final String WINDOW_HEIGHT_PREF_KEY = "MainWindow.height";

	private static final String WINDOW_XPOS_PREF_KEY = "MainWindow.xPos";

	private static final String WINDOW_YPOS_PREF_KEY = "MainWindow.yPos";

	private static final String WINDOW_EXTENDED_STATE_KEY = "MainWindow.extendedState";

	private Preferences prefs;

	private MainController mainController;

	private Configuration configuration;

	private MainMenuBar mainMenuBar;

	private JSplitPane mainSplitPane;
	
	private LogPane logPane;

	private JScrollPane treeScrollPane;

	private GroupTree groupTree;

	private GroupTreeModel groupTreeModel;

	private DetailsTabbedPane detailsTabbedPane;

	private ConnectAction connectAction;

	public MainWindow() throws HeadlessException {
		super(Texts.getTranslatedText(Texts.MAIN_WINDOW_TITLE));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				mainController.closeApplication();
			}
		});
		this.prefs = Preferences.userRoot().node(this.getClass().getName());
		this.configuration = new Configuration();
		this.mainController = new MainController(this.configuration, this);
		connectAction = new ConnectAction(this, configuration);
		registerWindowListeners();
		initMainMenu();
		initMainView();
		initLogPane();
	}

	public void start() {
		LOGGER.info("Start MainWindow with locale " + Locale.getDefault());
		restoreSizePositionAndExtendedState();
		setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				connectAction.actionPerformed(null);
			}
		});
		
	}

	private void registerWindowListeners() {

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				saveSizePositionAndExtendedState();
			}

		});
	}

	private void initMainMenu() {
		this.mainMenuBar = new MainMenuBar();
		this.mainMenuBar.setExitAction(new CloseApplicationAction(this));
		this.mainMenuBar.setConnectAction(connectAction);
		this.mainMenuBar.setAboutAction(new AboutAction(this));
		this.setJMenuBar(mainMenuBar);
	}

	private void initMainView() {
		this.getContentPane().setLayout(new BorderLayout());
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		this.getContentPane().add(mainSplitPane, BorderLayout.CENTER);
	}

	private void initLogPane() {
		logPane = new LogPane();
		this.add(logPane, BorderLayout.SOUTH);
	}
	
	private void restoreSizePositionAndExtendedState() {
		LOGGER.debug("Restoring position and size of main window");
		int width = prefs.getInt(WINDOW_WIDTH_PREF_KEY, 800);
		int height = prefs.getInt(WINDOW_HEIGHT_PREF_KEY, 600);
		int xPos = prefs.getInt(WINDOW_XPOS_PREF_KEY, -1000);
		int yPos = prefs.getInt(WINDOW_YPOS_PREF_KEY, -1000);
		int state = prefs.getInt(WINDOW_EXTENDED_STATE_KEY, MAXIMIZED_BOTH);

		setSize(width, height);
		this.setExtendedState(state);
		if (xPos == -1000 && yPos == -1000) {
			setLocationRelativeTo(null);
		} else {
			setLocation(xPos, yPos);
		}
	}

	private void saveSizePositionAndExtendedState() {
		prefs.putInt(WINDOW_WIDTH_PREF_KEY, this.getWidth());
		prefs.putInt(WINDOW_HEIGHT_PREF_KEY, this.getHeight());
		prefs.putInt(WINDOW_XPOS_PREF_KEY, this.getLocation().x);
		prefs.putInt(WINDOW_YPOS_PREF_KEY, this.getLocation().y);
		prefs.putInt(WINDOW_EXTENDED_STATE_KEY, this.getExtendedState());
	}

	public void initGroupTree(YGroup rootGroup) {
		this.groupTree = new GroupTree();
		groupTree.setShowsRootHandles(true);
		LazyTreeNode rootNode = new LazyTreeNode(rootGroup.getId(), rootGroup);
		this.groupTreeModel = new GroupTreeModel(groupTree, rootNode, mainController.getServerFacade());
		this.groupTree.setModel(this.groupTreeModel);
		this.groupTree.collapseRow(0);
		this.treeScrollPane = new JScrollPane(this.groupTree);
		this.treeScrollPane.setPreferredSize(new Dimension(300, 400));
		mainSplitPane.setLeftComponent(this.treeScrollPane);
		initGroupTabPane();
	}

	private void initGroupTabPane() {
		detailsTabbedPane = new DetailsTabbedPane(this, mainController.getServerFacade());
		GroupTreeSelectionListener selectionListener = new GroupTreeSelectionListener(groupTree, detailsTabbedPane);
		this.groupTree.addTreeSelectionListener(selectionListener);
		mainSplitPane.setRightComponent(detailsTabbedPane);
	}

	public MainController getMainController() {
		return mainController;
	}

}
