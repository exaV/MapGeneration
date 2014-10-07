package ch.fhnw.util.math.geometry;



public interface I3DObject {

	BoundingBox getBoundings();	
	
	float[] getPosition();
	
	void setPosition(float[] position);
	
}
