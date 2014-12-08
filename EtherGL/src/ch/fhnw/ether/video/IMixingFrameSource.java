package ch.fhnw.ether.video;

import ch.fhnw.ether.media.IFrameSource;

public interface IMixingFrameSource extends IFrameSource {
	void setSources(IFrameSource[] sources);
	void addSource(IFrameSource source);
	void removeSource(IFrameSource source);
}
