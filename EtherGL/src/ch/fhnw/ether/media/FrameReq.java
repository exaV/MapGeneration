package ch.fhnw.ether.media;

import javax.media.opengl.GL;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.IFrameProcessor;

public final class FrameReq {
	private final int     numFrames;
	private final Frame[] frames;
	private final int     textureId;
	private final GL      gl;
	private final double  time;
	private final boolean isFrameNumber;
	private final boolean isSequential;

	public FrameReq() {
		this(new Frame[1]);
	}
	
	public FrameReq(Frame ... frames) {
		this.frames        = frames;
		this.numFrames     = frames.length;
		this.textureId     = -1;
		this.gl            = null;
		this.isSequential  = true;
		this.time          = 0;
		this.isFrameNumber = false;
	}

	public FrameReq(int numFrames) {
		this(new Frame[numFrames]);
	}

	public FrameReq(GL gl, int numFrames, int textureId) {
		this.numFrames    = numFrames;
		this.frames       = new Frame[numFrames];
		this.textureId    = textureId;
		this.gl           = gl;
		this.isSequential = true;
		this.time          = 0;
		this.isFrameNumber = false;
	}
	
	public FrameReq(double time) {
		this.frames        = new Frame[1];
		this.numFrames     = frames.length;
		this.textureId     = -1;
		this.gl            = null;
		this.isSequential  = false;
		this.time          = time;
		this.isFrameNumber = false;
	}

	public FrameReq(long frameNumber, Frame ... frames) {
		this.frames        = frames;
		this.numFrames     = frames.length;
		this.textureId     = -1;
		this.gl            = null;
		this.isSequential  = false;
		this.time          = frameNumber;
		this.isFrameNumber = true;
	}

	public FrameReq(GL gl, double time, int textureId) {
		this.frames        = new Frame[1];
		this.numFrames     = this.frames.length;
		this.textureId     = textureId;
		this.gl            = gl;
		this.isSequential  = false;
		this.time          = time;
		this.isFrameNumber = false;
	}

	public boolean hasFrameNumber() {
		return isFrameNumber;
	}

	public int getFrameNumber() {
		return (int) time;
	}

	public int getNumFrames() {
		return numFrames;
	}

	public Frame getFrame(int i) {
		return frames[i];
	}

	public void setFrame(int i, Frame frame) {
		frames[i] = frame;
	}

	public Frame getFrame() {
		return getFrame(0);
	}

	public void loadFrames() {
		if(frames[0] != null && hasTextureId()) {
			for(Frame frame : frames)
				frame.load(gl, GL.GL_TEXTURE_2D, textureId);
		}
	}

	public void processFrames(Class<? extends Frame> preferredType, int preferredWidth, int preferredHeight, IFrameProcessor frameProcessor) {
		try {
			if(frames[0] == null)
				for(int i = 0; i < numFrames; i++)
					frames[i] = preferredType.getConstructor(int.class, int.class).newInstance(preferredWidth, preferredHeight);
			for(int i = 0; i < numFrames; i++)
				frameProcessor.process(frames[i], i);
			loadFrames();
		} catch(Throwable t) {
			throw new FrameException("Failed to create frame of type " + preferredType.getName(), t);
		}
	}

	public boolean hasTextureId() {
		return gl != null;
	}
	
	public boolean allFramesOfType(Class<? extends Frame> type) {
		for(Frame frame : frames)
			if(frame == null || type != frame.getClass()) return false;
		return true;
	}

	public boolean isSequential() {
		return isSequential;
	}
}
