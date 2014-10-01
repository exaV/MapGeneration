package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.reorg.api.IGeometry;
import ch.fhnw.ether.reorg.api.IMaterial;
import ch.fhnw.ether.reorg.api.IMesh;
import ch.fhnw.ether.reorg.builtin.EmptyMaterial;

public class SimpleMesh implements IMesh {

	private IGeometry geometry;
	private IMaterial material = new EmptyMaterial();
	
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

}
