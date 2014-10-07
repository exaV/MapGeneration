package ch.fhnw.ether.scene.mesh.geometry;

import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;
import ch.fhnw.util.math.ITransformable;
import ch.fhnw.util.math.geometry.BoundingBox;

public interface IGeometry extends IArrayAttributeProvider, ITransformable{
		
	BoundingBox getBoundings();
}
