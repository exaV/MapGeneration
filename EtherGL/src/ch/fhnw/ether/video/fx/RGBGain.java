package ch.fhnw.ether.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.image.RGBA8Frame;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IVideoFrameSource;

public class RGBGain extends AbstractVideoFX {
	private static final FXParameter RED   = new FXParameter("red",   "Red Gain",   0, 2, 1);
	private static final FXParameter GREEN = new FXParameter("green", "Green Gain", 0, 2, 1);
	private static final FXParameter BLUE  = new FXParameter("blue",  "Blue Gain",  0, 2, 1);

	public RGBGain(IVideoFrameSource source) {
		super(source.getWidth(), source.getHeight(), RED, GREEN, BLUE);
		setSources(source);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		req.processFrames(RGB8Frame.class, getWidth(), getHeight(), (Frame frame, int frameIdx)->{
			getNextFrame(sources[0], frame);

			final float rs = getVal(RED);
			final float gs = getVal(GREEN);
			final float bs = getVal(BLUE);

			if(frame instanceof RGBA8Frame) {
				frame.processByLine((final ByteBuffer pixels, final int j)->{
					int idx = pixels.position();
					for(int i = 0; i < frame.dimI; i++) {
						pixels.put(toByteSigned(pixels.get(idx++) * rs));
						pixels.put(toByteSigned(pixels.get(idx++) * gs));
						pixels.put(toByteSigned(pixels.get(idx++) * bs));
						pixels.get();
					}
				});
			} else {
				frame.processByLine((final ByteBuffer pixels, final int j)->{
					int idx = pixels.position();
					for(int i = 0; i < frame.dimI; i++) {
						pixels.put(toByteSigned(pixels.get(idx++) * rs));
						pixels.put(toByteSigned(pixels.get(idx++) * gs));
						pixels.put(toByteSigned(pixels.get(idx++) * bs));
					}
				});
			}
		});
		return req;
	}
}
