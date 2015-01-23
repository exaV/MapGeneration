package ch.fhnw.ether.media;

public abstract class AbstractFrameSource<T extends IRenderTarget, S extends PerTargetState<T>> extends AbstractRenderCommand<T, S> {
	public static final double FRAMERATE_UNKNOWN  = -1;
	public static final long   FRAMECOUNT_UNKNOWN = -1;

	public abstract long getFrameCount();
}
