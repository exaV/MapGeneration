package ch.fhnw.ether.media;

import javax.media.opengl.GL;

import ch.fhnw.ether.image.Frame;

public class RFrameReq extends FrameReq {
	private final double  time;
	private final boolean isFrameNumber;
	
	public RFrameReq(double time) {
		this.time          = time;
		this.isFrameNumber = false;
	}

	public RFrameReq(long frameNumber, Frame ... frames) {
		super(frames);
		this.time          = frameNumber;
		this.isFrameNumber = true;
	}

	public RFrameReq(GL gl, double time, int textureId) {
		super(gl, 1, textureId);
		this.time          = time;
		this.isFrameNumber = false;
	}

	public boolean hasFrameNumber() {
		return isFrameNumber;
	}

	public int getFrameNumber() {
		return (int) time;
	}
}
