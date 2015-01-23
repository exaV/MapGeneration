package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.AbstractFrame;

public class AudioFrame extends AbstractFrame {
	public final int     channels;
	public final float[] samples;
	
	public AudioFrame(double playOutTime, int channels, float[] samples) {
		super(playOutTime);
		this.channels = channels;
		this.samples  = samples;
	}
}
