package ch.fhnw.ether.audio.fx;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.Stateless;

public class AudioGain extends AbstractRenderCommand<IAudioRenderTarget,Stateless<IAudioRenderTarget>> {
	private static final Parameter GAIN = new Parameter("gain", "Gain", 0, 1, 1);
	
	public AudioGain() {
		super(GAIN);
	}
	
	@Override
	protected void run(Stateless<IAudioRenderTarget> state) throws RenderCommandException {
		final float   gain    = getVal(GAIN);
		final float[] samples = state.getTarget().getFrame().samples;
		for(int i = 0; i < samples.length; i++)
			samples[i] *= gain;
	}
}
