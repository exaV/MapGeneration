package ch.fhnw.ether.media;

import ch.fhnw.util.Log;

public interface IScheduler extends ITimebase {
	static final Log log = Log.create();
	
	double NOT_RENDERING = -1;

	boolean isRendering();
	void    start() throws RenderCommandException;
	void    stop() throws RenderCommandException;
	void    sleepUntil(double time);
	void    sleepUntil(double time, Runnable runnable);
	void    setTimebase(ITimebase timebase);
}
