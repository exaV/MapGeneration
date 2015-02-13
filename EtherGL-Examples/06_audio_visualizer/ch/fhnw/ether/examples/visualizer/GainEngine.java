package ch.fhnw.ether.examples.visualizer;

import ch.fhnw.ether.audio.AudioFrame;
import ch.fhnw.ether.audio.AudioUtilities;

/**
 * Get the level of a signal using averaging and attack/sustain/decay speeds
 */
public class GainEngine {
	
	private final static double MIN_LEVEL = AudioUtilities.dbToLevel(AudioUtilities.MIN_GAIN);
	
	private final AverageBuffer history; // Contains squared sample values
	private int sustainSpeed; // Number of samples
	private double attackSpeed; // Ratio to multiply by on each sample
	private double decaySpeed;
	private double smoothedGain;
	private int sustainCountDown;
	private double jumpLevel;
	
	
	public GainEngine(int historySize, int sustainSpeed, double attackSpeed, double decaySpeed, double jumpLevel) {
		this.history = new AverageBuffer(historySize);
		setSustainSpeed(sustainSpeed);
		setAttackSpeed(attackSpeed);
		setDecaySpeed(decaySpeed);
		setJumpLevel(jumpLevel);
		this.smoothedGain = MIN_LEVEL;
	}

	public int getSustainSpeed() {
		return sustainSpeed;
	}

	public void setSustainSpeed(int sustainSpeed) {
		this.sustainSpeed = sustainSpeed;
	}

	public double getAttackSpeed() {
		return attackSpeed;
	}

	public double getJumpLevel() {
		return jumpLevel;
	}

	public void setJumpLevel(double jumpLevel) {
		this.jumpLevel = jumpLevel;
	}

	public void setAttackSpeed(double attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public double getDecaySpeed() {
		return decaySpeed;
	}

	public void setDecaySpeed(double decaySpeed) {
		this.decaySpeed = decaySpeed;
	}
	
	public void process(AudioFrame frame) {
		final float[] samples = frame.getMonoSamples();
		
		for (int i = 0; i < samples.length; i++) {
			history.push(samples[i] * samples[i]);
			double immediateGain = Math.sqrt(history.getAverage());
			if (immediateGain >= smoothedGain) {
				// Attack
				if (smoothedGain < jumpLevel)
					smoothedGain = immediateGain;
				else
					smoothedGain *= attackSpeed;
				if (smoothedGain > immediateGain)
					smoothedGain = immediateGain;
				sustainCountDown = sustainSpeed;
			} else {
				if (sustainCountDown > 0) {
					// Sustain
					sustainCountDown--;
				} else {
					// Decay
					smoothedGain *= decaySpeed;
					if (smoothedGain < MIN_LEVEL)
						smoothedGain = MIN_LEVEL;
					if (smoothedGain < immediateGain)
						smoothedGain = immediateGain;
				}
			}
		}
	}
	
	public double getGain() {
		return this.smoothedGain;
	}

}
