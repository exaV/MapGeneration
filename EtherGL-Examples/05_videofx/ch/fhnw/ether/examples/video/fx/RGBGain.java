package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class RGBGain extends AbstractVideoFX {
	private static final FXParameter RED   = new FXParameter("red",   "Red Gain",   0, 2, 1);
	private static final FXParameter GREEN = new FXParameter("green", "Green Gain", 0, 2, 1);
	private static final FXParameter BLUE  = new FXParameter("blue",  "Blue Gain",  0, 2, 1);

	public RGBGain(IVideoFrameSource source) {
		super(RED, GREEN, BLUE);
		init(FTS_RGBA8_RGB8, source);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		processFrames(req, (Frame frame, int frameIdx)->{
			getNextFrame(sources[0], frame);

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
		});
		return req;
	}
}
