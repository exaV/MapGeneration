package ch.fhnw.ether.audio.fx;

import java.util.Arrays;

import ch.fhnw.ether.audio.AudioFrame;
import ch.fhnw.ether.audio.AudioUtilities;
import ch.fhnw.ether.audio.AverageBuffer;
import ch.fhnw.ether.audio.ButterworthFilter;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;

public class OnsetDetect extends AbstractRenderCommand<IAudioRenderTarget,OnsetDetect.State> {
	private static final float[] BANDS      = { 80, 4000, 10000, 16000 };
	private static final double  CHUNK_SIZE = 0.02;

	public class State extends PerTargetState<IAudioRenderTarget> {
		private final float[]             lastBands = new float[BANDS.length - 1];
		private final float[]             bands     = new float[BANDS.length - 1];
		private final AverageBuffer       flux;      
		private float                     lastThreshold;
		private final ButterworthFilter[] filters   = new ButterworthFilter[BANDS.length - 1];

		public State(IAudioRenderTarget target) {
			super(target);
			flux = new AverageBuffer(target.getSampleRate(), 1, 1.2);
			flux.fill(1);
			for(int i = 0; i < filters.length; i++)
				filters[i] = ButterworthFilter.getBandpassFilter(target.getSampleRate(), BANDS[i], BANDS[i+1]);
		}

		public void process(AudioFrame frame) {

			final float[] monoSamples = frame.getMonoSamples();

			int chunkSize = (int) (frame.sRate * CHUNK_SIZE);
			int numChunks = Math.max(1, ((monoSamples.length - 1) / chunkSize));
			chunkSize = monoSamples.length / numChunks;
			
			for(int start = 0; start < monoSamples.length; start += chunkSize) {
				float lastFlux = 0;
				final int end = Math.min(start + chunkSize, monoSamples.length);
				for(int band = 0; band < BANDS.length - 1; band++) {
					final float[] samples = Arrays.copyOfRange(monoSamples, start, end);
					if(samples.length > 5) {
						filters[band].processBand(samples);
						bands[band] = AudioUtilities.energy(samples);
						float d = bands[band] - lastBands[band];
						lastFlux += d < 0 ? 0 : d * d;
					}
				}
				flux.push(lastFlux, end - start);
			}

			lastThreshold = flux.getAverage();

			System.arraycopy(bands, 0, lastBands, 0, bands.length );
		}

		public float flux() {
			return flux.peek(-0.06);
		}

		public float threshold() {
			return (float)(lastThreshold * 2.0);
		}

		public float onset() {
			float result = flux() / (2f * threshold());
			if(result > 1) result = 1;
			return result;
		}
	}

	public OnsetDetect() {
	}

	@Override
	protected void run(State state) throws RenderCommandException {
		state.process(state.getTarget().getFrame());
	}	

	@Override
	public State createState(IAudioRenderTarget target) throws RenderCommandException {
		return new State(target);
	}
}
