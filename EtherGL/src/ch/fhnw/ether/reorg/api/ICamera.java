package ch.fhnw.ether.reorg.api;

import ch.fhnw.util.math.Mat4;

public interface ICamera extends I3DObject {

	Mat4 getViewMatrix();
	
	Mat4 getProjectionMatrix();
	
	void setViewMatrix(Mat4 viewMatrix);
	
	void setProjectionMatrix(Mat4 projectionMatrix);
	
	Mat4 getViewProjMatrix();
	
	Mat4 getViewProjInvMatrix();
	
	float getNear();
	
	void setNear(float near);
	
	float getFar();
	
	void setFar(float far);
	
	/**
	 * Turn the camera around the specified amounts on each axis. This is a camera-local transformation.
	 */
	void turn(float xAxis, float yAxis, float zAxis, boolean local_transformation);
	
	/**
	 * Move the camera along the specified amounts on each axis. This is a camera-local transformation.
	 */
	void move(float x, float y, float z, boolean local_transformation);
	
	/**
	 * Rotates the camera to the specific angles. The angles are assumed in world-space.
	 */
	void setRotation(float xAxis, float yAxis, float zAxis);
	
	/**
	 * Moves the camera to the specific position. The coordinates are assumed in world-space.
	 */
	void setPosition(float x, float y, float z);
	
		

}
