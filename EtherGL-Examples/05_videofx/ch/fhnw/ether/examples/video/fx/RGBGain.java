package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.Stateless;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class RGBGain extends AbstractVideoFX<Stateless<IVideoRenderTarget>> {
	private static final Parameter RED   = new Parameter("red",   "Red Gain",   0, 2, 1);
	private static final Parameter GREEN = new Parameter("green", "Green Gain", 0, 2, 1);
	private static final Parameter BLUE  = new Parameter("blue",  "Blue Gain",  0, 2, 1);

	public RGBGain() {
		super(RED, GREEN, BLUE);
	}

	@Override
	protected void processFrame(double playOutTime, Stateless<IVideoRenderTarget> state, Frame frame) {
		final float rs = getVal(RED);
		final float gs = getVal(GREEN);
		final float bs = getVal(BLUE);

		if(frame.pixelSize == 4) {
			frame.processLines((final ByteBuffer pixels, final int j)->{
				int idx = pixels.position();
				for(int i = 0; i < frame.dimI; i++) {
					pixels.put(toByte(toFloat(pixels.get(idx++)) * rs));
					pixels.put(toByte(toFloat(pixels.get(idx++)) * gs));
					pixels.put(toByte(toFloat(pixels.get(idx++)) * bs));
					pixels.get();
					idx++;
				}
			});
		} else {
			frame.processLines((final ByteBuffer pixels, final int j)->{
				int idx = pixels.position();
				for(int i = 0; i < frame.dimI; i++) {
					pixels.put(toByte(toFloat(pixels.get(idx++)) * rs));
					pixels.put(toByte(toFloat(pixels.get(idx++)) * gs));
					pixels.put(toByte(toFloat(pixels.get(idx++)) * bs));
				}
			});
		}
	}
}
