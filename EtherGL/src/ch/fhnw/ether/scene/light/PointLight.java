package ch.fhnw.ether.scene.light;

import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public class PointLight extends AbstractLight {
	public static LightAttribute POINT_LIGHT = new LightAttribute("builtin.light.point_light");

	private float intensity;
	private float distance;
	
	public PointLight(Vec3 position, RGB color) {
		this(position, color, 1, Float.POSITIVE_INFINITY);
	}

	public PointLight(Vec3 position, RGB color, float intensity, float distance) {
		super(position, color);
		this.intensity = intensity;
		this.distance = distance;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	@Override
	public void getAttributeSuppliers(ISuppliers suppliers) {
		suppliers.provide(POINT_LIGHT, () -> this);
	}
}
