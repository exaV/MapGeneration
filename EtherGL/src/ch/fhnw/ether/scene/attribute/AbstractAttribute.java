package ch.fhnw.ether.scene.attribute;


public abstract class AbstractAttribute<T> implements ITypedAttribute<T> {
	private final String id;
	
	protected AbstractAttribute(String id) {
		this.id = id;
	}

	@Override
	public final String id() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractAttribute<?> && id.equals(((AbstractAttribute<?>) obj).id))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return id;
	}
}
