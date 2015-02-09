package ch.fhnw.ether.examples.visualizer;

import java.util.Arrays;

import ch.fhnw.ether.audio.AudioFrame;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.util.ClassUtilities;

public class DCRemove extends AbstractRenderCommand<IAudioRenderTarget, DCRemove.State> {
	final static float POLE = 0.9999f;

	static class State extends PerTargetState<IAudioRenderTarget> {
		private float[] lastIn  = ClassUtilities.EMPTY_floatA;
		private float[] lastOut = ClassUtilities.EMPTY_floatA;

		public State(IAudioRenderTarget target) {
			super(target);
		}

		public void process(AudioFrame frame) {
			if(lastIn.length < frame.nChannels) {
				lastIn  = Arrays.copyOf(lastIn,  frame.nChannels);
				lastOut = Arrays.copyOf(lastOut, frame.nChannels);
			}
			final float[] samples = frame.samples;
			for(int i = 0; i < samples.length; i ++) {
				final int   c      = i % frame.nChannels;
				final float sample = samples[i];
				final float diff   = sample - lastIn[c];
				final float intg   = POLE * lastOut[c] + diff;
				lastIn[c]          = sample;
				lastOut[c]         = intg;
				samples[i]         = lastOut[c];
			}
		}
	}
	
	
	@Override
	protected void run(State state) throws RenderCommandException {
		state.process(state.getTarget().getFrame());
	}
	
	@Override
	protected State createState(IAudioRenderTarget target) throws RenderCommandException {
		return new State(target);
	}
}
