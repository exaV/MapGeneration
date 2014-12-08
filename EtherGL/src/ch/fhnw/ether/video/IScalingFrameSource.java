package ch.fhnw.ether.video;

import ch.fhnw.ether.media.IFrameSource;

public interface IScalingFrameSource extends IFrameSource {
	void setSize(int width, int height);
}
