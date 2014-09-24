package ch.fhnw.ether.render.attribute;

import ch.fhnw.ether.render.attribute.base.AbstractAttribute;


public abstract class AbstractArrayAttribute extends AbstractAttribute implements IArrayAttribute {
	private final NumComponents numComponents;
	private int stride;
	private int offset;

	protected AbstractArrayAttribute(String id, String shaderName, NumComponents numComponents) {
		super(id, shaderName);
		this.numComponents = numComponents;
	}

	@Override
	public final NumComponents getNumComponents() {
		return numComponents;
	}

	@Override
	public final void setup(int stride, int offset) {
		this.stride = stride;
		this.offset = offset;
	}

	protected final int getStride() {
		return stride;
	}
	
	protected final int getOffset() {
		return offset;
	}
}
