package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.NormalArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.reorg.api.IGeometry;
import ch.fhnw.ether.scene.Transform;
import ch.fhnw.util.math.Vec3;

public class VertexGeometry implements IGeometry {

	private Transform transform = new Transform();
	
	private float[][] vertexData; //first dimension is attribute, second data
	private IArrayAttribute[] attributes;
	private BoundingBox boundings;
	private PrimitiveType type;
	
	public VertexGeometry(float[][] attribData, IArrayAttribute[] attributes, PrimitiveType type) {
		this.attributes = attributes;
		this.vertexData = attribData;
		this.type = type;
		
		boundings = new BoundingBox();
		int positionArray = -1;
		for(int i=0; i<attributes.length; ++i) {
			if(attributes[i].id() == PositionArray.ID) {
				boundings.add(vertexData[i]);
				positionArray = i;
			}
		}
		
		if(positionArray == -1) {
			throw new IllegalArgumentException("Attributes must contain position");
		}

	}

	@Override
	public void getAttributeSuppliers(PrimitiveType primitiveType,
			ISuppliers dst) {
		if(this.type != primitiveType) {
			throw new RuntimeException("Primitive type is " + primitiveType.name() + 
					" but exptected " + type.name());
		}
		
		for(int i=0; i<attributes.length; ++i) {
			final int n = i;
			dst.add(attributes[i].id(), () -> {
				if(attributes[n].id() == NormalArray.ID) {
					return transform.transformNormals(vertexData[n]);
				} else if(attributes[n].id() == PositionArray.ID) {
					return transform.transformVertices(vertexData[n]);
				} else {
					return vertexData[n];
				}
			});
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

	@Override
	public Vec3 getTranslation() {
		return transform.getTranslation();
	}

	@Override
	public void setTranslation(Vec3 translation) {
		transform.setTranslation(translation);
	}

	@Override
	public Vec3 getRotation() {
		return transform.getRotation();
	}

	@Override
	public void setRotation(Vec3 rotation) {
		transform.setRotation(rotation);
	}

	@Override
	public Vec3 getScale() {
		return transform.getScale();
	}

	@Override
	public void setScale(Vec3 scale) {
		transform.setScale(scale);
	}

	@Override
	public Vec3 getOrigin() {
		return transform.getOrigin();
	}

	@Override
	public void setOrigin(Vec3 origin) {
		transform.setOrigin(origin);
	}

}
