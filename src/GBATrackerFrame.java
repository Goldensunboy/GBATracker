import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The main window of the GBA Tracker application
 * @author Andrew Wilder
 */
public class GBATrackerFrame extends JFrame {
	
	/** Definitions */
	private static final String APPLICATION_TITLE = "GBA Tracker";
	private static final String FILE_EXTENSION = "gbt";
	
	/** Panels used by the application */
	private GBATrackerControlPanel controlPanel;
	private GBATrackerNoteEditorPanel noteEditorPanel;
	private GBATrackerSimulationPanel simulationPanel;
	private JLabel tooltipLabel = new JLabel(" ");
	private File openFile = null;
	private boolean modification = false;
	private String songTitle = "untitled";
	
	/**
	 * Warn the user if they make an error
	 * @param msg The error message
	 */
	private void warningMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Load a song file
	 */
	public void loadFile() {
		
		// Save warning if modified
		if(modification) {
			if(JOptionPane.showConfirmDialog(this, "You have unsaved changes.\nOpen another file anyway?",
					"Are you sure?", JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_CANCEL_OPTION) {
				return;
			}
		}
		
		// Select a file
		File oldFile = openFile;
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "GBA Tracker files", FILE_EXTENSION);
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	openFile = chooser.getSelectedFile();

	    	// Send the CSV data to the editor panel and simulation panel
	    	Scanner sc = null;
	    	try {
	    		sc = new Scanner(openFile);
	    		noteEditorPanel.updateFromCSV(sc.nextLine());
	    		simulationPanel.populateFromString(sc.nextLine());
	    		modification = false;
	    		songTitle = noteEditorPanel.getTitle();
	    		setTitle(songTitle + " - " + APPLICATION_TITLE);
	    	} catch(FileNotFoundException e) {
	    		JOptionPane.showMessageDialog(this, "File not found:\n" + openFile.getName(), "Unable to open file", JOptionPane.ERROR_MESSAGE);
	    		openFile = oldFile;
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    		JOptionPane.showMessageDialog(this, "Corrupted file", "Unable to parse file:\n" + openFile, JOptionPane.ERROR_MESSAGE);
	    		openFile = oldFile;
	    	} finally {
	    		if(sc != null) {
	    			sc.close();
	    		}
	    	}
	    }
	}
	
	/**
	 * Save the current file
	 */
	public void saveFile() {
		
		// Must have an opened file
		if(openFile == null) {
			saveFileAs();
			return;
		}
		
		// Save the file
		try {
			PrintWriter pw = new PrintWriter(openFile);
			pw.println(noteEditorPanel.generateCSV());
			pw.println(simulationPanel.generateCSV());
			pw.close();
			modification = false;
			setTitle(songTitle + " - " + APPLICATION_TITLE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Unable to save file:\n" + e.getMessage(), "Error saving file", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Save as a new file
	 */
	public void saveFileAs() {
		
		// Must have valid song name
		if(!validName(songTitle)) {
			warningMessage("Song title must be a valid C identifier");
			return;
		}
		
		// Select a file
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "GBA Tracker files", FILE_EXTENSION);
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	openFile = chooser.getSelectedFile();
	    	if(!Pattern.matches(".*\\." + FILE_EXTENSION, openFile.getName())) {
	    		openFile = new File(openFile.getPath() + "." + FILE_EXTENSION);
	    	}
	    	// Call the regular save file function
	    	saveFile();
	    }
	}
	
	/**
	 * Play the file from the start
	 */
	public void play() {
		simulationPanel.play();
	}
	
	/**
	 * Play the file from an offset
	 */
	public void playHere() {
		simulationPanel.playHere();
	}
	
	/**
	 * Stop playing the file
	 */
	public void stop() {
		simulationPanel.stop();
	}
	
	/**
	 * Tell the program that a modification was made (for save warnings)
	 */
	public void setModified() {
		if(!modification) {
			modification = true;
			setTitle(songTitle + "* - " + APPLICATION_TITLE);
		}
	}
	
	/**
	 * Change the title bar name for the song
	 * @param newTitle The new title
	 */
	public void setModifiedTitle(String newTitle) {
		if("".equals(newTitle)) {
			newTitle = "(blank)";
		}
		songTitle = newTitle;
		setTitle(newTitle + (modification ? "*" : "") + " - " + APPLICATION_TITLE);
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
	 * Is this name a valid C identifier?
	 * @param name
	 * @return
	 */
	private boolean validName(String name) {
		return Pattern.matches("[a-zA-Z_][a-zA-Z_0-9]*", name);
	}
	
	/**
	 * Create an instance of the main program's window
	 */
	public GBATrackerFrame() {
		
		// Initialize JFrame related properties
		super("untitled - " + APPLICATION_TITLE);
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
