package ch.fhnw.ether.media;

public interface IScheduler {
	double NOT_RENDERING = -1;

	boolean                   isRendering();
	void                      start() throws RenderCommandException;
	void                      stop() throws RenderCommandException;
	void                      sleepUntil(double time);
	double                    getTime();
}
