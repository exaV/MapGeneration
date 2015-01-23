package ch.fhnw.ether.video;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.AbstractFrame;

public class VideoFrame extends AbstractFrame {
	public final Frame frame;
	
	public VideoFrame(double playOutTime, Frame frame) {
		super(playOutTime);
		this.frame = frame;
	}

}
