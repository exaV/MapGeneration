package ch.fhnw.ether.audio.fx;

import java.net.URL;

import ch.fhnw.ether.audio.IAudioFrameSource;
import ch.fhnw.ether.media.AbstractFX;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.IFrameSource;

public abstract class AbstractAudioFX extends AbstractFX implements IAudioFX {
	protected IAudioFrameSource[] sources;
	protected final int           channelCount;

	protected AbstractAudioFX(int channelCount, FXParameter ... parameters) {
		super(parameters);
		this.channelCount = channelCount;
	}

	@Override
	public void dispose() {
		for(IAudioFrameSource source : sources)
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
		double result = 44100;
		for(IFrameSource source : getSources())
			result = Math.max(result, source.getFrameRate());
		return result;
	}

	@Override
	public final long getFrameCount() {
		return FRAMECOUNT_UNKNOWN;
	}

	@Override
	public final FXParameter[] getParameters() {
		return parameters;
	}

	@Override
	public final IAudioFrameSource[] getSources() {
		return sources;
	}

	@Override
	public final int getNumSources() {
		return sources.length;
	}

	public final void setSources(IAudioFrameSource ... sources) {
		this.sources = sources;
	}
	
	@Override
	public int getChannelCount() {
		return channelCount;
	}
}
