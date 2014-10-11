package ch.fhnw.ether.examples.raytracing.util;

import ch.fhnw.util.math.Vec3;

public class Ray {
	public final Vec3 origin;
	public final Vec3 direction;

	public Ray(Vec3 origin, Vec3 direction) {
		this.origin = origin;
		this.direction = direction.normalize();
	}

	@Override
	public String toString() {
		return "[origin:" + origin + ", direction:" + direction + "]";
	}

}
