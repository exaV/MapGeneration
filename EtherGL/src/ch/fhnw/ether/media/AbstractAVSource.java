package ch.fhnw.ether.media;

import ch.fhnw.ether.audio.AudioFrame;

public abstract class AbstractAVSource extends AbstractFrameSource<IRenderTarget<?>> {
	protected AbstractAVSource() {
	}

	public abstract float getSampleRate();

	public abstract int   getNumChannels();
	
	protected AudioFrame createAudioFrame(long sTime, int frameSize) {
		return createAudioFrame(sTime, new float[frameSize]);
	}

	protected AudioFrame createAudioFrame(long sTime, float[] data) {
		return new AudioFrame(sTime, getNumChannels(), getSampleRate(), data);
	}	

	public abstract int getWidth();
	public abstract int getHeight();	
}
