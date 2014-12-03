package ch.fhnw.ether.media;

import javax.media.opengl.GL;

import ch.fhnw.ether.image.Frame;


public class SFrameReq extends FrameReq {
	
	public SFrameReq() {
		super(1);
	}

	public SFrameReq(Frame ... frames) {
		super(frames);
	}

	public SFrameReq(GL gl, int numFrames, int textureId) {
		super(gl, numFrames, textureId);
	}
}
