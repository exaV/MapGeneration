package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;

public class SilenceAudioSource extends AbstractAudioSource<SilenceAudioSource.State> {
	private final float sampleRate;
	private final int   nChannels;
	private final int   frameSize;
	
	static class State extends PerTargetState<IAudioRenderTarget> {
		long samples;
		
		public State(IAudioRenderTarget target) {
			super(target);
		}
	}
	
	public SilenceAudioSource(int nChannels, float sampleRate, int frameSize) {
		this.nChannels  = nChannels;
		this.sampleRate = sampleRate;
		this.frameSize  = Math.max(1, (frameSize / nChannels) * nChannels);
	}

	@Override
	protected void run(State state) throws RenderCommandException {
		state.getTarget().setFrame(createAudioFrame(state.samples, frameSize));
		state.samples += frameSize;
	}	

	@Override
	public long getFrameCount() {
		return FRAMECOUNT_UNKNOWN;
	}

	@Override
	public float getSampleRate() {
		return sampleRate;
	}

	@Override
	public int getNumChannels() {
		return nChannels;
	}
	
	@Override
	protected State createState(IAudioRenderTarget target) throws RenderCommandException {
		return new State(target);
	}
}
