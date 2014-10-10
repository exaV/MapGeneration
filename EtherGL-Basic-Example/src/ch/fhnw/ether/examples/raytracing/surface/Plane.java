package ch.fhnw.ether.examples.raytracing.surface;

import ch.fhnw.ether.examples.raytracing.util.Ray;
import ch.fhnw.util.math.Vec3;

public class Plane implements IParametricSurface {
	
	private float distance = 0;
	private Vec3 normal = Vec3.Z;
	
	public Plane(Vec3 normal, float distance) {
		this.normal = normal;
		this.distance = distance;
	}
	
	public Plane() {
	}

	@Override
	//From http://www.trenki.net/files/Raytracing1.pdf
	public Vec3 intersect(Ray ray) {
		float t = -(normal.dot(ray.origin) + distance) / normal.dot(ray.direction);
		return t < 0 ? null : ray.origin.add(ray.direction.scale(t));
	}

	@Override
	public Vec3 getNormalAt(Vec3 position) {
		return normal;
	}
	
	@Override
	public String toString() {
		return "plane(n=" + normal + ",d=" + distance + ")";
	}

	@Override
	public void setPosition(Vec3 position) {
	}

	@Override
	public Vec3 getPosition() {
		return normal.scale(distance);
	}

}
