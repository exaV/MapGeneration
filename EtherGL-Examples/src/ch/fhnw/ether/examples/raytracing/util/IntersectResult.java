package ch.fhnw.ether.examples.raytracing.util;

import ch.fhnw.ether.examples.raytracing.surface.IParametricSurface;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;

public class IntersectResult {

	public final IParametricSurface surface;
	public final Vec3 position;
	public final RGBA color;
	public final float dist;

	public IntersectResult(IParametricSurface surface, Vec3 position, RGBA color, float dist) {
		this.position = position;
		this.color = color;
		this.dist = dist;
		this.surface = surface;
	}

	public boolean isValid() {
		return surface != null;
	}

	public static final IntersectResult VOID = new IntersectResult(null, null, null, Float.POSITIVE_INFINITY);
}
