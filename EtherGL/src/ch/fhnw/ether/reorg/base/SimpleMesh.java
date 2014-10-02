package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.reorg.api.IGeometry;
import ch.fhnw.ether.reorg.api.IMaterial;
import ch.fhnw.ether.reorg.api.IMesh;

public class SimpleMesh implements IMesh {

	private IGeometry geometry;
	private IMaterial material = IMaterial.EmptyMaterial;
	private float[] position = new float[3];
	
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
		return geometry.getBoundings();
	}

	@Override
	public float[] getPosition() {
		return new float[] {position[0],position[1],position[2]};
	}

	@Override
	public void setPosition(float[] position) {
		this.position[0] = position[0];
		this.position[1] = position[1];
		this.position[2] = position[2];
	}

}
