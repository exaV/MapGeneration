package ch.fhnw.ether.examples.video.fx;

import java.util.Arrays;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.Stateless;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class ScalingMixer extends AbstractVideoFX<Stateless<IVideoRenderTarget>> {
	private static Parameter[] GAINS = new Parameter[0]; 

	private Frame[] frames    = new Frame[0];
	private float[] gains     = new float[0];
	private int     width     = 0;
	private int     height    = 0;

	public ScalingMixer(/*IVideoFrameSource ... sources*/) {
	//	super(makeParams(sources.length));
	}

	private static Parameter[] makeParams(int n) {
		int count = GAINS.length;
		if(n > GAINS.length)
			GAINS = Arrays.copyOf(GAINS, n);
		for(int i = count; i < n; i++)
			GAINS[i] = new Parameter("g" + i, "Gain channel " + i, 0, 1, i == 0 ? 1 : 0);
		return Arrays.copyOf(GAINS, n);
	}

	@Override
	protected void processFrame(double playOutTime, Stateless<IVideoRenderTarget> state, Frame frame) {
		/*
		final double dimI1 = frame.dimI - 1;
		final double dimJ1 = frame.dimJ - 1;
		if(frames.length < sources.length) {
			frames = Arrays.copyOf(frames, sources.length);
			gains  = new float[frames.length];
		}
		for(int i = 0; i < sources.length; i++) {
			gains[i]  = getVal(GAINS[i]);
			frames[i] = matchSize(sources[i], 
					frames[i] != null ? 
							frames[i] : 
								frame.create(sources[i].getWidth(), sources[i].getHeight()));
		}

		int count = 0;
		for(int i = 0; i < sources.length; i++)
			if(gains[i] > 0)
				count++;
		int[] inFrames = new int[count];
		count          = 0;
		for(int i = 0; i < sources.length; i++)
			if(gains[i] > 0) {
				getNextFrame(sources[i], frames[i]);
				inFrames[count++] = i;
			}
		if(frame.pixelSize == 4) {
			frame.processLines((final ByteBuffer pixels, final int j)->{
				for(int i = 0; i < frame.dimI; i++) {
					final double u = i / dimI1;
					final double v = j / dimJ1;
					float r = 0;
					float g = 0;
					float b = 0;
					float a = 0;
					for(int f : inFrames) {
						r += frames[f].getComponentBilinear(u, v, 0) * gains[f];
						g += frames[f].getComponentBilinear(u, v, 1) * gains[f];
						b += frames[f].getComponentBilinear(u, v, 2) * gains[f];
						a += frames[f].getComponentBilinear(u, v, 3) * gains[f];
					}
					pixels.put(toByte(r));
					pixels.put(toByte(g));
					pixels.put(toByte(b));
					pixels.put(toByte(a));
				}
			});
		} else {
			frame.processLines((final ByteBuffer pixels, final int j)->{
				for(int i = 0; i < frame.dimI; i++) {
					final double u = i / dimI1;
					final double v = j / dimJ1;
					float r = 0;
					float g = 0;
					float b = 0;
					for(int f : inFrames) {
						r += frames[f].getComponentBilinear(u, v, 0) * gains[f];
						g += frames[f].getComponentBilinear(u, v, 1) * gains[f];
						b += frames[f].getComponentBilinear(u, v, 2) * gains[f];
					}
					pixels.put(toByte(r));
					pixels.put(toByte(g));
					pixels.put(toByte(b));
				}
			});
		}
		*/
	}

	public void setSize(int width, int height) {
		/*
		this.width  = width;
		this.height = height;
		for(IVideoFrameSource src : sources)
			if(src instanceof IScalingFrameSource)
				((IScalingFrameSource)src).setSize(width, height);
				*/
	}
}
