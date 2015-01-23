package ch.fhnw.ether.media;

public class Stateless<T extends IRenderTarget> extends PerTargetState<T> {
	public Stateless(T target) {
		super(target);
	}
}
