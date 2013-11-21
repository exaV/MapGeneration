package ch.ethz.util;

import java.util.concurrent.atomic.AtomicBoolean;

public final class UpdateRequest {
	private AtomicBoolean update = new AtomicBoolean();
	
	public void requestUpdate() {
		update.set(true);
	}
	
	public boolean needsUpdate() {
		return update.getAndSet(false);
	}
}