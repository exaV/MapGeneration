package ch.fhnw.ether.video;

import ch.fhnw.ether.media.IFrameSource;

public interface IVideoFrameSource extends IFrameSource {
	int      getWidth();
	int      getHeight();
}
