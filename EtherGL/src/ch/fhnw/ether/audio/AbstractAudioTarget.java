package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.AbstractFrameSource;
import ch.fhnw.ether.media.AbstractMediaTarget;

public abstract class AbstractAudioTarget extends AbstractMediaTarget<AudioFrame, IAudioRenderTarget> implements IAudioRenderTarget {
	protected AbstractAudioTarget(int threadPriority, boolean realTime) {
		super(threadPriority, realTime);
	}
	
	@Override
	public AbstractFrameSource<IAudioRenderTarget> getFrameSource() {
		return super.getFrameSource();
	}
}
