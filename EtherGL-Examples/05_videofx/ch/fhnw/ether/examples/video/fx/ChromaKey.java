package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGBA8Frame;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;
import ch.fhnw.util.color.ColorUtils;


public class ChromaKey extends AbstractVideoFX {
	private static final FXParameter HUE    = new FXParameter("hue",   "Hue",                0, 1,    0.5f);
	private static final FXParameter RANGE  = new FXParameter("range", "Color Range",        0, 0.5f, 0.1f);
	private static final FXParameter S_MIN  = new FXParameter("sMin",  "Saturation Minimum", 0, 1,    0.1f);
	private static final FXParameter B_MIN  = new FXParameter("bMin",  "Brightness Minimum", 0, 1,    0.1f);

	private Frame backdrop = EMPTY;
	
	public ChromaKey(IVideoFrameSource mask, IVideoFrameSource backdrop) {
		super(HUE, RANGE, S_MIN, B_MIN);
		init(FTS_RGBA8, mask, backdrop);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		processFrames(req, (Frame frame, int frameIdx)->{
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
				ColorUtils.getHSBfromRGB(pixels, hsb, 4);
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
		});
		return req;
	}
	
	@Override
	public int getWidth() {
		return sources[0].getWidth();
	}
	
	@Override
	public int getHeight() {
		return sources[0].getHeight();
	}
}
