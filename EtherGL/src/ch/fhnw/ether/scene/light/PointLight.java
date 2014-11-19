package ch.fhnw.ether.scene.light;

import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public class PointLight extends GenericLight {
	public PointLight(Vec3 position, RGB ambient, RGB color) {
		this(position, ambient, color, 1.0f, 0.1f, 0.1f);
	}

	public PointLight(Vec3 position, RGB ambient, RGB color, float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
		super(LightSource.pointSource(position, ambient, color, constantAttenuation, linearAttenuation, quadraticAttenuation));
	}
}
