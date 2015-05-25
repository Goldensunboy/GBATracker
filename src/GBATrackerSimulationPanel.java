import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.JOptionPane;

/**
 * The simulation panel for showing the timeline of notes
 * @author Andrew Wilder
 */
@SuppressWarnings("serial")
public class GBATrackerSimulationPanel extends JComponent {

	/** Definitions */
	private static final double MIN_SCROLL = -0.25;
	private static final double MAX_ZOOM = 3.0;
	private static final double MIN_ZOOM = 0.2;
	private static final double ZOOM_DELTA = 0.8;
	private static final double SCROLL_DELTA = 0.1;
	private static final int NOTE_SIZE = 35;
	private static final int[] PlaySliderPolygonX = {-8, 8, 0};
	private static final int[] PlaySliderPolygonY = {0, 0, 8};
	private static final int FRAMERATE = 60;
	private static final Dimension SimulationPanelSize = new Dimension(800, 260);

	/** Variables that define the simulation */
	private double zoom = 0.7;
	private double scroll = -0.1;
	private int quantization = 8;
	private int clickStep = 0, clickChannel = 0;
	private List<EditorChannel> channels = new ArrayList<>();
	private EditorNote selectedNote = null;
	private int selectedNoteChannel;
	private boolean simulating = false;
	private double playSlider = 0;
	private int endStep = 48;
	private int loopStep = 0;
	private boolean looping = true;
	private int playingStep = 0;
	private double startScroll = 0;

	/**
	 * Get the maximum step for the notes currently placed
	 * @return The maximum step, in 48ths
	 */
	private int getMaxStep() {
		int maxStep = 0;
		for(int i = 0; i < 3; ++i) {
			for(EditorNote edn : channels.get(i).notes) {
				if(edn.step > maxStep) {
					maxStep = edn.step;
				}
			}
		}
		return maxStep;
	}

	/**
	 * The ActionListener housing the update function for the simulation
	 */
	private static class SimulationListener implements ActionListener {

		/** MS elapsed after last played note */
		private int elapsedMS = 0;

		/**
		 * Reference to the simulation panel's fields
		 */
		GBATrackerSimulationPanel simPanel;
		public SimulationListener(GBATrackerSimulationPanel simPanel) {
			this.simPanel = simPanel;
		}

		/**
		 * Play a note of music
		 */
		private void playNote() {

			// Get the notes to play
			EditorNote edn = new EditorNote(null, simPanel.playingStep);
			Note[] playNotes = {null, null, null};
			for(int i = 0; i < 3; ++i) {
				EditorChannel edc = simPanel.channels.get(i);
				if(edc.notes.contains(edn)) {
					for(EditorNote edn2 : edc.notes) {
						if(edn2.equals(edn)) {
							playNotes[i] = edn2.note;
							playNotes[i].prepareBuf(i != 1);
							break;
						}
					}
				}
			}

			// Play available notes
			for(int i = 0; i < 3; ++i) {
				if(playNotes[i] != null) {
					playNotes[i].playBuf(simPanel.channels.get(i).channel);
				}
			}

			// Move the playing step
			if(++simPanel.playingStep >= simPanel.endStep) {

				// If we've reached the end of playback...
				if(simPanel.looping) {

					// If looping, set play step to loop marker and scroll back to it
					simPanel.playingStep = simPanel.loopStep;
					simPanel.scroll = simPanel.playingStep / 48.0 - 0.1;
				} else {

					// If not looping, reset scroll and stop simulating
					simPanel.scroll = simPanel.startScroll;
					simPanel.simulating = false;
					simPanel.simulationTimer.stop();
				}
			}
		}

		/**
		 * Update graphics, and play a note at appropriate times
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			// New thread to help keep the timer going the right speed
			new Thread() {
				public void run() {
					// Move the arrow
					simPanel.playSlider = (simPanel.playingStep + (double) elapsedMS / (5000 / simPanel.controller.getBPM())) / 48;

					// Scroll the screen
					if((simPanel.playSlider - simPanel.scroll) * simPanel.getWidth() * simPanel.zoom > simPanel.getWidth()) {
						simPanel.scroll += 1 / simPanel.zoom;
					}

					// Play a note
					if((elapsedMS += 1000 / FRAMERATE) > 5000 / simPanel.controller.getBPM()) {
						elapsedMS %= 5000 / simPanel.controller.getBPM();
						playNote();
					}

					// Repaint stuff
					simPanel.controller.updateAnimation((simPanel.playSlider * 4) % 1);
					simPanel.repaint();
				}
			}.start();
		}
	};
	private Timer simulationTimer = null;

	/**
	 * Get the BPM
	 */
	private final GBATrackerFrame controller;
	public int getBPM() {
		return controller.getBPM();
	}

	/**
	 * Clear all the notes
	 */
	public void clearAll() {
		for(int i = 0; i < 3; ++i) {
			channels.get(i).notes.clear();
		}
		repaint();
	}

	/**
	 * Initialize the simulation panel and the simulation variables
	 * @param controller Reference to the main controller
	 */
	public GBATrackerSimulationPanel(final GBATrackerFrame controller) {

		// No actual components, just a rigid area to maintain window size
		this.controller = controller;
		setPreferredSize(SimulationPanelSize);

		// Instantiate the channel data
		channels.add(new EditorChannel()); // Channel 1 (square w/ sweep)
		channels.add(new EditorChannel()); // Channel 2 (square)
		channels.add(new EditorChannel()); // Channel 4 (noise)

		// Create the MouseListener that handling clicking
		addMouseListener(new MouseListener() {

			/**
			 * Handle mouse clicks
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				
				// Do nothing while simulating
				if(simulating || e.getButton() == 4 || e.getButton() == 5) {
					return;
				}

				// Which channel was clicked?
				clickChannel = e.getY() / (getHeight() >> 2) - 1;
				if(clickChannel < -1) {
					return;
				}

				// Which step was clicked?
				double bars = scroll * quantization;
				double measureWidth = getWidth() * zoom;
				double clickBars = bars + e.getX() / measureWidth * quantization;
				clickStep = (int) Math.round(clickBars);
				if(clickStep < bars) {
					++clickStep;
				} else if(clickStep > bars + quantization / zoom) {
					--clickStep;
				}
				if(clickStep < 0) {
					return;
				}
				clickStep *= 48 / quantization;
				controller.setModified();

				// Was the end marker clicked?
				if(clickChannel == -1) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						if(clickStep > 0) {
							endStep = Math.max(clickStep, getMaxStep() + 48 / quantization);
							if(loopStep >= endStep) {
								loopStep = endStep - 48 / quantization;
							}
						}
					} else {
						loopStep = clickStep;
						if(loopStep >= endStep) {
							loopStep = endStep - 48 / quantization;
						}
					}
					repaint();
					return;
				} else if(clickStep >= endStep) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						endStep = clickStep + 48 / quantization;
					}
				}

				// Is there already a note here?
				Note newNote = controller.getNoteFromUI(clickChannel < 2);
				EditorNote newEdNote = new EditorNote(newNote, clickStep);
				if(channels.get(clickChannel).notes.contains(newEdNote)) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						// If there is, play the existing note and update the UI
						for(EditorNote n : channels.get(clickChannel).notes) {
							if(n.step == clickStep) {
								controller.updateUIFromNote(n.note);
								if(n.equals(selectedNote) && clickChannel == selectedNoteChannel) {
									selectedNote = null;
								} else {
									selectedNote = n;
									selectedNoteChannel = clickChannel;
								}
								break;
							}
						}
					} else {
						if(newNote.equals(selectedNote) && clickChannel == selectedNoteChannel) {
							selectedNote = null;
						}
						channels.get(clickChannel).notes.remove(newEdNote);
					}
				} else {
					if(e.getButton() == MouseEvent.BUTTON1) {
						// If there isn't, play the note, add it to channel
						newNote.playSound(clickChannel != 1);
						channels.get(clickChannel).notes.add(newEdNote);
						if(newNote.equals(selectedNote) && clickChannel == selectedNoteChannel) {
							selectedNote = null;
						}
					}
				}

				// Update the simulation area
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// unused
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				// unused
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocusInWindow();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// unused
			}
		});

		// Create mouse wheel listener for scrolling
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getPreciseWheelRotation() < 0) {
					moveLeft();
				} else {
					moveRight();
				}
			}
		});
		
		// Send key events to the simulation panel
		addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				GBATrackerSimulationPanel.this.keyPressed(e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// unused
			}
			@Override
			public void keyTyped(KeyEvent e) {
				// unused
			}
		});
	}

	/**
	 * Update a note when a key is pressed
	 * @param keyCode Event passed from the JFrame
	 */
	public void keyPressed(int keyCode) {
		if(!simulating && selectedNote != null && selectedNoteChannel < 2) {
			boolean modified = true;
			switch(keyCode) {
			case KeyEvent.VK_C:
				if(selectedNote.note.musicalNote == 0) {
					selectedNote.note.musicalNote = 1;
				} else {
					selectedNote.note.musicalNote = 0;
				}
				break;
			case KeyEvent.VK_D:
				if(selectedNote.note.musicalNote == 2) {
					selectedNote.note.musicalNote = 3;
				} else {
					selectedNote.note.musicalNote = 2;
				}
				break;
			case KeyEvent.VK_E:
				selectedNote.note.musicalNote = 4;
				break;
			case KeyEvent.VK_F:
				if(selectedNote.note.musicalNote == 5) {
					selectedNote.note.musicalNote = 6;
				} else {
					selectedNote.note.musicalNote = 5;
				}
				break;
			case KeyEvent.VK_G:
				if(selectedNote.note.musicalNote == 7) {
					selectedNote.note.musicalNote = 8;
				} else {
					selectedNote.note.musicalNote = 7;
				}
				break;
			case KeyEvent.VK_A:
				if(selectedNote.note.musicalNote == 9) {
					selectedNote.note.musicalNote = 10;
				} else {
					selectedNote.note.musicalNote = 9;
				}
				break;
			case KeyEvent.VK_B:
				selectedNote.note.musicalNote = 11;
				break;
			case KeyEvent.VK_UP:
				if(selectedNote.note.octave < 7) {
					++selectedNote.note.octave;
				} else {
					return;
				}
				break;
			case KeyEvent.VK_DOWN:
				if(selectedNote.note.octave > 2) {
					--selectedNote.note.octave;
				} else {
					return;
				}
				break;
			default:
				modified = false;
			}
			if(modified) {
				selectedNote.note.playSound(selectedNoteChannel != 1);
				controller.updateUIFromNote(selectedNote.note);
				repaint();
			}
		}
	}

	/**
	 * Update the selected note from UI changes in real time
	 * @param note
	 */
	public void updateSelectedNote(Note newNote) {
		if(selectedNote != null) {
			EditorChannel ch = channels.get(selectedNoteChannel);
			for(EditorNote n : ch.notes) {
				if(n.equals(selectedNote)) {
					if(n.note.isSquareType == newNote.isSquareType) {
						n.note = newNote;
						newNote.playSound(true);
					}
				}
			}
			repaint();
		}
	}


	/**
	 * Get the length of the song, in steps
	 * @return The number of 48ths in this song
	 */
	public int getDataLength() {
		int lineCount = 0;
		for(int i = 0; i < endStep; ++i) {
			EditorNote edn = new EditorNote(null, i);
			if(channels.get(0).notes.contains(edn) || channels.get(1).notes.contains(edn) || channels.get(2).notes.contains(edn)) {
				++lineCount;
			}
		}
		return lineCount;
	}

	/**
	 * Get the start of the song's loop
	 * @return The step on which the song loops
	 */
	public int getLoop() {
		return loopStep;
	}

	/**
	 * Play the file from the start
	 */
	public void play() {
		if(!simulating) {

			// Render the notes
			controller.setTooltipText("Rendering notes...");
			for(int i = 0; i < 3; ++i) {
				for(EditorNote edn : channels.get(i).notes) {
					edn.note.prepareBuf(i != 1);
				}
			}
			controller.setTooltipText(" ");
			selectedNote = null;

			// Start playing
			playingStep = 0;
			startScroll = scroll;
			if(scroll > 0) {
				scroll = -0.1;
			}
			simulating = true;
			simulationTimer = new Timer(1000 / FRAMERATE, new SimulationListener(this));
			simulationTimer.start();
		}
	}

	/**
	 * Play the file from an offset
	 */
	public void playHere() {
		if(!simulating) {

			// Render the notes
			controller.setTooltipText("Rendering notes...");
			for(int i = 0; i < 3; ++i) {
				for(EditorNote edn : channels.get(i).notes) {
					edn.note.prepareBuf(i != 1);
				}
			}
			controller.setTooltipText(" ");
			selectedNote = null;

			// Start playing
			playingStep = (int) Math.ceil(scroll) * 48;
			startScroll = scroll;
			simulating = true;
			simulationTimer = new Timer(1000 / FRAMERATE, new SimulationListener(this));
			simulationTimer.start();
		}
	}

	/**
	 * Stop playing the file
	 */
	public void stop() {
		if(simulating) {
			scroll = startScroll;
			simulating = false;
			simulationTimer.stop();
			repaint();
		}
	}

	/**
	 * Zoom in
	 * @return The amount the simulation is currently zoomed
	 */
	public double zoomIn() {
		zoom /= ZOOM_DELTA;
		if(zoom > MAX_ZOOM) {
			zoom = MAX_ZOOM;
		}
		repaint();
		return zoom;
	}

	/**
	 * Zoom out
	 * @return The amount the simulation is currently zoomed
	 */
	public double zoomOut() {
		zoom *= ZOOM_DELTA;
		if(zoom < MIN_ZOOM) {
			zoom = MIN_ZOOM;
		}
		repaint();
		return zoom;
	}

	/**
	 * Scroll to the left
	 */
	public double moveLeft() {
		scroll -= SCROLL_DELTA / zoom;
		if(scroll < MIN_SCROLL) {
			scroll = MIN_SCROLL;
		}
		repaint();
		return scroll;
	}

	/**
	 * Scroll to the right
	 */
	public double moveRight() {
		scroll += SCROLL_DELTA / zoom;
		repaint();
		return scroll;
	}

	/**
	 * Set the quantization level for the editor
	 * @param quantization The new quantization
	 */
	public void setQuantization(int quantization) {
		this.quantization = quantization;
		repaint();
	}

	/**
	 * Enable or disable looping
	 * @param enable If true, loop the song
	 */
	public void setLoopingEnabled(boolean enable) {
		looping = enable;
		repaint();
	}

	/**
	 * Draw the simulation screen
	 */
	@Override
	public void paintComponent(Graphics _g) {

		// Set up brush
		Graphics2D g = (Graphics2D) _g;
		g.setColor(Color.WHITE);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));

		// Background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Play marker
		double measureWidth = getWidth() * zoom;
		double X = (Math.ceil(scroll) * measureWidth) - scroll * measureWidth;
		if(simulating) {
			X = (playSlider - scroll) * measureWidth;
		}
		double Y = 20;
		g.setColor(Color.DARK_GRAY);
		g.drawLine((int) Math.round(X), (int) Math.round(Y), (int) Math.round(X), getHeight());
		g.setColor(Color.GREEN);
		int[] polyPointsX = new int[3];
		int[] polyPointsY = new int[3];
		for(int i = 0; i < PlaySliderPolygonX.length; ++i) {
			polyPointsX[i] = PlaySliderPolygonX[i] + (int) Math.round(X);
			polyPointsY[i] = PlaySliderPolygonY[i] + (int) Math.round(Y);
		}
		g.fillPolygon(polyPointsX, polyPointsY, PlaySliderPolygonX.length);

		// Quantization bars
		g.setStroke(new BasicStroke(3));
		int cellHeight = getHeight() >> 2;
		int n = (int) Math.ceil(scroll);
		double barX = (n - scroll) * measureWidth;
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.LIGHT_GRAY);
		for(double qBarX = barX - measureWidth; qBarX < getWidth(); qBarX += measureWidth / quantization) {
			g.drawLine((int) Math.round(qBarX), 3 * cellHeight / 2, (int) Math.round(qBarX), getHeight());
		}
		g.setColor(Color.WHITE);

		// Horizontal bars
		for(int i = 0; i < 3; ++i) {
			int h = 3 * cellHeight / 2 + i * cellHeight;
			g.drawLine(0, h, getWidth(), h);
		}

		// Measure bars
		for(g.setStroke(new BasicStroke(3)); barX < getWidth(); barX += measureWidth) {
			g.drawLine((int) barX, cellHeight, (int) barX, getHeight());
			String label = String.format("%d", n++);
			Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(label, g);
			g.drawString(label, (int) Math.round((barX - stringBounds.getWidth() / 2)), (int) Math.round((cellHeight - stringBounds.getHeight() + 5)));
		}

		// Draw the notes
		g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
		for(int channelNum = 0; channelNum < 3; ++channelNum) {
			for(EditorNote edNote : channels.get(channelNum).notes) {
				X = (edNote.step * measureWidth / 48) - scroll * measureWidth;
				Y = cellHeight * channelNum + 3 * cellHeight / 2;
				g.setColor(Color.CYAN);
				g.fillRect((int) Math.round(X) - NOTE_SIZE / 2, (int) Math.round(Y) - NOTE_SIZE / 2, NOTE_SIZE, NOTE_SIZE);
				g.setColor(Color.BLACK);
				String label;
				if(channelNum < 2) {
					label = String.format("%s%d", GBATrackerSquareChannelPanel.Notes[edNote.note.musicalNote], edNote.note.octave);
				} else {
					label = String.format("%s|%s", (int) edNote.note.dividingRatio, edNote.note.shiftClockFrequency);
				}
				Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(label, g);
				g.drawString(label, (int) Math.round(X - stringBounds.getWidth() / 2), (int) Y);
				if(edNote.equals(selectedNote) && channelNum == selectedNoteChannel) {
					g.setStroke(new BasicStroke(4));
					g.setColor(Color.RED);
					g.drawRect((int) Math.round(X) - NOTE_SIZE / 2, (int) Math.round(Y) - NOTE_SIZE / 2, NOTE_SIZE, NOTE_SIZE);
					g.setStroke(new BasicStroke(1));
					g.setColor(Color.CYAN);
				}
			}
		}

		// End marker
		X = (endStep * measureWidth / 48) - scroll * measureWidth;
		g.setColor(Color.WHITE);
		String label = String.format("End", n++);
		Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(label, g);
		g.drawString(label, (int) (Math.round(X) - stringBounds.getWidth() / 2), (int) stringBounds.getHeight());

		// Loop marker
		if(looping) {
			X = (loopStep * measureWidth / 48) - scroll * measureWidth;
			label = String.format("Loop", n++);
			stringBounds = g.getFontMetrics().getStringBounds(label, g);
			g.drawString(label, (int) (Math.round(X) - stringBounds.getWidth() / 2), (int) stringBounds.getHeight());
		}
	}

	/**
	 * Generate a String representation of the notes
	 * @return The String representation of the notes
	 */
	public String generateCSV() {
		String str = endStep + "," + loopStep;
		for(int i = 0; i < 3; ++i) {
			str += "," + channels.get(i).notes.size();
			for(EditorNote edn : channels.get(i).notes) {
				if(i < 2) {
					str += "," + edn.note.getSWP();
				}
				str += "," + edn.note.getENV();
				str += "," + edn.note.getFRQ();
				str += "," + edn.step;
			}
		}
		return str;
	}

	/**
	 * Populate the notes from a String representation
	 * @param csv The csv-format String representation
	 */
	public void populateFromString(String csv) {

		// Scanner used to parse the csv
		Scanner sc = new Scanner(csv);
		sc.useDelimiter(",");

		// New channels
		List<EditorChannel> newChannels = new ArrayList<>();
		newChannels.add(new EditorChannel());
		newChannels.add(new EditorChannel());
		newChannels.add(new EditorChannel());

		// Fail gracefully on parse error
		try {
			int newEndStep = Integer.parseInt(sc.next());
			int newLoopStep = Integer.parseInt(sc.next());

			// For each channel...
			for(int i = 0; i < 3; ++i) {
				int len = Integer.parseInt(sc.next());
				while(len-- > 0) {
					Note note;

					// Differentiate between square and noise channels
					if(i < 2) {
						int SWP = Integer.parseInt(sc.next());
						int ENV = Integer.parseInt(sc.next());
						int FRQ = Integer.parseInt(sc.next());
						note = new Note(SWP, ENV, FRQ, i != 1);
					} else {
						int ENV = Integer.parseInt(sc.next());
						int FRQ = Integer.parseInt(sc.next());
						note = new Note(ENV, FRQ);
					}
					int step = Integer.parseInt(sc.next());
					newChannels.get(i).notes.add(new EditorNote(note, step));
				}
			}

			// If all was successful, now set the values
			selectedNote = null;
			endStep = newEndStep;
			loopStep = newLoopStep;
			channels = newChannels;
			repaint();
		} catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Corrupted file", "Unable to parse file", JOptionPane.ERROR_MESSAGE);
		} finally {
			sc.close();
		}
	}

	/**
	 * Generate formatted note data for exporting
	 * @return The C array for the note data
	 */
	public String getNoteData() {

		// Get the uncompressed data
		int dataVals[] = new int[endStep << 3];
		for(int i = 0; i < endStep; ++i) {
			EditorNote edn = new EditorNote(null, i);
			Note n = null;
			for(EditorNote edn_itr : channels.get(0).notes) {
				if(edn.equals(edn_itr)) {
					n = edn_itr.note;
					break;
				}
			}
			if(n != null) {
				dataVals[i << 3] = n.getSWP();
				dataVals[(i << 3) + 1] = n.getENV();
				dataVals[(i << 3) + 2] = n.getFRQ();
			} else {
				dataVals[i << 3] = dataVals[(i << 3) + 1] = dataVals[(i << 3) + 2] = 0;
			}
			n = null;
			for(EditorNote edn_itr : channels.get(1).notes) {
				if(edn.equals(edn_itr)) {
					n = edn_itr.note;
					break;
				}
			}
			if(n != null) {
				dataVals[(i << 3) + 3] = n.getENV();
				dataVals[(i << 3) + 4] = n.getFRQ();
			} else {
				dataVals[(i << 3) + 3] = dataVals[(i << 3) + 4] = 0;
			}
			n = null;
			for(EditorNote edn_itr : channels.get(2).notes) {
				if(edn.equals(edn_itr)) {
					n = edn_itr.note;
					break;
				}
			}
			if(n != null) {
				dataVals[(i << 3) + 5] = n.getENV();
				dataVals[(i << 3) + 6] = n.getFRQ();
			} else {
				dataVals[(i << 3) + 5] = dataVals[(i << 3) + 6] = 0;
			}
			dataVals[(i << 3) + 7] = 1;
		}

		// Compress lines by calculating RLE lengths
		for(int i = 0, currIndex = 0; i < endStep; ++i) {
			if(dataVals[(i << 3) + 2] != 0 || dataVals[(i << 3) + 4] != 0 || dataVals[(i << 3) + 6] != 0) {
				currIndex = i;
			} else {
				++dataVals[(currIndex << 3) + 7];
			}
		}

		// Create data string
		int dataLen = getDataLength();
		int currLine = 0;
		String dataStr = "";
		for(int i = 0; i < endStep; ++i) {
			if(dataVals[(i << 3) + 2] != 0 || dataVals[(i << 3) + 4] != 0 || dataVals[(i << 3) + 6] != 0) {
				String dataLine = "";
				for(int j = 0; j < 8; ++j) {
					dataLine += String.format("0x%04X", dataVals[(i << 3) + j]);
					if(j < 7) {
						dataLine += ",";
					}
				}
				dataStr += "\t{" + dataLine + "}";
				if(currLine++ < dataLen - 1) {
					dataStr += ",\n";
				}
			}
		}
		return dataStr;
	}

	/**
	 * This class adds a step to a regular Note object for representation in
	 * the simulator
	 * @author Andrew Wilder
	 */
	private static class EditorNote {

		/** The variables held by the EditorNote object */
		public Note note;
		public int step;

		/**
		 * Create a new EditorNote object
		 * @param note The Note to contain
		 * @param step The 48th measure this is placed on
		 */
		public EditorNote(Note note, int step) {
			this.note = note;
			this.step = step;
		}

		/**
		 * Determine if this is equal to another one. Used for Lists.
		 */
		public boolean equals(Object o) {
			return o instanceof EditorNote && step == ((EditorNote) o).step;
		}
	}

	/**
	 * This class pairs a sound channel and a list of EditorNote objects
	 * @author Andrew Wilder
	 */
	private static class EditorChannel {

		/** The variables held by the EditorChannel object */
		public List<EditorNote> notes = new ArrayList<>();
		public Channel channel = new Channel();
	}
}
