package ch.fhnw.ether.scene.mesh.material;

import ch.fhnw.util.IUpdateListener;

public abstract class AbstractMaterial implements IMaterial {
	private final UpdateListeners listeners = new UpdateListeners();

	@Override
	public final void addUpdateListener(IUpdateListener listener) {
		listeners.addListener(listener);
	}

	@Override
	public final void removeUpdateListener(IUpdateListener listener) {
		listeners.removeListener(listener);
	}

	protected void requestUpdate() {
		listeners.requestUpdate(this);
	}
}
