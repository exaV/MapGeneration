package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.reorg.api.IGeometry;
import ch.fhnw.ether.reorg.api.IMaterial;
import ch.fhnw.ether.reorg.api.IMesh;
import ch.fhnw.ether.reorg.builtin.EmptyMaterial;
import ch.fhnw.util.math.Vec3;

public class SimpleMesh implements IMesh {

	private IGeometry geometry;
	private IMaterial material = new EmptyMaterial();
	private Vec3 position = new Vec3(0,0,0);
	
	public SimpleMesh(IGeometry geometry) {
		this.geometry = geometry;
	}
	
	@Override
	public IGeometry getGeometry() {
		return geometry;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public BoundingBox getBoundings() {
		return null;
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

}
