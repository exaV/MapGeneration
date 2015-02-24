/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */package ch.fhnw.ether.audio.fx;

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
