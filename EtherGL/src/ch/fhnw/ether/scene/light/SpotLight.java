package ch.fhnw.ether.scene.light;

import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public class SpotLight extends GenericLight {
	public SpotLight(Vec3 position, RGB ambient, RGB color, Vec3 direction) {
		this(position, ambient, color, direction, 15.0f, 1f, 1.0f, 0.1f, 0.1f);
	}

	public SpotLight(Vec3 position, RGB ambient, RGB color, Vec3 direction, float cutoff, float exponent, float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
		super(LightSource.spotSource(position, ambient, color, direction, cutoff, exponent, constantAttenuation, linearAttenuation, quadraticAttenuation));
	}
}
