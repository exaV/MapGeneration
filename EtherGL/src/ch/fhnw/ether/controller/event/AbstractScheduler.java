package ch.fhnw.ether.controller.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GLAutoDrawable;

public abstract class AbstractScheduler implements IScheduler {

	private final List<GLAutoDrawable> drawables = new ArrayList<>();

	protected final BlockingQueue<Runnable> renderQueue = new LinkedBlockingQueue<>();
	protected final BlockingQueue<Runnable> modelQueue = new LinkedBlockingQueue<>();

	protected AbstractScheduler() {
		new Thread(this::runRenderThread).start();
		new Thread(this::runModelThread).start();
	}

	@Override
	public void addDrawable(GLAutoDrawable drawable) {
		invokeOnRenderThread(() -> drawables.add(drawable));
	}

	@Override
	public void removeDrawable(GLAutoDrawable drawable) {
		invokeOnRenderThread(() -> drawables.remove(drawable));
	}

	@Override
	public void invokeOnRenderThread(Runnable runnable) {
		renderQueue.add(runnable);
	}

	@Override
	public void invokeOnModelThread(Runnable runnable) {
		modelQueue.add(runnable);
	}

	protected abstract void runRenderThread();

	protected void displayDrawables() {
		for (GLAutoDrawable drawable : drawables) {
			try {
				drawable.display();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void runModelThread() {
		try {
			while (true) {
				try {
					modelQueue.take().run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}		
	}
}
