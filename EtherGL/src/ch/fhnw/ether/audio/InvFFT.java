package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.Stateless;

public class InvFFT extends AbstractRenderCommand<IAudioRenderTarget, Stateless<IAudioRenderTarget>> {
	private final FFT fft;
	
	public InvFFT(FFT fft) {
		this.fft = fft;
	}

	@Override
	protected void run(Stateless<IAudioRenderTarget> state) throws RenderCommandException {
		fft.inverse(state.getTarget());
	}
}