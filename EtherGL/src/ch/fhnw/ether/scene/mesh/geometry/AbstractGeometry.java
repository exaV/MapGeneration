package ch.fhnw.ether.scene.mesh.geometry;

import ch.fhnw.util.IUpdateListener;

public abstract class AbstractGeometry implements IGeometry {
	private final Primitive type;
	private final UpdateListeners listeners = new UpdateListeners();

	protected AbstractGeometry(Primitive type) {
		this.type = type;
	}
	
	@Override
	public final Primitive getType() {
		return type;
	}
	
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
