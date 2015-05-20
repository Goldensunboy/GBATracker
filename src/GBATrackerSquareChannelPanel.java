import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class represents the controls for the square wave channel
 * @author Andrew Wilder
 */
public class GBATrackerSquareChannelPanel extends JPanel {
	
	/** Definitions */
	public static final String[] Notes = {
		"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
	};
	private static final Integer[] Octaves = {
		2, 3, 4, 5, 6, 7
	};
	private static final String[] Duties = {
		"1/8", "1/4", "1/2", "3/4"
	};
	private static final Integer[] Envelopes = {
		0, 1, 2, 3, 4, 5, 6, 7
	};
	private static final Integer[] SweepRates = Envelopes;
	private static final Integer[] SweepSteps = Envelopes;
	
	/** Components used by the panel */
	private JComboBox<String> noteComboBox;
	private JComboBox<Integer> octaveComboBox;
	private JComboBox<String> dutyComboBox;
	private JComboBox<Integer> envelopeComboBox;
	private JCheckBox cutoffCheckBox;
	private JSlider cutoffSlider;
	private JLabel cutoffLabel;
	private JCheckBox increasingEnvelopeCheckBox;
	private JComboBox<Integer> sweepRateComboBox;
	private JComboBox<Integer> sweepStepComboBox;
	private JCheckBox increasingSweepCheckBox;
	private JSlider volumeSlider;
	private JLabel volumeLabel;
	private static boolean allowUpdates = true;
	
	/**
	 * Create the UI for the square wave channel modifiers
	 * @param controller Reference to the main controller
	 */
	public GBATrackerSquareChannelPanel(final GBATrackerFrame controller) {
		
		// Initialize JPanel related properties
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Square Channels"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// ActionListener for updating the selected note
		ActionListener updateSelectedNoteListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(allowUpdates) {
					controller.updateSelectedNote(createNote());
				}
			}
		};
		
		// Note pitch panel
		JPanel pitchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		pitchPanel.add(new JLabel("Tone:"));
		noteComboBox = new JComboBox<>(Notes);
		noteComboBox.addMouseListener(new MessageMouseListener(controller, "The tone for a note"));
		noteComboBox.addActionListener(updateSelectedNoteListener);
		pitchPanel.add(noteComboBox);
		octaveComboBox = new JComboBox<>(Octaves);
		octaveComboBox.setSelectedItem(new Integer(4));
		octaveComboBox.addMouseListener(new MessageMouseListener(controller, "The octave for a note"));
		octaveComboBox.addActionListener(updateSelectedNoteListener);
		pitchPanel.add(octaveComboBox);
		dutyComboBox = new JComboBox<>(Duties);
		dutyComboBox.setSelectedItem("1/2");
		dutyComboBox.addMouseListener(new MessageMouseListener(controller, "Wave duty cycle"));
		dutyComboBox.addActionListener(updateSelectedNoteListener);
		pitchPanel.add(dutyComboBox);
		add(pitchPanel);
		
		// Volume panel
		JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		volumePanel.add(new JLabel("Volume:"));
		volumeSlider = new JSlider(0, 15);
		volumeSlider.setValue(15);
		volumeSlider.setPreferredSize(new Dimension(100, 20));
		volumeSlider.addMouseListener(new MessageMouseListener(controller, "Initial volume of the note"));
		volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				volumeLabel.setText("" + volumeSlider.getValue());
				if(allowUpdates) {
					controller.updateSelectedNote(createNote());
				}
			}
		});
		
		volumePanel.add(volumeSlider);
		volumeLabel = new JLabel("15");
		volumeLabel.setPreferredSize(new Dimension(25, 10));
		volumePanel.add(volumeLabel);
		add(volumePanel);
		
		// Note envelope panel
		JPanel envelopePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		envelopePanel.add(new JLabel("Env:"));
		envelopeComboBox = new JComboBox<>(Envelopes);
		envelopeComboBox.setSelectedItem(new Integer(2));
		envelopeComboBox.addMouseListener(new MessageMouseListener(controller, "Envelope volume step time (n/64 s)"));
		envelopeComboBox.addActionListener(updateSelectedNoteListener);
		envelopePanel.add(envelopeComboBox);
		increasingEnvelopeCheckBox = new JCheckBox("Increasing", false);
		increasingEnvelopeCheckBox.addMouseListener(new MessageMouseListener(controller, "Increasing envelope volume (as opposed to decreasing)"));
		increasingEnvelopeCheckBox.addActionListener(updateSelectedNoteListener);
		envelopePanel.add(increasingEnvelopeCheckBox);
		add(envelopePanel);
		
		// Cutoff panel
		JPanel cutoffPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		cutoffCheckBox = new JCheckBox("Cutoff", false);
		cutoffCheckBox.addMouseListener(new MessageMouseListener(controller, "Cutoff the note early"));
		cutoffCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean selected = cutoffCheckBox.isSelected();
				cutoffSlider.setEnabled(selected);
				cutoffLabel.setText(selected ? String.format("%d ms", ((64 - cutoffSlider.getValue()) * 1000) >> 8) : "");
				if(allowUpdates) {
					controller.updateSelectedNote(createNote());
				}
			}
		});
		cutoffPanel.add(cutoffCheckBox);
		cutoffSlider = new JSlider(0, 63);
		cutoffSlider.setValue(0);
		cutoffSlider.setPreferredSize(new Dimension(80, 20));
		cutoffSlider.addMouseListener(new MessageMouseListener(controller, "Cutoff time in ms"));
		cutoffSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				cutoffLabel.setText(String.format("%d ms", ((64 - cutoffSlider.getValue()) * 1000) >> 8));
				if(allowUpdates) {
					controller.updateSelectedNote(createNote());
				}
			}
		});
		cutoffSlider.setEnabled(false);
		cutoffPanel.add(cutoffSlider);
		cutoffLabel = new JLabel("");
		cutoffLabel.setPreferredSize(new Dimension(50, 10));
		cutoffPanel.add(cutoffLabel);
		add(cutoffPanel);
		
		// Sweep rate panel
		JPanel sweepRatePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		sweepRatePanel.add(new JLabel("Sweep rate:"));
		sweepRateComboBox = new JComboBox<>(SweepRates);
		sweepRateComboBox.addMouseListener(new MessageMouseListener(controller, "Rate of sweep change"));
		sweepRateComboBox.addActionListener(updateSelectedNoteListener);
		sweepRatePanel.add(sweepRateComboBox);
		add(sweepRatePanel);
		
		// Sweep step panel
		JPanel sweepStepPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		sweepStepPanel.add(new JLabel("Step:"));
		sweepStepComboBox = new JComboBox<>(SweepSteps);
		sweepStepComboBox.addMouseListener(new MessageMouseListener(controller, "Time between sweep steps (0 = no sweep)"));
		sweepStepComboBox.addActionListener(updateSelectedNoteListener);
		sweepStepPanel.add(sweepStepComboBox);
		increasingSweepCheckBox = new JCheckBox("Increasing", false);
		increasingSweepCheckBox.addMouseListener(new MessageMouseListener(controller, "Sweep direction"));
		increasingSweepCheckBox.addActionListener(updateSelectedNoteListener);
		sweepStepPanel.add(increasingSweepCheckBox);
		add(sweepStepPanel);
		
		// Play note panel
		JPanel playNotePanel = new JPanel();
		JButton playNoteButton = new JButton("Test note");
		playNoteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Note note = createNote();
				note.testSound();
				controller.setTooltipText(note.toString());
			}
		});
		playNoteButton.addMouseListener(new MessageMouseListener(controller, "Test the note sound"));
		playNotePanel.add(playNoteButton);
		add(playNotePanel);
	}
	
	/**
	 * Update the UI from a Note object's properties
	 * @param note The Note object
	 */
	public void updateUIFromNote(Note note) {
		allowUpdates = false;
		noteComboBox.setSelectedIndex(note.musicalNote);
		octaveComboBox.setSelectedItem(new Integer(note.octave));
		switch((int) (note.dutyCycle * 4)) {
		case 0:
			dutyComboBox.setSelectedItem("1/8");
			break;
		case 1:
			dutyComboBox.setSelectedItem("1/4");
			break;
		case 2:
			dutyComboBox.setSelectedItem("1/2");
			break;
		case 3:
			dutyComboBox.setSelectedItem("3/4");
			break;
		}
		volumeSlider.setValue(note.volume);
		volumeLabel.setText("" + note.volume);
		envelopeComboBox.setSelectedIndex(note.envelopeStep);
		increasingEnvelopeCheckBox.setSelected(note.increasingEnvelope);
		cutoffSlider.setValue(note.cutoffValue);
		if(note.hasCutoff) {
			cutoffCheckBox.setSelected(true);
			cutoffSlider.setEnabled(true);
		} else {
			cutoffCheckBox.setSelected(false);
			cutoffSlider.setEnabled(false);
		}
		cutoffLabel.setText(note.hasCutoff ? String.format("%d ms", ((64 - cutoffSlider.getValue()) * 1000) >> 8) : "");
		sweepRateComboBox.setSelectedIndex(note.sweepRate);
		sweepStepComboBox.setSelectedIndex(note.sweepStep);
		increasingSweepCheckBox.setSelected(note.increasingSweep);
		allowUpdates = true;
	}
	
	/**
	 * Generate a Note object from the selected options
	 * @return The generated Note object
	 */
	public Note createNote() {
		Note note = new Note(true);
		note.musicalNote = noteComboBox.getSelectedIndex();
		note.octave = (Integer) octaveComboBox.getSelectedItem();
		switch((String) dutyComboBox.getSelectedItem()) {
		case "1/8":
			note.dutyCycle = 0.125;
			break;
		case "1/4":
			note.dutyCycle = 0.25;
			break;
		case "1/2":
			note.dutyCycle = 0.5;
			break;
		case "3/4":
			note.dutyCycle = 0.75;
			break;
		}
		note.volume = volumeSlider.getValue();
		note.envelopeStep = envelopeComboBox.getSelectedIndex();
		note.increasingEnvelope = increasingEnvelopeCheckBox.isSelected();
		note.hasCutoff = cutoffCheckBox.isSelected();
		note.cutoffValue = cutoffSlider.getValue();
		note.sweepRate = sweepRateComboBox.getSelectedIndex();
		note.sweepStep = sweepStepComboBox.getSelectedIndex();
		note.increasingSweep = increasingSweepCheckBox.isSelected();
		return note;
	}
}
