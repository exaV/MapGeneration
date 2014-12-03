package ch.fhnw.ether.video;

public interface IMixingFrameSource extends IFrameSource {
	void setSources(IFrameSource[] sources);
	void addSource(IFrameSource source);
	void removeSource(IFrameSource source);
}
