package ch.fhnw.ether.video.fx;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.IFrameProcessor;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.image.RGBA8Frame;
import ch.fhnw.ether.media.AbstractFX;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameException;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.media.IFrameSource;
import ch.fhnw.ether.video.IRandomAccessFrameSource;
import ch.fhnw.ether.video.ISequentialFrameSource;
import ch.fhnw.ether.video.IVideoFrameSource;

public abstract class AbstractVideoFX extends AbstractFX implements IVideoFX {
	protected final Frame EMPTY = new RGBA8Frame(1,1);
	
	protected long                        frame;
	protected IVideoFrameSource[]         sources;
	protected Class<? extends Frame>[]    frameTypes;
	protected Set<Class<? extends Frame>> preferredTypes;

	protected AbstractVideoFX(FXParameter ... parameters) {
		super(parameters);
	}

	@Override
	public void dispose() {
		for(IVideoFrameSource source : sources)
			source.dispose();
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
	public int getWidth() {
		int result = 0;
		for(IVideoFrameSource source : getSources())
			result = Math.max(result, source.getWidth());
		return result;
	}

	@Override
	public int getHeight() {
		int result = 0;
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

	public final Frame getNextFrame(IFrameSource source, Frame result) {
		if(source instanceof ISequentialFrameSource) {
			ISequentialFrameSource sfs = (ISequentialFrameSource)source;
			FrameReq               req = new FrameReq(result);
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
			rafs.getFrames(new FrameReq(frame++, result));
		}
		return result;
	}

	@Override
	public final IVideoFrameSource[] getSources() {
		return sources;
	}

	@Override
	public final int getNumSources() {
		return sources.length;
	}

	public final void init(Class<? extends Frame>[] frameTypes, IVideoFrameSource ... sources) {
		this.frameTypes     = frameTypes;
		this.preferredTypes = new HashSet<>(Arrays.asList(getFrameTypes()));
		for(IVideoFrameSource source : sources)
			source.setPreferredFrameTypes(preferredTypes);
		this.sources = sources;
	}

	@Override
	public Class<? extends Frame>[] getFrameTypes() {
		return frameTypes;
	}

	@SuppressWarnings("unchecked")
	public String toString(Class<? extends Frame> ... types) {
		StringBuffer result = new StringBuffer();
		for(Class<? extends Frame> type : types) {
			result.append(type.getName());
			result.append(' ');
		}
		return result.toString();
	}

	public final static float toFloat(final byte v) {
		return (v & 0xFF) / 255f;
	}

	public final static byte toByte(final float v) {
		if(v < 0f) return 0;
		if(v > 1f) return -1;
		return (byte) (v * 255f);
	}

	public final static byte toByte(final double v) {
		if(v < 0.0) return 0;
		if(v > 1.0) return -1;
		return (byte) (v * 255.0);
	}

	public final static float wrap(final float v) {
		float result = v % 1f;
		return result < 0 ? result + 1 : result;
	}

	public final static float mix(final float val0, final float val1, float w) {
		return val0 * w + (1f-w) * val1;
	}
	
	protected void processFrames(FrameReq req, IFrameProcessor frameProcessor) {
		req.processFrames(preferredTypes.iterator().next(), getWidth(), getHeight(), frameProcessor);
	}

	@Override
	public void setPreferredFrameTypes(Set<Class<? extends Frame>> frameTypes) {
		IVideoFrameSource.updatePreferredFrameTypes(preferredTypes, frameTypes);
	}
	
	protected static Frame matchSize(IVideoFrameSource source, Frame result) {
		if(source.getWidth() != result.dimI || source.getHeight() != result.dimJ)
			result = result.create(source.getWidth(), source.getHeight());
		return result;
	}
}
