package ch.fhnw.ether.video.fx;

import java.net.URL;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.AbstractFX;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameException;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.media.IFrameSource;
import ch.fhnw.ether.video.IRandomAccessFrameSource;
import ch.fhnw.ether.video.ISequentialFrameSource;
import ch.fhnw.ether.video.IVideoFrameSource;

public abstract class AbstractVideoFX extends AbstractFX implements IVideoFX {
	protected       int           width;
	protected       int           height; 
	protected       long          frame;
	protected IVideoFrameSource[] sources;

	protected AbstractVideoFX(int width, int height, FXParameter ... parameters) {
		super(parameters);
		this.width      = width;
		this.height     = height;
	}

	@Override
	public void dispose() {
	}

	@Override
	public URL getURL() {
		return null;
	}

	@Override
	public final double getDuration() {
		return DURATION_UNKNOWN;
	}

	@Override
	public final double getFrameRate() {
		double result = 25.0;
		for(IFrameSource source : getSources())
			result = Math.max(result, source.getFrameRate());
		return result;
	}

	@Override
	public final long getFrameCount() {
		return FRAMECOUNT_UNKNOWN;
	}

	@Override
	public final int getWidth() {
		int result = width;
		for(IVideoFrameSource source : getSources())
			result = Math.max(result, source.getWidth());
		return result;
	}

	@Override
	public final int getHeight() {
		int result = height;
		for(IVideoFrameSource source : getSources())
			result = Math.max(result, source.getHeight());
		return result;
	}

	@Override
	public final FXParameter[] getParameters() {
		return parameters;
	}

	@Override
	public void rewind() {}

	public final void getNextFrame(IFrameSource source, Frame outFrame) {
		if(source instanceof ISequentialFrameSource) {
			ISequentialFrameSource sfs = (ISequentialFrameSource)source;
			FrameReq               req = new FrameReq(outFrame);
			try {
				sfs.getFrames(req);
			} catch(FrameException e) {
				sfs.rewind();
				sfs.getFrames(req);
			}
		} else if(source instanceof IRandomAccessFrameSource) {
			IRandomAccessFrameSource rafs = (IRandomAccessFrameSource)source;
			if(frame >= rafs.getFrameCount())
				frame = 0;
			rafs.getFrames(new FrameReq(frame++, outFrame));
		}
	}

	@Override
	public final IVideoFrameSource[] getSources() {
		return sources;
	}

	@Override
	public final int getNumSources() {
		return sources.length;
	}

	public final void setSources(IVideoFrameSource ... sources) {
		this.sources = sources;
	}
	
	public final static byte toByteSigned(float v) {
		if(v < -128) return -128;
		if(v > 127)  return 127;
		return (byte)v;
	}
}
