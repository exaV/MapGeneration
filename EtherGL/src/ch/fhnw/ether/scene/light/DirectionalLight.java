package ch.fhnw.ether.scene.light;

import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public class DirectionalLight extends GenericLight {
	public DirectionalLight(Vec3 position, RGB ambient, RGB color) {
		super(LightSource.directionalSource(position, ambient, color));
	}
}
