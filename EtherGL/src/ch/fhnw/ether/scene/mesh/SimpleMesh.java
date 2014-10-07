package ch.fhnw.ether.scene.mesh;

import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.math.geometry.BoundingBox;

public class SimpleMesh implements IMesh {

	private IGeometry geometry;
	private IMaterial material = IMaterial.EmptyMaterial;
	private float[] position = new float[3];
	
	public SimpleMesh(IGeometry geometry) {
		this.geometry = geometry;
	}
	
	public SimpleMesh(IGeometry geometry, IMaterial material) {
		this.geometry = geometry;
		this.material = material;
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
