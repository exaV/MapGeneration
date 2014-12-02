package ch.fhnw.ether.video;

import java.net.URL;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.FXParameter;

public abstract class AbstractFX implements IVideoFX {
	public static final int AUTO_WIDTH  = -1;
	public static final int AUTO_HEIGHT = -1;

	protected final FXParameter[] parameters;
	protected       IFrameSource[] sources;
	protected final int           width;
	protected final int           height; 
	protected       long          frame;

	protected AbstractFX(int width, int height, FXParameter ... parameters) {
		this.parameters = parameters;
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
	public double getDuration() {
		return DURATION_UNKNOWN;
	}

	@Override
	public double getFrameRate() {
		double result = 25.0;
		if(sources != null)
			for(IFrameSource source : sources)
				result = Math.max(result, source.getFrameRate());
		return result;
	}

	@Override
	public long getFrameCount() {
		return FRAMECOUNT_UNKNOWN;
	}

	@Override
	public int getWidth() {
		int result = width;
		if(width == AUTO_WIDTH && sources != null)
			for(IFrameSource source : sources)
				result = Math.max(result, source.getWidth());
		return result;
	}

	@Override
	public int getHeight() {
		int result = height;
		if(width == AUTO_HEIGHT && sources != null)
			for(IFrameSource source : sources)
				result = Math.max(result, source.getHeight());
		return result;
	}

	@Override
	public FXParameter[] getParameters() {
		return parameters;
	}

	@Override
	public IFrameSource[] getSources() {
		return sources;
	}

	@Override
	public void setSources(IFrameSource[] sources) {
		this.sources = sources;
	}

	@Override
	public int getNumSources() {
		return sources == null ? -1 : sources.length;
	}

	@Override
	public void rewind() {}

	public Frame getNextFrame(IFrameSource source) {
		if(source instanceof ISequentialFrameSource) {
			ISequentialFrameSource sfs = (ISequentialFrameSource)source;
			Frame frame = sfs.getNextFrame();
			if(frame == null) {
				sfs.rewind();
				frame = sfs.getNextFrame();
			}
			return frame;
		} else if(source instanceof IRandomAccessFrameSource) {
			IRandomAccessFrameSource rafs = (IRandomAccessFrameSource)source;
			if(frame >= rafs.getFrameCount())
				frame = 0;
			return rafs.getFrame(frame++);
		}
		return null;
	}
}
