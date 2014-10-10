package ch.fhnw.ether.examples.raytracing.surface;

import ch.fhnw.ether.examples.raytracing.util.Ray;
import ch.fhnw.util.math.Vec3;

public interface IParametricSurface {
	Vec3 intersect(Ray ray);
	Vec3 getNormalAt(Vec3 position);
}
