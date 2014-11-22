package ch.fhnw.ether.scene.light;

import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public class PointLight extends GenericLight {
	public PointLight(Vec3 position, RGB ambient, RGB color) {
		this(position, ambient, color, Float.MAX_VALUE);
	}

	public PointLight(Vec3 position, RGB ambient, RGB color, float range) {
		super(LightSource.pointSource(position, ambient, color, range));
	}
}
