package ch.fhnw.ether.examples.video.fx;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.Stateless;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.fx.AbstractVideoFX;


public class ChromaKey extends AbstractVideoFX<Stateless<IVideoRenderTarget>> {
	private static final Parameter HUE    = new Parameter("hue",   "Hue",                0, 1,    0.5f);
	private static final Parameter RANGE  = new Parameter("range", "Color Range",        0, 0.5f, 0.1f);
	private static final Parameter S_MIN  = new Parameter("sMin",  "Saturation Minimum", 0, 1,    0.1f);
	private static final Parameter B_MIN  = new Parameter("bMin",  "Brightness Minimum", 0, 1,    0.1f);

	private Frame backdrop = EMPTY;

	public ChromaKey(/*IVideoFrameSource mask, IVideoFrameSource backdrop*/) {
		super(HUE, RANGE, S_MIN, B_MIN);
	}

	@Override
	protected void processFrame(double playOutTime, Stateless<IVideoRenderTarget> state, Frame frame) {
		/*
		backdrop = matchSize(sources[1], backdrop);

		getNextFrame(sources[0], frame);
		getNextFrame(sources[1], backdrop);

		final float h  = getVal(HUE);
		final float r  = getVal(RANGE);
		final float s  = getVal(S_MIN);
		final float b  = getVal(B_MIN);
		final float hh = wrap(h + r);
		final float hl = wrap(h - r);

		frame.processLines((final ByteBuffer pixels, final int j)->{
			final float[] hsb = new float[frame.dimI * 3];
			final int     pos = pixels.position();
			final double  dimI1 = frame.dimI - 1;
			final double  dimJ1 = frame.dimJ - 1;
			ColorUtilities.getHSBfromRGB(pixels, hsb, 4);
			pixels.position(pos);
			for(int i = 0; i < frame.dimI; i++) {
				int idx = i * 3;
				if(hsb[idx+1] > s && hsb[idx+2] > b && hsb[idx+0] > hl && hsb[idx+0] < hh) {
					final double u = i / dimI1;
					final double v = j / dimJ1;
					pixels.put(toByte(backdrop.getComponentBilinear(u, v, 0)));
					pixels.put(toByte(backdrop.getComponentBilinear(u, v, 1)));
					pixels.put(toByte(backdrop.getComponentBilinear(u, v, 2)));
					pixels.put(toByte(backdrop.getComponentBilinear(u, v, 3)));
				} else {
					pixels.get();
					pixels.get();
					pixels.get();
					pixels.get();
				}
			}
		});
		*/
	}
}
