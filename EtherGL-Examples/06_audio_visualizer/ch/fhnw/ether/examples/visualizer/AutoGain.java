package ch.fhnw.ether.examples.visualizer;

import ch.fhnw.ether.audio.AudioFrame;
import ch.fhnw.ether.audio.AudioUtilities;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;

public class AutoGain extends AbstractRenderCommand<IAudioRenderTarget,AutoGain.State> {
	private final static double MAX2AVG = 0.5;

	static class State extends PerTargetState<IAudioRenderTarget> {
		private static final double SMOOTH_DELAY = 0.05;
		private static final double MIN_LEVEL    = AudioUtilities.dbToLevel(-40.0);
		private static final double SUSTAIN_TIME = 2.0;
		private static final double ACCURACY     = AudioUtilities.dbToLevel(1.0); // Width of 'void' range, where no correction occurs
		private static final double TARGET_UPPER = AudioUtilities.dbToLevel(-3.0);
		private static final double TARGET_LOWER = TARGET_UPPER / ACCURACY;

		private final double sampleRate;
		private final double attackFactor;
		private final double decayFactor;

		private final int        historySize;
		private final int        sustainSpeed;
		private final GainEngine gainEngine;

		public State(IAudioRenderTarget target) {
			super(target);
			sampleRate   = target.getSampleRate();
			attackFactor = AudioUtilities.dbToLevel(600.0 / sampleRate);
			decayFactor  = AudioUtilities.dbToLevel(-6.0 / sampleRate);
			historySize  = (int)(SMOOTH_DELAY * sampleRate + 0.5);
			sustainSpeed = (int)(SUSTAIN_TIME * sampleRate + 0.5);
			gainEngine   = new GainEngine(historySize, sustainSpeed, attackFactor, decayFactor, MIN_LEVEL);
		}

		public void process(AudioFrame frame) {
			double thresholdLevel = MIN_LEVEL * MAX2AVG;

			gainEngine.process(frame);

			double gain = gainEngine.getGain();
			if (gain < thresholdLevel)
				gain = thresholdLevel;

			float correction = 1.0f;
			if (gain < TARGET_LOWER)
				correction = (float)(TARGET_LOWER / gain);
			else if (gain > TARGET_UPPER)
				correction = (float)(gain / TARGET_UPPER);

			final float[] samples = frame.samples;
			for (int i = 0; i < samples.length; i++)
				samples[i] *= correction;
		}
	}

	@Override
	protected void run(AutoGain.State state) throws RenderCommandException {
		state.process(state.getTarget().getFrame());
	}
	
	@Override
	protected State createState(IAudioRenderTarget target) throws RenderCommandException {
		return new State(target);
	}
}
