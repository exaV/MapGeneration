package ch.fhnw.ether.reorg.api;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;
import ch.fhnw.ether.scene.ITransformable;

public interface IGeometry extends IArrayAttributeProvider, ITransformable{
	
	IArrayAttribute[] getArrayAttributes();
	
	BoundingBox getBoundings();
}
