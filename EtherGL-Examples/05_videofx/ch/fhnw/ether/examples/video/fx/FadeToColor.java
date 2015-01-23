package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.Stateless;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class FadeToColor extends AbstractVideoFX<Stateless<IVideoRenderTarget>> {
	private static final Parameter FADE  = new Parameter("fade",  "Fade",  0, 1, 1);
	private static final Parameter RED   = new Parameter("red",   "Red",   0, 1, 0);
	private static final Parameter GREEN = new Parameter("green", "Green", 0, 1, 0);
	private static final Parameter BLUE  = new Parameter("blue",  "Blue",  0, 1, 0);

	public FadeToColor() {
		super(FADE, RED, GREEN, BLUE);
	}

	@Override
	protected void processFrame(double playOutTime, Stateless<IVideoRenderTarget> state, Frame frame) {
		final float w  = getVal(FADE);
		final float rs = getVal(RED);
		final float gs = getVal(GREEN);
		final float bs = getVal(BLUE);

		if(frame.pixelSize == 4) {
			frame.processLines((final ByteBuffer pixels, final int j)->{
				int idx = pixels.position();
				for(int i = 0; i < frame.dimI; i++) {
					pixels.put(toByte(mix(toFloat(pixels.get(idx++)), rs, w)));
					pixels.put(toByte(mix(toFloat(pixels.get(idx++)), gs, w)));
					pixels.put(toByte(mix(toFloat(pixels.get(idx++)), bs, w)));
					pixels.get();
					idx++;
				}
			});
		} else {
			frame.processLines((final ByteBuffer pixels, final int j)->{
				int idx = pixels.position();
				for(int i = 0; i < frame.dimI; i++) {
					pixels.put(toByte(mix(toFloat(pixels.get(idx++)), rs, w)));
					pixels.put(toByte(mix(toFloat(pixels.get(idx++)), gs, w)));
					pixels.put(toByte(mix(toFloat(pixels.get(idx++)), bs, w)));
				}
			});
		}
	}
}
