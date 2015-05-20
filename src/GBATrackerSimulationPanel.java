import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;

/**
 * The simulation panel for showing the timeline of notes
 * @author Andrew Wilder
 */
public class GBATrackerSimulationPanel extends JPanel {
	
	/** Definitions */
	private static final double MIN_SCROLL = -0.25;
	private static final double MAX_ZOOM = 3.0;
	private static final double MIN_ZOOM = 0.2;
	private static final double ZOOM_DELTA = 0.8;
	private static final double SCROLL_DELTA = 0.35;
	private static final int NOTE_SIZE = 35;
	
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
	
	/**
	 * Initialize the simulation panel and the simulation variables
	 * @param controller Reference to the main controller
	 */
	public GBATrackerSimulationPanel(final GBATrackerFrame controller) {
		
		// No actual components, just a rigid area to maintain window size
		//this.controller = controller;
		add(Box.createRigidArea(new Dimension(800, 300)));
		
		// Instantiate the channel data
		channels.add(new EditorChannel(true));  // Channel 1 (square w/ sweep)
		channels.add(new EditorChannel(false)); // Channel 2 (square)
		channels.add(new EditorChannel(true));  // Channel 4 (noise)
		
		// Create the MouseListener that handling clicking
		addMouseListener(new MouseListener() {
			
			/**
			 * Handle mouse clicks
			 */
			@Override
			public void mousePressed(MouseEvent e) {

				// Do nothing while simulating
				if(simulating) {
					return;
				}
				
				// Which channel was clicked?
				clickChannel = e.getY() / (getHeight() >> 2) - 1;
				if(clickChannel < 0) {
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
				
				// Is there already a note here?
				Note newNote = controller.getNoteFromUI(clickChannel < 2);
				EditorNote newEdNote = new EditorNote(newNote, clickStep);
				if(channels.get(clickChannel).notes.contains(newEdNote)) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						// If there is, play the existing note and update the UI
						for(EditorNote n : channels.get(clickChannel).notes) {
							if(n.step == clickStep) {
								n.note.playSound(channels.get(clickChannel).channel);
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
						newNote.playSound(channels.get(clickChannel).channel);
						channels.get(clickChannel).notes.add(newEdNote);
						if(newNote.equals(selectedNote) && clickChannel == selectedNoteChannel) {
							selectedNote = null;
						} else {
							selectedNote = newEdNote;
							selectedNoteChannel = clickChannel;
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
				// unused
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// unused
			}
		});
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
					n.note = newNote;
				}
			}
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
	
	public void setQuantization(int quantization) {
		this.quantization = quantization;
		repaint();
	}
	
	/**
	 * Draw the simulation screen
	 */
	@Override
	public void paintComponent(Graphics _g) {
		
		// Set up brush
		Graphics2D g = (Graphics2D) _g;
		g.setStroke(new BasicStroke(3));
		g.setColor(Color.WHITE);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		
		// Background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// Quantization bars
		int cellHeight = getHeight() >> 2;
		double measureWidth = getWidth() * zoom;
		int n = (int) Math.ceil(scroll);
		double barX = (n - scroll) * measureWidth;
		Stroke tempStroke = g.getStroke();
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.LIGHT_GRAY);
		for(double qBarX = barX - measureWidth; qBarX < getWidth(); qBarX += measureWidth / quantization) {
			g.drawLine((int) Math.round(qBarX), 3 * cellHeight / 2, (int) Math.round(qBarX), getHeight());
		}
		g.setStroke(tempStroke);
		g.setColor(Color.WHITE);
		
		// Horizontal bars
		for(int i = 0; i < 3; ++i) {
			int h = 3 * cellHeight / 2 + i * cellHeight;
			g.drawLine(0, h, getWidth(), h);
		}
		
		// Measure bars
		for(; barX < getWidth(); barX += measureWidth) {
			g.drawLine((int) barX, cellHeight, (int) barX, getHeight());
			String label = String.format("%d", n++);
			Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(label, g);
			g.drawString(label, (int) Math.round((barX - stringBounds.getWidth() / 2)), (int) Math.round((cellHeight - stringBounds.getHeight())));
		}
		
		// Draw the notes
		g.setFont(new Font("TimesRoman", Font.PLAIN, 10));
		for(int channelNum = 0; channelNum < 3; ++channelNum) {
			for(EditorNote edNote : channels.get(channelNum).notes) {
				double X = (edNote.step * measureWidth / 48) - scroll * measureWidth;
				double Y = cellHeight * channelNum + 3 * cellHeight / 2;
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
		
//		g.setColor(Color.CYAN);
//		double Y = cellHeight * clickChannel + 3 * cellHeight / 2;
//		double X = (clickStep * measureWidth / quantization) - scroll * measureWidth;
//		g.drawOval((int) X - 15, (int) Y - 15, 30, 30);
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
		public Channel channel;
		
		/**
		 * Create a new EditorChannel object
		 * @param channelHasSweep Used for preventing sweep on channel 2
		 */
		public EditorChannel(boolean channelHasSweep) {
			channel = new Channel(channelHasSweep);
		}
	}
}
