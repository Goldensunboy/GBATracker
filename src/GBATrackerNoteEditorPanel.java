import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The editor panel, with note and song properties
 * @author Andrew Wilder
 */
@SuppressWarnings("serial")
public class GBATrackerNoteEditorPanel extends JPanel {
	
	/** Definitions */
	private static final Integer[] Quantizations = {
		8, 12, 16, 24, 48
	};
	
	/** Components used by the panel */
	private JTextField titleTextField;
	private JTextField bpmTextField;
	private JComboBox<Integer> quantizationComboBox;
	private GBATrackerSquareChannelPanel squareChannelPanel;
	private GBATrackerNoiseChannelPanel noiseChannelPanel;
	private JCheckBox loopCheckBox;
	
	/**
	 * Get the title of the song
	 * @return The title of the song
	 */
	public String getTitle() {
		return titleTextField.getText();
	}
	
	/**
	 * Get the BPM of the song
	 * @return The BPM of the song
	 */
	public String getBPM() {
		return bpmTextField.getText();
	}
	
	/**
	 * Get the editor quantization
	 * @return The editor quantization
	 */
	public int getQuantization() {
		return (Integer) quantizationComboBox.getSelectedItem();
	}
	
	/**
	 * Get a Note object from the appropriate UI subgroup
	 * @param isSquare Whether the note is for a square or noise channel
	 * @return The Note object
	 */
	public Note getNoteFromUI(boolean isSquare) {
		if(isSquare) {
			return squareChannelPanel.createNote();
		} else {
			return noiseChannelPanel.createNote();
		}
	}
	
	/**
	 * Validate the BPM value the user input in the editor panel
	 * @return Whether or not the BPM value is valid
	 */
	public boolean validateBPM() {
		return Pattern.matches("[1-9]\\d*", bpmTextField.getText());
	}
	
	/**
	 * Update the UI components from data in a Note
	 * @param note
	 */
	public void updateUIFromNote(Note note) {
		if(note.isSquareType) {
			squareChannelPanel.updateUIFromNote(note);
		} else {
			noiseChannelPanel.updateUIFromNote(note);
		}
	}
	
	/**
	 * Create the control panel
	 * @param controller Reference to the main controller
	 */
	public GBATrackerNoteEditorPanel(final GBATrackerFrame controller) {
		
		// Initialize JPanel related properties
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// Note properties panel
		JPanel notePropertiesPanel = new JPanel();
		notePropertiesPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		squareChannelPanel = new GBATrackerSquareChannelPanel(controller);
		notePropertiesPanel.add(squareChannelPanel);
		noiseChannelPanel = new GBATrackerNoiseChannelPanel(controller);
		notePropertiesPanel.add(noiseChannelPanel);
		
		// Editor properties panel
		JPanel editorPropertiesPanel = new JPanel();
		editorPropertiesPanel.setLayout(new BoxLayout(editorPropertiesPanel, BoxLayout.Y_AXIS));
		editorPropertiesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Song Properties"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// Title
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		titlePanel.add(new JLabel("Title:"));
		titleTextField = new JTextField(10);
		titleTextField.setText("untitled");
		titleTextField.addMouseListener(new MessageMouseListener(controller, "Title of the song"));
		titleTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				controller.setModifiedTitle(titleTextField.getText());
				controller.setModified();
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				controller.setModifiedTitle(titleTextField.getText());
				controller.setModified();
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				controller.setModifiedTitle(titleTextField.getText());
				controller.setModified();
			}
		});
		titlePanel.add(titleTextField);
		editorPropertiesPanel.add(titlePanel);
		
		// BPM
		JPanel bpmPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		bpmPanel.add(new JLabel("BPM:"));
		bpmTextField = new JTextField(4);
		bpmTextField.setText("150");
		bpmTextField.addMouseListener(new MessageMouseListener(controller, "Speed of the song in beats per minute"));
		bpmTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				controller.setModified();
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				controller.setModified();
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				controller.setModified();
			}
		});
		bpmPanel.add(bpmTextField);
		loopCheckBox = new JCheckBox("Loop", true);
		loopCheckBox.addMouseListener(new MessageMouseListener(controller, "Loop the song"));
		loopCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setLoopingEnabled(loopCheckBox.isSelected());
				controller.setModified();
			}
		});
		bpmPanel.add(loopCheckBox);
		editorPropertiesPanel.add(bpmPanel);
		
		// Quantization
		JPanel quantizationPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		quantizationPanel.add(new JLabel("Quantization:"));
		quantizationComboBox = new JComboBox<>(Quantizations);
		quantizationComboBox.setSelectedItem(new Integer(8));
		quantizationComboBox.addMouseListener(new MessageMouseListener(controller, "Editor quantization"));
		quantizationComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setQuantization((Integer) quantizationComboBox.getSelectedItem());
			}
		});
		quantizationPanel.add(quantizationComboBox);
		editorPropertiesPanel.add(quantizationPanel);
		
		// View
		JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JButton zoomInButton = new JButton("+");
		zoomInButton.addMouseListener(new MessageMouseListener(controller, "Zoom in"));
		zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double newZoom = controller.zoomIn();
				controller.setTooltipText(String.format("%d%%", (int) (newZoom * 100)));
			}
		});
		viewPanel.add(zoomInButton);
		JButton zoomOutButton = new JButton("-");
		zoomOutButton.addMouseListener(new MessageMouseListener(controller, "Zoom out"));
		zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double newZoom = controller.zoomOut();
				controller.setTooltipText(String.format("%d%%", (int) (newZoom * 100)));
			}
		});
		viewPanel.add(zoomOutButton);
		JButton moveLeftButton = new JButton("<");
		moveLeftButton.addMouseListener(new MessageMouseListener(controller, "Move left"));
		moveLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double newPos = controller.moveLeft();
				controller.setTooltipText(String.format("Measure: %.1f", newPos));
			}
		});
		viewPanel.add(moveLeftButton);
		JButton moveRightButton = new JButton(">");
		moveRightButton.addMouseListener(new MessageMouseListener(controller, "Move right"));
		moveRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double newPos = controller.moveRight();
				controller.setTooltipText(String.format("Measure: %.1f", newPos));
			}
		});
		viewPanel.add(moveRightButton);
		editorPropertiesPanel.add(viewPanel);
		
		// Clean up
		editorPropertiesPanel.add(Box.createRigidArea(new Dimension(220, 103)));
		notePropertiesPanel.add(editorPropertiesPanel);
		add(notePropertiesPanel);
	}
	
	/**
	 * Get a CSV representation of the song options
	 * @return The CSV values
	 */
	public String generateCSV() {
		return titleTextField.getText() + "," + bpmTextField.getText() + "," + loopCheckBox.isSelected();
	}
	
	/**
	 * Get looping value
	 * @return True if looping is enabled
	 */
	public boolean getLooping() {
		return loopCheckBox.isSelected();
	}
	
	/**
	 * Update the UI from CSV values generated by generateCSV
	 * @param csv The csv values
	 */
	public void updateFromCSV(String csv) {
		Scanner sc = new Scanner(csv);
		sc.useDelimiter(",");
		try {
			titleTextField.setText(sc.next());
			bpmTextField.setText(sc.next());
			loopCheckBox.setSelected(Boolean.parseBoolean(sc.next()));
		} catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Corrupted file", "Unable to parse file", JOptionPane.ERROR_MESSAGE);
		} finally {
			sc.close();
		}
	}
}
