package ch.fhnw.ether.media;



public class PerTargetState<T extends IRenderTarget> {
	protected final T target;

	public PerTargetState(T target) {
		this.target  = target;
	}

	public T getTarget() {
		return target;
	}
}
