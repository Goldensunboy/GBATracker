import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class represents the controls for the noise channel
 * @author Andrew Wilder
 */
public class GBATrackerNoiseChannelPanel extends JPanel {
	
	/** Definitions */
	private static final Integer[] DividingRatios = {
		0, 1, 2, 3, 4, 5, 6, 7
	};
	private static final Integer[] Envelopes = DividingRatios;
	
	/** Components used by the panel */
	private JComboBox<Integer> ratioComboBox;
	private JSlider shiftFrequencySlider;
	private JLabel shiftFrequencyLabel;
	private JSlider volumeSlider;
	private JLabel volumeLabel;
	private JCheckBox cutoffCheckBox;
	private JSlider cutoffSlider;
	private JLabel cutoffLabel;
	private JComboBox<Integer> envelopeComboBox;
	private JCheckBox increasingEnvelopeCheckBox;
	private JRadioButton counterWidth15Bits;
	
	/**
	 * Create the UI for the noise channel modifiers
	 * @param controller Reference to the main controller
	 */
	public GBATrackerNoiseChannelPanel(final GBATrackerFrame controller) {
		
		// Initialize JPanel related properties
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Noise Channel"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// Note pitch panel
		JPanel frequencyPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		frequencyPanel.add(new JLabel("Ratio:"));
		ratioComboBox = new JComboBox<>(DividingRatios);
		ratioComboBox.setSelectedItem(new Integer(3));
		ratioComboBox.addMouseListener(new MessageMouseListener(controller, "Frequency dividing ratio (r)"));
		frequencyPanel.add(ratioComboBox);
		shiftFrequencySlider = new JSlider(0, 15);
		shiftFrequencySlider.setValue(3);
		shiftFrequencySlider.setPreferredSize(new Dimension(100, 20));
		shiftFrequencySlider.addMouseListener(new MessageMouseListener(controller, "Shift clock frequency (s)"));
		shiftFrequencySlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				shiftFrequencyLabel.setText("" + shiftFrequencySlider.getValue());
			}
		});
		frequencyPanel.add(shiftFrequencySlider);
		shiftFrequencyLabel = new JLabel("3");
		shiftFrequencyLabel.setPreferredSize(new Dimension(20, 10));
		frequencyPanel.add(shiftFrequencyLabel);
		add(frequencyPanel);
		
		// Volume panel
		JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		volumePanel.add(new JLabel("Volume:"));
		volumeSlider = new JSlider(0, 15);
		volumeSlider.setValue(9);
		volumeSlider.setPreferredSize(new Dimension(100, 20));
		volumeSlider.addMouseListener(new MessageMouseListener(controller, "Initial volume of the note"));
		volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				volumeLabel.setText("" + volumeSlider.getValue());
			}
		});
		volumePanel.add(volumeSlider);
		volumeLabel = new JLabel("9");
		volumeLabel.setPreferredSize(new Dimension(25, 10));
		volumePanel.add(volumeLabel);
		add(volumePanel);
		
		// Note envelope panel
		JPanel envelopePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		envelopePanel.add(new JLabel("Env:"));
		envelopeComboBox = new JComboBox<>(Envelopes);
		envelopeComboBox.setSelectedItem(new Integer(3));
		envelopeComboBox.addMouseListener(new MessageMouseListener(controller, "Envelope volume step time (n/64 s)"));
		envelopePanel.add(envelopeComboBox);
		increasingEnvelopeCheckBox = new JCheckBox("Increasing", false);
		increasingEnvelopeCheckBox.addMouseListener(new MessageMouseListener(controller, "Increasing envelope volume (as opposed to decreasing)"));
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
			}
		});
		cutoffSlider.setEnabled(false);
		cutoffPanel.add(cutoffSlider);
		cutoffLabel = new JLabel("");
		cutoffLabel.setPreferredSize(new Dimension(50, 10));
		cutoffPanel.add(cutoffLabel);
		add(cutoffPanel);
		
		// Counter width panel
		JPanel counterWidthPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		counterWidthPanel.add(new JLabel("Rand:"));
		ButtonGroup counterWidthGroup = new ButtonGroup();
		counterWidth15Bits = new JRadioButton("15 bits", true);
		counterWidth15Bits.addMouseListener(new MessageMouseListener(controller, "Randomization vector: 2^15"));
		counterWidthGroup.add(counterWidth15Bits);
		counterWidthPanel.add(counterWidth15Bits);
		JRadioButton counterWidth7bits = new JRadioButton("7 bits", false);
		counterWidth7bits.addMouseListener(new MessageMouseListener(controller, "Randomization vector: 2^7"));
		counterWidthGroup.add(counterWidth7bits);
		counterWidthPanel.add(counterWidth7bits);
		add(counterWidthPanel);
		add(Box.createVerticalStrut(35));
		
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
	 * Generate a Note object from the selected options
	 * @return The generated Note object
	 */
	public Note createNote() {
		Note note = new Note();
		note.isSquareType = false;
		note.volume = volumeSlider.getValue();
		note.envelopeStep = envelopeComboBox.getSelectedIndex();
		note.increasingEnvelope = increasingEnvelopeCheckBox.isSelected();
		note.hasCutoff = cutoffCheckBox.isSelected();
		note.cutoffValue = cutoffSlider.getValue();
		double r = ratioComboBox.getSelectedIndex();
		note.dividingRatio = r == 0.0 ? 0.5 : r;
		note.shiftClockFrequency = shiftFrequencySlider.getValue();
		note.counterStepIs15Bits = counterWidth15Bits.isSelected();
		return note;
	}
}
