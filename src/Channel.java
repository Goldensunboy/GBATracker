import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * This class represents a single channel for playing sounds
 * Only one Note can play at a time per Channel
 * @author Andrew Wilder
 */
public class Channel {
	
	/** Definitions */
	private static final AudioFormat PlayerFormat = new AudioFormat(48000, 8, 1, true, true);
	
	/** The SourceDataLine used for playing sounds */
	public SourceDataLine line;
	
	/** Whether or not sweeping is allowed on this channel */
	public boolean hasSweep;
	
	/**
	 * Construct a new Channel object
	 */
	public Channel() {
		try {
			line = AudioSystem.getSourceDataLine(PlayerFormat);
			line.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
