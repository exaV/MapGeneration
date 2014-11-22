package ch.fhnw.ether.scene.light;

import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public class SpotLight extends GenericLight {
	public SpotLight(Vec3 position, RGB ambient, RGB color, Vec3 direction, float angle, float softness) {
		super(LightSource.spotSource(position, ambient, color, Float.MAX_VALUE, direction, angle, softness));
	}

	public SpotLight(Vec3 position, RGB ambient, RGB color, float range, Vec3 direction, float angle, float softness) {
		super(LightSource.spotSource(position, ambient, color, range, direction, angle, softness));
	}
}
