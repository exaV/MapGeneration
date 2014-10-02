package ch.fhnw.ether.reorg.api;

import ch.fhnw.ether.geom.BoundingBox;

public interface I3DObject {

	BoundingBox getBoundings();	
	
	float[] getPosition();
	
	void setPosition(float[] position);
	
}
