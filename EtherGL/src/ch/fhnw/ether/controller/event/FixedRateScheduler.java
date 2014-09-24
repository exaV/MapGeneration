package ch.fhnw.ether.controller.event;

import javax.media.opengl.GLAutoDrawable;

// TODO: currently this is "max-rate" scheduling. implement framerate handling...
public final class FixedRateScheduler extends AbstractScheduler {
	public FixedRateScheduler() {
	}
	
	@Override
	protected void runRenderThread() {
		try {
			while (true) {
				try {
					while (renderQueue.peek() != null)
						renderQueue.take().run();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					displayDrawables();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread.yield();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void requestUpdate(GLAutoDrawable drawable) {
		// this is ignored since we're continuously rendering
	}
}
