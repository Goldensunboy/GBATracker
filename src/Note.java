import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

/**
 * This class represents the properties of a note
 * @author Andrew Wilder
 */
public class Note {
	
	/** Constants */
	private static final double PLAYER_VOLUME = 0.3;
	private static final int[] NoteFrequencies = {
		8013, 7566, 7144, 6742, 6362, 6005, 5666, 5346, 5048, 4766, 4499, 4246
	};
	
	/** Used for noise generation */
	private static Random rand = new Random(0); // deterministic
	private static boolean rands[] = new boolean[0x7FFF];
	static {
		for(int i = 0; i < rands.length; ++i) {
			rands[i] = rand.nextBoolean();
		}
	}
	
	/** Global parameters */
	public boolean isSquareType;
	public int volume;
	public int envelopeStep;
	public boolean increasingEnvelope;
	public boolean hasCutoff;
	public int cutoffValue;
	
	/** Square channel properties */
	public int musicalNote; // C = 0
	public int octave;
	public double dutyCycle;
	public int sweepRate;
	public int sweepStep;
	public boolean increasingSweep;
	
	/** Noise channel properties */
	public double dividingRatio;
	public int shiftClockFrequency;
	public boolean counterStepIs15Bits;
	
	/**
	 * Test the note sound using the test channel
	 */
	static Channel testChannel = new Channel(true);
	void testSound() {
		playSound(testChannel);
	}
	
	/**
	 * Test the note sound
	 */
	void playSound(Channel channel) {
		
		// Buffer where generated samples will be stored
		byte[] buf = new byte[5 * 48000];
		
		// Differentiate between square or noise notes
		if(isSquareType) {
			boolean pitchOutOfRange = false;
			double phaseAdjust = 0;
			int currVolume = volume;
			
			// Get the wavelength of the wave form
			double freq = 440 * Math.pow(2, (musicalNote - 9 + (octave - 4) * 12) / 12.0);
			double wavelength = 48000 / freq;
			
			// Fill in the samples
			for(int i = 0; i < buf.length; ++i) {
				
				// Adjust frequency if sweeping
				if(channel.hasSweep && i > 0 && sweepStep > 0 && i % (sweepStep * 375) == 0) {
					double n = 2048 - 131072 / freq;
					double delta = n / Math.pow(2, sweepRate);
					n = increasingSweep ? n + delta : n - delta;
					if(n < 0 || n > 2047) {
						pitchOutOfRange = true;
					}
					freq = 131072 / (2048 - n);
					wavelength = 48000 / freq;
					phaseAdjust = i % wavelength;
				}
				
				// Adjust envelope volume
				if(i > 0 && envelopeStep > 0 && i % (envelopeStep * 750) == 0) {
					if(increasingEnvelope) {
						if(currVolume < 15) {
							++currVolume;
						}
					} else {
						if(currVolume > 0) {
							--currVolume;
						}
					}
				}
				
				// Determine sample amplitude by volume
				byte amplitude = (byte) (127 * PLAYER_VOLUME * currVolume / 15);
				if(pitchOutOfRange || hasCutoff && i / 48000.0 > (64 - cutoffValue) / 256.0) {
					amplitude = 0;
				}
				
				// Determine high or low by duty cycle
				double phase = (i - phaseAdjust) % wavelength;
				if(phase / wavelength < dutyCycle) {
					buf[i] = amplitude;
				} else {
					buf[i] = (byte) -amplitude;
				}
			}
		} else {
			int X = 0;
			int currVolume = volume;
			boolean high = true;
			
			// Get the wavelength for one period of noise step
			double freq = 524288 / dividingRatio / Math.pow(2, shiftClockFrequency + 1);
			double wavelength = 48000 / freq;
			
			// Fill in the samples
			for(int i = 0; i < buf.length; ++i) {
				
				// Adjust envelope volume
				if(i > 0 && envelopeStep > 0 && i % (envelopeStep * 750) == 0) {
					if(increasingEnvelope) {
						if(currVolume < 15) {
							++currVolume;
						}
					} else {
						if(currVolume > 0) {
							--currVolume;
						}
					}
				}
				
				// At each step, determine if the waveform should switch
				double phase = i % wavelength;
				if(i > 0 && phase < 1.0) {
					high = rands[X];
					if(++X == (counterStepIs15Bits ? 0x7FFF : 0x7F)) {
						X = 0;
					}
				}
				
				// Determine sample amplitude by volume
				byte amplitude = (byte) (127 * PLAYER_VOLUME * currVolume / 15);
				if(hasCutoff && i / 48000.0 > (64 - cutoffValue) / 256.0) {
					amplitude = 0;
				}
				if(high) {
					buf[i] = amplitude;
				} else {
					buf[i] = (byte) -amplitude;
				}
			}
		}
		
		// Play the buffer
		channel.currClip.stop();
		channel.lastClip = channel.currClip;
		try {
			channel.currClip = AudioSystem.getClip();
			channel.currClip.open(new AudioFormat(48000, 8, 1, true, true), buf, 0, buf.length);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		channel.currClip.start();
		
		// Clean up any previously playing sound
		new CancelSound(channel).start();
	}
	
	/**
	 * Return the sweep value for this Note
	 * @return The sweep value in GBA format
	 */
	public int getSWP() {
		return sweepRate | ((increasingSweep ? 0 : 1) << 3) | (sweepStep << 4);
	}
	
	/**
	 * Return the envelope value for this Note
	 * @return The envelope value in GBA format
	 */
	public int getENV() {
		return cutoffValue | ((int) (dutyCycle * 4) << 6) | (envelopeStep << 8) | ((increasingEnvelope ? 1 : 0) << 11) | (volume << 12);
	}
	
	/**
	 * Get the frequency value for this Note
	 * @return The frequency value in GBA format
	 */
	public int getFRQ() {
		int FRQ = 0x8000 | ((hasCutoff ? 1 : 0) << 14);
		if(isSquareType) {
			return FRQ | (2048 - (NoteFrequencies[musicalNote] >> octave));
		} else {
			return FRQ | ((int) dividingRatio) | ((counterStepIs15Bits ? 1 : 0) << 3) | (shiftClockFrequency << 4);
		}
	}
	
	/**
	 * Print the register values corresponding to this note
	 * @return The sound values for GBA
	 */
	public String toString() {
		int ENV = getENV();
		int FRQ = getFRQ();
		if(isSquareType) {
			int SWP = getSWP();
			return String.format("SWP: 0x%04X  ENV: 0x%04X  FRQ: 0x%04X", SWP, ENV, FRQ);
		} else {
			return String.format("ENV: 0x%04X  FRQ: 0x%04X", ENV, FRQ);
		}
	}
	
	/**
	 * This inner class is used to cancel sounds asynchronously because of
	 * problems with Clip not canceling properly, causing UI to hang sometimes
	 * @author Andrew Wilder
	 */
	private class CancelSound extends Thread {
		
		/** The channel representing the sound to cancel */
		Channel channel;
		
		/**
		 * Construct a new CancelSound thread object
		 * @param channel
		 */
		public CancelSound(Channel channel) {
			this.channel = channel;
		}
		
		/**
		 * Cancel the sound playing on the channel
		 */
		@Override
		public void run() {
			channel.lastClip.flush();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			channel.lastClip.close();
		}
	}
}
