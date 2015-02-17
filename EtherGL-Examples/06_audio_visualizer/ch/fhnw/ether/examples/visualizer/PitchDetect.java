package ch.fhnw.ether.examples.visualizer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ch.fhnw.ether.audio.AudioUtilities;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.FFT;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.StateHandle;
import ch.fhnw.util.ClassUtilities;
import ch.fhnw.util.math.Vec2;

public class PitchDetect extends AbstractRenderCommand<IAudioRenderTarget,PitchDetect.State> {
	private static final float THRESHOLD = 0.2f;

	private final StateHandle<FFT.State> spectrum;
	private final int                    nHarmonics;
	
	class State extends PerTargetState<IAudioRenderTarget> {
		private final AtomicReference<float[]> pitch = new AtomicReference<>(ClassUtilities.EMPTY_floatA);
		private final List<Vec2>               peaks = new ArrayList<>();

		public State(IAudioRenderTarget target) {
			super(target);
		}

		void process() throws RenderCommandException {
			final IAudioRenderTarget target = getTarget();
			final float[]            spec   = spectrum.get(target).power().clone();
			
			for(int h = 0; h < nHarmonics; h++) {
				final int hop = h + 1;
				final int lim = spec.length / hop;
				for(int i = 0; i < lim; i++)
					spec[i] *= spec[i * hop];
			}
			
			final BitSet peaks  = AudioUtilities.peaks(spec, 3, THRESHOLD);
			this.peaks.clear();

			for (int i = peaks.nextSetBit(0); i >= 0; i = peaks.nextSetBit(i+1))
				this.peaks.add(new Vec2(spec[i], spectrum.get(target).idx2f(i)));

			Collections.sort(this.peaks, (Vec2 v0, Vec2 v1)->v0.x < v1.x ? 1 : v0.x > v1.x ? -1 : 0);
			
			float[] pitch = new float[this.peaks.size()];
			for(int i = 0; i < pitch.length; i++)
				pitch[i] = this.peaks.get(i).y;
			this.pitch.set(pitch);
		}

		public float[] pitch() {
			return pitch.get();
		}
	}

	public PitchDetect(StateHandle<FFT.State> spectrum, int nHarmonics) {
		this.spectrum   = spectrum;
		this.nHarmonics = nHarmonics;
	}

	@Override
	protected void run(State state) throws RenderCommandException {
		state.process();
	}	

	public float[] pitch(IAudioRenderTarget target) {
		return getState(target).pitch();
	}

	@Override
	protected State createState(IAudioRenderTarget target) throws RenderCommandException {
		return new State(target);
	}
}
