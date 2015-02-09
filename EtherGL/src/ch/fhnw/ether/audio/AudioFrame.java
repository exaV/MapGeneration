package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.AbstractFrame;

public class AudioFrame extends AbstractFrame {
	public final int     nChannels;
	public final float[] samples;
	private      float[] monoSamples;
	public final float   sRate;
	public final long    sTime;

	public AudioFrame(long sTime, int nChannels, float sRate, float[] samples) {
		super((sTime / nChannels) / (double)sRate);
		this.nChannels = nChannels;
		this.sRate     = sRate;
		this.sTime     = sTime;
		this.samples   = samples;
	}

	public float[] getMonoSamples() {
		if(monoSamples == null) {
			monoSamples = new float[samples.length / nChannels];
			for(int i = 0; i < samples.length; i++)
				monoSamples[i / nChannels] += samples[i];
			final float cs = nChannels;
			for(int i = 0; i< monoSamples.length; i++)
				monoSamples[i] /= cs;
		}
		return monoSamples;
	}
}
