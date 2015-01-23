package ch.fhnw.ether.media;



public interface IRenderTarget {
	boolean           isRendering();
	void              start() throws RenderCommandException;
	void              render() throws RenderCommandException;
	void              stop();
	PerTargetState<?> getState(AbstractRenderCommand<?,?> cmd) throws RenderCommandException;
	void              sleepUntil(double time);
	double            getTime();
}
