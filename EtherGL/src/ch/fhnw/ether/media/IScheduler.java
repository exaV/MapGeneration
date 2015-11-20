package ch.fhnw.ether.media;

import ch.fhnw.util.Log;

public interface IScheduler {
	static final Log log = Log.create();
	
	double NOT_RENDERING = -1;
	double ASAP = -1000;

	double SEC2NS = 1000 * 1000 * 1000;
	double SEC2US = 1000 * 1000;
	double SEC2MS = 1000;

	boolean                   isRendering();
	void                      start() throws RenderCommandException;
	void                      stop() throws RenderCommandException;
	void                      sleepUntil(double time);
	void                      sleepUntil(double time, Runnable runnable);
	double                    getTime();	
}
