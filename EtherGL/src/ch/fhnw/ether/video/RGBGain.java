package ch.fhnw.ether.video;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.media.FXParameter;

public class RGBGain extends AbstractFX {
	private static final FXParameter red   = new FXParameter("red",   "Red Gain",   0, 2, 1);
	private static final FXParameter green = new FXParameter("green", "Green Gain", 0, 2, 1);
	private static final FXParameter blue  = new FXParameter("blue",  "Blue Gain",  0, 2, 1);

	private Frame   outFrame  = new RGB8Frame(1,1);
	private float[] line   = new float[3];
	private Frame[] frames = new Frame[0];

	protected RGBGain(IFrameSource ... sources) {
		super(AUTO_WIDTH, AUTO_HEIGHT, red, green, blue);
		setSources(sources);
	}

	@Override
	public Frame getNextFrame() {
		if(sources.length > 0) {
			if(outFrame.getWidth() != getWidth() || outFrame.getHeight() != getHeight()) {
				outFrame = new RGB8Frame(getWidth(), getHeight());
				line     = new float[outFrame.dimI * 3];
			}
			if(frames.length != getNumSources())
				frames = new Frame[getNumSources()];
			int f = 0;
			for(IFrameSource source : sources)
				frames[f++] = getNextFrame(source);

			final float  rs = red.getVal();
			final float  gs = green.getVal();
			final float  bs = blue.getVal();
			final double w = outFrame.dimI;
			final double h = outFrame.dimJ;
			for(int j = 0; j < outFrame.dimJ; j++) {
				Arrays.fill(line, 0f);
				for(Frame frame : frames) {
					for(int i = 0; i < frame.dimI; i++) {
						line[i*3+0] += frame.getComponentBilinear(i / w, j / h, 0) * rs;
						line[i*3+1] += frame.getComponentBilinear(i / w, j / h, 1) * gs;
						line[i*3+2] += frame.getComponentBilinear(i / w, j / h, 2) * bs;
					}
				}
				final ByteBuffer pixels = outFrame.pixels;
				pixels.position(j * outFrame.dimI * 3);
				final float s = 255 / getNumSources();
				for(int i = 0; i < line.length; i++)
					pixels.put((byte) (line[i] * s));
			}
		}
		return outFrame;
	}
}
