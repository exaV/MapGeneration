package ch.fhnw.ether.video;

import ch.fhnw.ether.media.AbstractFrameSource;
import ch.fhnw.ether.media.PerTargetState;

public abstract class AbstractVideoSource<S extends PerTargetState<IVideoRenderTarget>> extends AbstractFrameSource<IVideoRenderTarget, S> {
	public abstract int    getWidth();
	public abstract int    getHeight();
	public abstract double getFrameRate();
}
