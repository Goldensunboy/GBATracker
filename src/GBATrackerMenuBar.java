import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * This class represents the menu bar and its options
 * @author Andrew Wilder
 */
@SuppressWarnings("serial")
public class GBATrackerMenuBar extends JMenuBar {

	/**
	 * Create and populate the menu bar for the application
	 * @param controller
	 */
	public GBATrackerMenuBar(final GBATrackerFrame controller) {

		// Create file menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);

		JMenuItem openFileMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.loadFile();
			}
		});
		fileMenu.add(openFileMenuItem);

		JMenuItem saveFileMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
		saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.saveFile();
			}
		});
		fileMenu.add(saveFileMenuItem);

		JMenuItem saveAsFileMenuItem = new JMenuItem("Save As", KeyEvent.VK_A);
		saveAsFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		saveAsFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.saveFileAs();
			}
		});
		fileMenu.add(saveAsFileMenuItem);

		JMenuItem exportFileMenuItem = new JMenuItem("Export", KeyEvent.VK_E);
		exportFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		exportFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.exportFile();
			}
		});
		fileMenu.add(exportFileMenuItem);

		JMenuItem quitFileMenuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
		quitFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quitFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.exitConfirmation();
			}
		});
		fileMenu.add(quitFileMenuItem);

		// Create edit menu
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		add(editMenu);

		JMenuItem zoomInEditMenuItem = new JMenuItem("Zoom In", KeyEvent.VK_I);
		zoomInEditMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0));
		zoomInEditMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.zoomIn();
			}
		});
		editMenu.add(zoomInEditMenuItem);

		JMenuItem zoomOutEditMenuItem = new JMenuItem("Zoom Out", KeyEvent.VK_O);
		zoomOutEditMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0));
		zoomOutEditMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.zoomOut();
			}
		});
		editMenu.add(zoomOutEditMenuItem);

		JMenuItem clearEditMenuItem = new JMenuItem("Clear All", KeyEvent.VK_C);
		clearEditMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		clearEditMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.clearAll();
			}
		});
		editMenu.add(clearEditMenuItem);

		// Create play menu
		JMenu playMenu = new JMenu("Play");
		playMenu.setMnemonic(KeyEvent.VK_P);
		add(playMenu);

		JMenuItem playAllPlayMenuItem = new JMenuItem("Play All", KeyEvent.VK_P);
		playAllPlayMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		playAllPlayMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.play();
			}
		});
		playMenu.add(playAllPlayMenuItem);

		JMenuItem playHerePlayMenuItem = new JMenuItem("Play Here", KeyEvent.VK_H);
		playHerePlayMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		playHerePlayMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.playHere();
			}
		});
		playMenu.add(playHerePlayMenuItem);

		JMenuItem stopPlayMenuItem = new JMenuItem("Stop", KeyEvent.VK_S);
		stopPlayMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		stopPlayMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.stop();
			}
		});
		playMenu.add(stopPlayMenuItem);

		// Create info menu
		JMenu infoMenu = new JMenu("Info");
		infoMenu.setMnemonic(KeyEvent.VK_I);
		add(infoMenu);

		JMenuItem hintInfoMenuItem = new JMenuItem("Hint", KeyEvent.VK_H);
		hintInfoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		hintInfoMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.displayNextUsageHint();
			}
		});
		infoMenu.add(hintInfoMenuItem);

		JMenuItem aboutInfoMenuItem = new JMenuItem("About", KeyEvent.VK_A);
		aboutInfoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		aboutInfoMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.about();
			}
		});
		infoMenu.add(aboutInfoMenuItem);
	}
}
