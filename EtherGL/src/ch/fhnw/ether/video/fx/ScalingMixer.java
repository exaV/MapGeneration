package ch.fhnw.ether.video.fx;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IScalingFrameSource;
import ch.fhnw.ether.video.IVideoFrameSource;

public class ScalingMixer extends AbstractVideoFX implements IScalingFrameSource {
	private Frame   outFrame  = new RGB8Frame(1,1);
	private Frame[] frames    = new Frame[0];

	protected ScalingMixer(int width, int height, IVideoFrameSource ... sources) {
		super(width, height);
		setSources(sources);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		throw new UnsupportedOperationException();
	}
	
	/*
	@Override
	public Frame getNextFrame() {
		if(sources.length > 0) {
			outFrame = new RGB8Frame(getWidth(), getHeight());
			if(frames.length != getNumSources())
				frames = new Frame[getNumSources()];
			int f = 0;
			for(IFrameSource source : sources)
				frames[f++] = getNextFrame(source);

			final double w = outFrame.dimI;
			final double h = outFrame.dimJ;

			outFrame.processByLine((final ByteBuffer pixels, final int j)->{
				float[] line = new float[outFrame.dimI * 3];

				Arrays.fill(line, 0f);
				for(Frame frame : frames) {
					for(int i = 0; i < frame.dimI; i++) {
						line[i*3+0] += frame.getComponentBilinear(i / w, j / h, 0);
						line[i*3+1] += frame.getComponentBilinear(i / w, j / h, 1);
						line[i*3+2] += frame.getComponentBilinear(i / w, j / h, 2);
					}
				}
				final float s = 255 / getNumSources();
				for(int i = 0; i < line.length; i++)
					pixels.put((byte) (line[i] * s));				
			});
		}
		return outFrame;
	}
	*/

	@Override
	public void setSize(int width, int height) {
		this.width  = width;
		this.height = height;
	}
}
