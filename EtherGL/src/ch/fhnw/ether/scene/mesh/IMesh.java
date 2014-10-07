package ch.fhnw.ether.scene.mesh;

import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.math.geometry.I3DObject;

public interface IMesh extends I3DObject {
	
	IGeometry getGeometry();
	
	IMaterial getMaterial();
	
}
