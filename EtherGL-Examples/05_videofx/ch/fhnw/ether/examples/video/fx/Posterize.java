package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class Posterize extends AbstractVideoFX {
	private static final FXParameter MASK = new FXParameter("mask", "Bit Mask", 0, 7, 0);

	public Posterize(IVideoFrameSource source) {
		super(MASK);
		init(FTS_RGBA8_RGB8, source);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		processFrames(req, (Frame frame, int frameIdx)->{
			getNextFrame(sources[0], frame);

			final int mask = 0xFF << (int)getVal(MASK);

			if(frame.pixelSize == 4) {
				frame.processLines((final ByteBuffer pixels, final int j)->{
					int idx = pixels.position();
					for(int i = 0; i < frame.dimI; i++) {
						pixels.put((byte)(pixels.get(idx++) & mask));
						pixels.put((byte)(pixels.get(idx++) & mask));
						pixels.put((byte)(pixels.get(idx++) & mask));
						pixels.get();
						idx++;
					}
				});
			} else {
				frame.processLines((final ByteBuffer pixels, final int j)->{
					int idx = pixels.position();
					for(int i = 0; i < frame.dimI; i++) {
						pixels.put((byte)(pixels.get(idx++) & mask));
						pixels.put((byte)(pixels.get(idx++) & mask));
						pixels.put((byte)(pixels.get(idx++) & mask));
					}
				});
			}
		});
		return req;
	}
}
