import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The main window of the GBA Tracker application
 * @author Andrew Wilder
 */
public class GBATrackerFrame extends JFrame {
	
	/** Panels used by the application */
	private GBATrackerControlPanel controlPanel;
	private GBATrackerNoteEditorPanel noteEditorPanel;
	private GBATrackerSimulationPanel simulationPanel;
	private JLabel tooltipLabel = new JLabel(" ");
	
	/**
	 * Load a song file
	 */
	public void loadFile() {
		// TODO
	}
	
	/**
	 * Save the current file
	 */
	public void saveFile() {
		// TODO
	}
	
	/**
	 * Save as a new file
	 */
	public void saveFileAs() {
		// TODO
	}
	
	/**
	 * Play the file from the start
	 */
	public void play() {
		// TODO
	}
	
	/**
	 * Play the file from an offset
	 */
	public void playHere() {
		// TODO
	}
	
	/**
	 * Stop playing the file
	 */
	public void stop() {
		// TODO
	}
	
	/**
	 * Zoom in, on the editor
	 */
	public double zoomIn() {
		return simulationPanel.zoomIn();
	}
	
	/**
	 * Zoom out, on the editor
	 */
	public double zoomOut() {
		return simulationPanel.zoomOut();
	}
	
	/**
	 * Scroll left in the editor
	 */
	public double moveLeft() {
		return simulationPanel.moveLeft();
	}
	
	/**
	 * Scroll right in the editor
	 */
	public double moveRight() {
		return simulationPanel.moveRight();
	}
	
	/**
	 * Set the text of the tooltip at the bottom of the screen
	 * @param text The message to display
	 */
	public void setTooltipText(String text) {
		tooltipLabel.setText(text);
	}
	
	/**
	 * Set a new quantization level for the editor
	 * @param quantization The new quantization
	 */
	public void setQuantization(int quantization) {
		simulationPanel.setQuantization(quantization);
	}
	
	/**
	 * Retrieve a Note object from the UI
	 * @param isSqaure Whether this is from a square or noise channel
	 * @return The Note
	 */
	public Note getNoteFromUI(boolean isSqaure) {
		return noteEditorPanel.getNoteFromUI(isSqaure);
	}
	
	/**
	 * Update the editor panel with information from a Note
	 * @param note
	 */
	public void updateUIFromNote(Note note) {
		noteEditorPanel.updateUIFromNote(note);
	}
	
	/**
	 * Update the currently selected note in real time
	 * @param note The Note data providing the update
	 */
	public void updateSelectedNote(Note note) {
		simulationPanel.updateSelectedNote(note);
	}
	
	/**
	 * Enable or disable looping
	 * @param enable If true, loop the song
	 */
	public void setLoopingEnabled(boolean enable) {
		simulationPanel.setLoopingEnabled(enable);
	}
	
	/**
	 * Create an instance of the main program's window
	 */
	public GBATrackerFrame() {
		
		// Initialize JFrame related properties
		super("GBA Tracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		// Set up the panels
		controlPanel = new GBATrackerControlPanel(this);
		noteEditorPanel = new GBATrackerNoteEditorPanel(this);
		simulationPanel = new GBATrackerSimulationPanel(this);
		JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		messagePanel.add(tooltipLabel);
		content.add(controlPanel);
		content.add(noteEditorPanel);
		content.add(simulationPanel);
		content.add(messagePanel);
		
		// Generate noise buffer for Notes
		Note.generateNoiseWaveform();
		
		// Finalize JFrame properties
		setContentPane(content);
		pack();
		setVisible(true);
	}
	
	/**
	 * Create an instance of the program's main window.
	 * @param args Unused
	 */
	public static void main(String[] args) {
		new GBATrackerFrame();
	}
}
