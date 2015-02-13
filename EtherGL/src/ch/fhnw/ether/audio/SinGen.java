package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.Stateless;

public class SinGen extends AbstractRenderCommand<IAudioRenderTarget, Stateless<IAudioRenderTarget>> {
	private static final double PI2 = Math.PI * 2;

	private static final Parameter GAIN  = new Parameter("gain",  "Gain",        0, 1,          0.5f);
	private static final Parameter F     = new Parameter("f",     "Frequency",   0, 20000,      1000);
	private static final Parameter PHI   = new Parameter("phi",   "Phase",       0, (float)PI2, 0);

	private final int channel;
	
	public SinGen(int channel) {
		super(GAIN, F, PHI);
		this.channel = channel;
	}

	@Override
	protected void run(Stateless<IAudioRenderTarget> state) throws RenderCommandException {
		final AudioFrame frame     = state.getTarget().getFrame();
		final float      gain      = getVal(GAIN);
		final float      f         = getVal(F);
		final double     phi       = getVal(PHI);
		final float[]    samples   = frame.samples;
		final int        nChannels = frame.nChannels;
		final int        c         = channel % nChannels;
		final float      sRate     = frame.sRate;
		final long       sTime     = frame.sTime;
		for(int i = 0; i < samples.length; i += nChannels) {
			samples[i+c] += gain * (float)Math.sin(phi + ((f * PI2 * ((sTime + i) / nChannels)) / sRate)); 
		}
	}
}

