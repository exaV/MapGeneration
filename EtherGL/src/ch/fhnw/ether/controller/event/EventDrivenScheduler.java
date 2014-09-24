package ch.fhnw.ether.controller.event;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.media.opengl.GLAutoDrawable;

public final class EventDrivenScheduler extends AbstractScheduler {
	private static final class RequestUpdate implements Runnable {
		AtomicBoolean scheduled = new AtomicBoolean();

		@Override
		public void run() {
			// FIXME: this doesn't really work yet....
			scheduled.set(false);
		}
	};

	private final RequestUpdate update = new RequestUpdate();

	public EventDrivenScheduler() {
	}
	
	@Override
	protected void runRenderThread() {
		try {
			while (true) {
				try {
					renderQueue.take().run();
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void requestUpdate(GLAutoDrawable drawable) {
		// note: currently drawable is ignored and we simply update all of them
		if (!update.scheduled.getAndSet(true)) {
			invokeOnRenderThread(update);
		}
	}
}
