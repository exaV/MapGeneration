package ch.fhnw.ether.reorg.api;

import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public interface ICamera extends I3DObject {

	// projection settings

	Mat4 getProjectionMatrix();

	void setProjectionMatrix(Mat4 projectionMatrix);

	float getNear();

	void setNear(float near);

	float getFar();

	void setFar(float far);

	float getFov();

	void setFov(float fov);

	float getAspect();

	void setAspect(float aspect);

	// view matrix

	Mat4 getViewMatrix();

	void setViewMatrix(Mat4 viewMatrix);

	Mat4 getViewProjMatrix();

	Mat4 getViewProjInvMatrix();

	// camera matrix

	void turn(float amount, Vec3 axis, boolean localTransformation);

	void move(float x, float y, float z, boolean localTransformation);

	void setRotation(float xAxis, float yAxis, float zAxis);

	void setPosition(float x, float y, float z);

	Vec3 getLookDirection();

	// orbit-related changes, camera transformations around pivot point

	void ORBITzoom(float zoomFactor);

	void ORBITturnAzimut(float amount);

	void ORBITturnElevation(float amount);

	void ORBITsetZoom(float zoom);
	float ORBIgetZoom();

	void ORBITsetAzimut(float azimut);

	void ORBITsetElevation(float elevation);

	void ORBITmovePivot(float x, float y, float z, boolean localTransformation);

}
