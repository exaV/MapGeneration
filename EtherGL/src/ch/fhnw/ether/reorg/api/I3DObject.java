package ch.fhnw.ether.reorg.api;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.util.math.Vec3;

public interface I3DObject {

	BoundingBox getBoundings();	
	
	Vec3 getPosition();
	
}
