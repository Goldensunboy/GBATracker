import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The editor panel, with note and song properties
 * @author Andrew Wilder
 */
public class GBATrackerNoteEditorPanel extends JPanel {
	
	/** Definitions */
	private static final Integer[] Quantizations = {
		8, 12, 16, 24, 32, 48
	};
	
	/** Components used by the panel */
	private JTextField titleTextField;
	private JTextField bpmTextField;
	private JComboBox<Integer> quantizationComboBox;
	private GBATrackerSquareChannelPanel squareChannelPanel;
	private GBATrackerNoiseChannelPanel noiseChannelPanel;
	
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
		
		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel1.add(new JLabel("Title:"));
		titleTextField = new JTextField(10);
		titleTextField.setText("mysong");
		titleTextField.addMouseListener(new MessageMouseListener(controller, "Title of the song"));
		panel1.add(titleTextField);
		editorPropertiesPanel.add(panel1);
		
		JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel2.add(new JLabel("BPM:"));
		bpmTextField = new JTextField(4);
		bpmTextField.setText("150");
		bpmTextField.addMouseListener(new MessageMouseListener(controller, "Speed of the song in beats per minute"));
		panel2.add(bpmTextField);
		editorPropertiesPanel.add(panel2);
		
		JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel3.add(new JLabel("Quantization:"));
		quantizationComboBox = new JComboBox<>(Quantizations);
		quantizationComboBox.setSelectedItem(new Integer(8));
		quantizationComboBox.addMouseListener(new MessageMouseListener(controller, "Editor quantization"));
		panel3.add(quantizationComboBox);
		editorPropertiesPanel.add(panel3);
		
		JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JButton zoomInButton = new JButton("+");
		zoomInButton.addMouseListener(new MessageMouseListener(controller, "Zoom in"));
		zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double newZoom = controller.zoomIn();
				controller.setTooltipText(String.format("%d%%", (int) (newZoom * 100)));
			}
		});
		panel4.add(zoomInButton);
		JButton zoomOutButton = new JButton("-");
		zoomOutButton.addMouseListener(new MessageMouseListener(controller, "Zoom out"));
		zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double newZoom = controller.zoomOut();
				controller.setTooltipText(String.format("%d%%", (int) (newZoom * 100)));
			}
		});
		panel4.add(zoomOutButton);
		JButton moveLeftButton = new JButton("<");
		moveLeftButton.addMouseListener(new MessageMouseListener(controller, "Move left"));
		moveLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.moveLeft();
			}
		});
		panel4.add(moveLeftButton);
		JButton moveRightButton = new JButton(">");
		moveRightButton.addMouseListener(new MessageMouseListener(controller, "Move right"));
		moveRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.moveRight();
			}
		});
		panel4.add(moveRightButton);
		editorPropertiesPanel.add(panel4);
		
		// Clean up
		editorPropertiesPanel.add(Box.createRigidArea(new Dimension(220, 107)));
		notePropertiesPanel.add(editorPropertiesPanel);
		add(notePropertiesPanel);
	}
}
