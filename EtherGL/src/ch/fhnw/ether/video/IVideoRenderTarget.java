package ch.fhnw.ether.video;

import ch.fhnw.ether.media.IRenderTarget;

public interface IVideoRenderTarget extends IRenderTarget {
	void       setFrame(VideoFrame frame);
	VideoFrame getFrame();
}
