package ch.fhnw.ether.controller.event;

import javax.media.opengl.GLAutoDrawable;

public interface IScheduler {
	void addDrawable(GLAutoDrawable drawable);

	void removeDrawable(GLAutoDrawable drawable);

	void requestUpdate(GLAutoDrawable drawable);

	void invokeOnRenderThread(Runnable runnable);

	void invokeOnModelThread(Runnable runnable);
}
