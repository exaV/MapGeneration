package ch.fhnw.ether.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.image.RGBA8Frame;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IFrameSource;

public class RGBGain extends AbstractFX {
	private static final FXParameter red   = new FXParameter("red",   "Red Gain",   0, 2, 1);
	private static final FXParameter green = new FXParameter("green", "Green Gain", 0, 2, 1);
	private static final FXParameter blue  = new FXParameter("blue",  "Blue Gain",  0, 2, 1);

	public RGBGain(IFrameSource source) {
		super(source.getWidth(), source.getHeight(), red, green, blue);
		setSources(source);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		req.processFrames(RGB8Frame.class, getWidth(), getHeight(), (Frame frame, int frameIdx)->{
			getNextFrame(sources[0], frame);

			final float  rs = red.getVal();
			final float  gs = green.getVal();
			final float  bs = blue.getVal();

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
