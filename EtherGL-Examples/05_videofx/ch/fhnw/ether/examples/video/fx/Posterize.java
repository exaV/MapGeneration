package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.Stateless;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class Posterize extends AbstractVideoFX<Stateless<IVideoRenderTarget>> {
	private static final Parameter MASK = new Parameter("mask", "Bit Mask", 0, 7, 0);

	public Posterize() {
		super(MASK);
	}

	@Override
	protected void processFrame(double playOutTime, Stateless<IVideoRenderTarget> state, Frame frame) {
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
	}
}
