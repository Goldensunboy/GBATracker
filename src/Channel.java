import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * This class represents a single channel for playing sounds
 * Only one Note can play at a time per Channel
 * @author Andrew Wilder
 */
public class Channel {
	
	/** The Clips used for playing sounds */
	public Clip lastClip;
	public Clip currClip;
	
	/**
	 * Construct a new Channel object
	 */
	public Channel() {
		try {
			currClip = AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
