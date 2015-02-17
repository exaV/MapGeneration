package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.StateHandle;
import ch.fhnw.ether.media.Stateless;

public class InvFFT extends AbstractRenderCommand<IAudioRenderTarget, Stateless<IAudioRenderTarget>> {
	private final StateHandle<FFT.State> fft;
	
	public InvFFT(StateHandle<FFT.State> fft) {
		this.fft = fft;
	}

	@Override
	protected void run(Stateless<IAudioRenderTarget> state) throws RenderCommandException {
		fft.get(state.getTarget()).inverse();
	}
}