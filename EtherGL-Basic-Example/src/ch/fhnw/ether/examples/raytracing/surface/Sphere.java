package ch.fhnw.ether.examples.raytracing.surface;

import ch.fhnw.ether.examples.raytracing.util.Ray;
import ch.fhnw.util.math.Vec3;

public class Sphere implements IParametricSurface {

	private float r;
	private Vec3 pos = Vec3.ZERO;
	
	public Sphere(float radius) {
		this.r = radius;
	}

	//From http://www.trenki.net/files/Raytracing1.pdf
	@Override
	public Vec3 intersect(Ray ray) {
		Vec3 o = ray.origin;
		Vec3 d = ray.direction;
		Vec3 c = pos;
		
		Vec3 l = c.subtract(o);
		float t;
		float r2 = r*r;
		float s = l.dot(d);
		float l2 = l.dot(l);
		if(s < 0 && l2 > r2) return null;
		float m2 = l2 - s*s;
		if(m2 > r2) return null;
		float q = (float) Math.sqrt(r2 - m2);
		if(l2 > r2) t = s-q;
		else t = s+q;
		return o.add(d.scale(t));
	}

	@Override
	public Vec3 getNormalAt(Vec3 position) {
		return position.subtract(pos).normalize();
	}
	
	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public Vec3 getPosition() {
		return pos;
	}

	public void setPosition(Vec3 pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "sphere(center=" + pos + ",r=" + r + ")";
	}

}
