package ch.fhnw.ether.reorg.api;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;

public interface IGeometry extends IArrayAttributeProvider{
	
	IArrayAttribute[] getArrayAttributes();
	
	BoundingBox getBoundings();
}
