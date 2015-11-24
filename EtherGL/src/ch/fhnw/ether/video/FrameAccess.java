package ch.fhnw.ether.video;

import java.io.IOException;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.AbstractFrameSource;
import ch.fhnw.ether.scene.mesh.material.Texture;

public class FrameAccess {
	protected final URLVideoSource src;
	protected int                  numPlays;
	private final Frame            frame;

	FrameAccess(URLVideoSource src) throws IOException {
		this.frame    = Frame.create(src.getURL());
		this.src      = src;
		this.numPlays = 0;
	}

	protected FrameAccess(URLVideoSource src, int numPlays) {
		this.frame    = null;
		this.src      = src;
		this.numPlays = numPlays;
	}

	protected boolean skipFrame() {return false;}

	protected Frame getNextFrame() {
		return frame;
	}
	protected int getWidth() {
		return frame.dimI;
	}
	protected int getHeight() {
		return frame.dimJ;
	}
	protected float getFrameRate() {
		return AbstractFrameSource.FRAMERATE_UNKNOWN;
	}

	protected long getFrameCount() {
		return 1;
	}

	protected double getDuration() {
		return AbstractFrameSource.LENGTH_INFINITE;
	}

	public Texture getNextTexture() {
		return frame.getTexture();
	}
}
