package ch.fhnw.ether.scene.attribute;


public abstract class AbstractAttribute<T> implements ITypedAttribute<T> {
	private final String id;
	
	protected AbstractAttribute(ITypedAttribute<T> attribute) {
		this.id = attribute.id();
	}

	protected AbstractAttribute(String id) {
		this.id = id;
	}

	@Override
	public final String id() {
		return id;
	}

	@Override
	public String toString() {
		return id;
	}
}
