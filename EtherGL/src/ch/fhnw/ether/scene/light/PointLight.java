package ch.fhnw.ether.scene.light;

import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public class PointLight extends GenericLight {
	public PointLight(Vec3 position, RGB ambient, RGB color) {
		this(position, ambient, color, 0, 0, 0);
	}

	public PointLight(Vec3 position, RGB ambient, RGB color, float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
		super(new LightParameters(true, false, position, ambient, color, null, 0, 0, constantAttenuation, linearAttenuation, quadraticAttenuation));
	}
}
