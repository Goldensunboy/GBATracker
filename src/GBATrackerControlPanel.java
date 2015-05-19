import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The control panel, with buttons for saving, export and playback
 * @author Andrew Wilder
 */
public class GBATrackerControlPanel extends JPanel {
	
	public GBATrackerControlPanel(final GBATrackerFrame controller) {
		
		// Initialize JPanel related properties
		super(new FlowLayout(FlowLayout.LEADING));
		
		// Load button
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.loadFile();
			}
		});
		loadButton.addMouseListener(new MessageMouseListener(controller, "Load a file from disk"));
		add(loadButton);
		
		// Save button
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveFile();
			}
		});
		saveButton.addMouseListener(new MessageMouseListener(controller, "Save the currently opened file"));
		add(saveButton);

		// Save as button
		JButton saveAsButton = new JButton("Save as");
		saveAsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveFileAs();
			}
		});
		saveAsButton.addMouseListener(new MessageMouseListener(controller, "Save as a new file"));
		add(saveAsButton);
		
		// Export button
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.loadFile();
			}
		});
		exportButton.addMouseListener(new MessageMouseListener(controller, "Export song to C file"));
		add(exportButton);
		
		add(new JLabel(" - "));
		
		// Play from start button
		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.play();
			}
		});
		playButton.addMouseListener(new MessageMouseListener(controller, "Play from start of file"));
		add(playButton);
		
		// Play from here button
		JButton playHereButton = new JButton("Play here");
		playHereButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.playHere();
			}
		});
		playHereButton.addMouseListener(new MessageMouseListener(controller, "Play from here"));
		add(playHereButton);
		
		// Play from here button
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.stop();
			}
		});
		stopButton.addMouseListener(new MessageMouseListener(controller, "Stop the music"));
		add(stopButton);
		
		add(new JLabel(" - "));
		
		JButton aboutButton = new JButton("About");
		aboutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "GBA Tracker 1.0\nBy Andrew Wilder\nandrew.m.wilder@gmail.com", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		aboutButton.addMouseListener(new MessageMouseListener(controller, "About this program"));
		add(aboutButton);
	}
}
