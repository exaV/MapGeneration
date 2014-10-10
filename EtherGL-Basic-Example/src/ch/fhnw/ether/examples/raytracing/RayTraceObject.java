package ch.fhnw.ether.examples.raytracing;

import ch.fhnw.ether.examples.raytracing.surface.IParametricSurface;
import ch.fhnw.ether.examples.raytracing.util.IntersectResult;
import ch.fhnw.ether.examples.raytracing.util.Ray;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class RayTraceObject implements IMesh{
	
	private IParametricSurface surface;
	private Vec3 position = Vec3.ZERO;
	private RGBA color = RGBA.WHITE;
	
	public RayTraceObject(IParametricSurface surface) {
		this.surface = surface;
	}
	
	public RayTraceObject(IParametricSurface surface, RGBA color) {
		this(surface);
		this.color = color;
	}

	@Override
	public BoundingBox getBoundings() {
		return new BoundingBox();
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vec3 position) {
		this.position = position;
	}

	@Override
	public IGeometry getGeometry() {
		return null;
	}

	public IntersectResult intersect(Ray ray) {
		Vec3 point = surface.intersect(ray);
		if(point == null) {
			return IntersectResult.VOID;
		}else {
			return new IntersectResult(surface, point, color, ray.origin.subtract(point).length());
		}
	}
	
	@Override
	public IMaterial getMaterial() {
		return new ColorMaterial(color);
	}

	@Override
	public boolean hasChanged() {
		return false;
	}
	
	@Override
	public String toString() {
		return "object=(" + surface + ", rgba=" + color + ")";
	}

}
