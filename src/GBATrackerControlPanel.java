import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The control panel, with buttons for saving, export and playback
 * @author Andrew Wilder
 */
@SuppressWarnings("serial")
public class GBATrackerControlPanel extends JPanel {

	/** These are the icons used by the control panel */
	private static Icon loadIcon, saveIcon, saveAsIcon, exportIcon, playIcon, playHereIcon, stopIcon, aboutIcon, hintIcon;
	static {
		try {
			loadIcon     = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/Open.png")));
			saveIcon     = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/Save.png")));
			saveAsIcon   = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/SaveAs.png")));
			exportIcon   = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/Export.png")));
			playIcon     = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/Play.png")));
			playHereIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/PlayHere.png")));
			stopIcon     = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/Stop.png")));
			aboutIcon    = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/About.png")));
			hintIcon     = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("res/Hint.png")));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the UI for the control panel
	 * @param controller
	 */
	public GBATrackerControlPanel(final GBATrackerFrame controller) {

		// Initialize JPanel related properties
		super(new FlowLayout(FlowLayout.LEADING));

		// Load button
		JButton loadButton = new JButton(loadIcon);
		loadButton.setMargin(new Insets(0, 0, 0, 0));
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.loadFile();
			}
		});
		loadButton.addMouseListener(new MessageMouseListener(controller, "Load a file from disk"));
		add(loadButton);

		// Save button
		JButton saveButton = new JButton(saveIcon);
		saveButton.setMargin(new Insets(0, 0, 0, 0));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveFile();
			}
		});
		saveButton.addMouseListener(new MessageMouseListener(controller, "Save the currently opened file"));
		add(saveButton);

		// Save as button
		JButton saveAsButton = new JButton(saveAsIcon);
		saveAsButton.setMargin(new Insets(0, 0, 0, 0));
		saveAsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveFileAs();
			}
		});
		saveAsButton.addMouseListener(new MessageMouseListener(controller, "Save as a new file"));
		add(saveAsButton);

		// Export button
		JButton exportButton = new JButton(exportIcon);
		exportButton.setMargin(new Insets(0, 0, 0, 0));
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.exportFile();
			}
		});
		exportButton.addMouseListener(new MessageMouseListener(controller, "Export song to C file"));
		add(exportButton);

		add(new JLabel(" - "));

		// Play from start button
		JButton playButton = new JButton(playIcon);
		playButton.setMargin(new Insets(0, 0, 0, 0));
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.play();
			}
		});
		playButton.addMouseListener(new MessageMouseListener(controller, "Play from start of file"));
		add(playButton);

		// Play from here button
		JButton playHereButton = new JButton(playHereIcon);
		playHereButton.setMargin(new Insets(0, 0, 0, 0));
		playHereButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.playHere();
			}
		});
		playHereButton.addMouseListener(new MessageMouseListener(controller, "Play from this measure"));
		add(playHereButton);

		// Play from here button
		JButton stopButton = new JButton(stopIcon);
		stopButton.setMargin(new Insets(0, 0, 0, 0));
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.stop();
			}
		});
		stopButton.addMouseListener(new MessageMouseListener(controller, "Stop the music"));
		add(stopButton);

		add(new JLabel(" - "));

		JButton hintButton = new JButton(hintIcon);
		hintButton.setMargin(new Insets(0, 0, 0, 0));
		hintButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.displayNextUsageHint();
			}
		});
		hintButton.addMouseListener(new MessageMouseListener(controller, "Display usage hints"));
		add(hintButton);

		JButton aboutButton = new JButton(aboutIcon);
		aboutButton.setMargin(new Insets(0, 0, 0, 0));
		aboutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.about();
			}
		});
		aboutButton.addMouseListener(new MessageMouseListener(controller, "About this program"));
		add(aboutButton);
	}
}
