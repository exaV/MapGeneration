package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class FadeToColor extends AbstractVideoFX {
	private static final FXParameter FADE  = new FXParameter("fade",  "Fade",  0, 1, 1);
	private static final FXParameter RED   = new FXParameter("red",   "Red",   0, 1, 0);
	private static final FXParameter GREEN = new FXParameter("green", "Green", 0, 1, 0);
	private static final FXParameter BLUE  = new FXParameter("blue",  "Blue",  0, 1, 0);

	public FadeToColor(IVideoFrameSource source) {
		super(FADE, RED, GREEN, BLUE);
		init(FTS_RGBA8_RGB8, source);
	}
	
	@Override
	public FrameReq getFrames(FrameReq req) {
		processFrames(req, (Frame frame, int frameIdx)->{
			getNextFrame(sources[0], frame);

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
		});
		return req;
	}
}
