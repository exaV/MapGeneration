package ch.fhnw.ether.examples.raytracing;

import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class PointLight implements ILight {

	private Vec3 position;
	private RGBA color;

	public PointLight(Vec3 position, RGBA color) {
		this.position = position;
		this.color = color;
	}

	@Override
	public BoundingBox getBoundings() {
		BoundingBox b = new BoundingBox();
		b.add(position);
		return b;
	}

	@Override
	public void getAttributeSuppliers(ISuppliers dst) {
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vec3 position) {
		this.position = position;
	}

	@Override
	public RGBA getColor() {
		return color;
	}

	@Override
	public void setColor(RGBA color) {
		this.color = color;
	}

}
