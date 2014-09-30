package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.reorg.api.IGeometry;

public class GenericGeometry implements IGeometry {
	
	private float[][] vertexData; //first dimension is attribute, second data
	private IArrayAttribute[] attributes;
	private BoundingBox boundings;
	
	public GenericGeometry(float[][] attribData, IArrayAttribute[] attributes) {
		this.attributes = attributes;
		this.vertexData = attribData;
		
		boundings = new BoundingBox();
		for(int i=0; i<attributes.length; ++i) {
			if(attributes[i].id() == PositionArray.ID) {
				boundings.add(vertexData[i]);
			}
		}

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

	@Override
	public BoundingBox getBoundings() {
		return boundings;
	}

}
