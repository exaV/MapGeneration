package ch.fhnw.ether.reorg.builtin;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.reorg.api.IGeometry;

public class TriangleGeometry implements IGeometry {
	
	private float[][] vertexData; //first dimension is attribute, second data
	private IArrayAttribute[] attributes;
	
	public TriangleGeometry(float[][] attribData, IArrayAttribute[] attributes) {
		this.attributes = attributes;
		this.vertexData = attribData;
	}

	@Override
	public void getAttributeSuppliers(PrimitiveType primitiveType,
			ISuppliers dst) {
		assert primitiveType == PrimitiveType.TRIANGLE;
		
		for(int i=0; i<attributes.length; ++i) {
			final int n = i;
			dst.add(attributes[i].id(), () -> { return vertexData[n]; });
		}
	}

	@Override
	public IArrayAttribute[] getArrayAttributes() {
		return attributes;
	}

}
