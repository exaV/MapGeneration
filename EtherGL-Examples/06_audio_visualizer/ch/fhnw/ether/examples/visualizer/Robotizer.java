package ch.fhnw.ether.examples.visualizer;

import ch.fhnw.ether.audio.FFT;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.StateHandle;
import ch.fhnw.ether.media.Stateless;

public class Robotizer extends AbstractRenderCommand<IAudioRenderTarget, Stateless<IAudioRenderTarget>> {
	private final StateHandle<FFT.State> fft;
	
	public Robotizer(StateHandle<FFT.State> fft) {
		this.fft = fft;
	}
	
	@Override
	protected void run(Stateless<IAudioRenderTarget> state) throws RenderCommandException {
		fft.get(state.getTarget()).modifySpectrum((float[] spectrum)->{
			for(int i = 1; i < spectrum.length; i += 2)
				spectrum[i] = 0;
		});
	}

}
