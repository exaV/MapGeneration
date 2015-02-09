package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.AbstractFrameSource;
import ch.fhnw.ether.media.PerTargetState;

public abstract class AbstractAudioSource<S extends PerTargetState<IAudioRenderTarget>> extends AbstractFrameSource<IAudioRenderTarget, S> {

	public abstract float getSampleRate();

	public abstract int   getNumChannels();

	protected AudioFrame createAudioFrame(long sTime, int frameSize) {
		return createAudioFrame(sTime, new float[frameSize]);
	}

	protected AudioFrame createAudioFrame(long sTime, float[] data) {
		return new AudioFrame(sTime, getNumChannels(), getSampleRate(), data);
	}	
}
