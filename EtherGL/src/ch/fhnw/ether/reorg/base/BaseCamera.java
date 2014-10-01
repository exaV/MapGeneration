package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.reorg.api.ICamera;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public class BaseCamera implements ICamera{
	
	private Mat4 projectionMatrix = Mat4.identityMatrix();
	private Mat4 viewMatrix = Mat4.identityMatrix();
	protected float fov;
	protected float aspect;
	protected float near;
	protected float far;
	private BoundingBox camera_box = new BoundingBox();
	
	public BaseCamera() {
		this(45, 1, 0.0001f, 10000000);
	}
	
	public BaseCamera(float fov, float aspect, float near, float far) {
		this.fov = fov;
		this.aspect = aspect;
		this.near = near;
		this.far = far;
	}
	
	@Override
	public float[] getPosition() {
		return new float[]{viewMatrix.m[Mat4.M30], viewMatrix.m[Mat4.M31], viewMatrix.m[Mat4.M32]};
	}

	@Override
	public void setPosition(float[] position) {
		setPosition(position[0], position[1], position[2]);
	}
	
	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		this.fov = fov;
	}

	public float getAspect() {
		return aspect;
	}

	public void setAspect(float aspect) {
		this.aspect = aspect;
	}

	@Override
	public float getNear() {
		return near;
	}

	@Override
	public void setNear(float near) {
		this.near = near;
	}

	@Override
	public float getFar() {
		return far;
	}

	@Override
	public void setFar(float far) {
		this.far = far;
	}

	@Override
	public Mat4 getProjectionMatrix() {
		projectionMatrix.perspective(fov, aspect, near, far);
		return projectionMatrix;
	}

	@Override
	public Mat4 getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public BoundingBox getBoundings() {
		return camera_box;
	}

	@Override
	public Mat4 getViewProjMatrix() {
		return Mat4.product(viewMatrix, getProjectionMatrix());
	}

	@Override
	public Mat4 getViewProjInvMatrix() {
		return getViewProjMatrix().inverse();
	}

	@Override
	public void move(float x, float y, float z) {
		Mat4 move = Mat4.identityMatrix();
		move.translate(x,y,z);
		viewMatrix = Mat4.product(viewMatrix, move); 
	}

	@Override
	public void turn(float xAxis, float yAxis, float zAxis) {
		Mat4 turn = Mat4.identityMatrix();
		turn.rotate(xAxis, Vec3.X);
		turn.rotate(yAxis, Vec3.Y);
		turn.rotate(zAxis, Vec3.Z);
		viewMatrix = Mat4.product(viewMatrix, turn); 
	}

	@Override
	public void setRotation(float xAxis, float yAxis, float zAxis) {
		float x = viewMatrix.m[Mat4.M30];
		float y = viewMatrix.m[Mat4.M31];
		float z = viewMatrix.m[Mat4.M32]; 
		viewMatrix = Mat4.identityMatrix();
		viewMatrix.rotate(xAxis, Vec3.X);
		viewMatrix.rotate(yAxis, Vec3.Y);
		viewMatrix.rotate(zAxis, Vec3.Z);
		viewMatrix.translate(x,y,z);
	}

	@Override
	public void setPosition(float x, float y, float z) {
		viewMatrix.m[Mat4.M30] = x;
		viewMatrix.m[Mat4.M31] = y;
		viewMatrix.m[Mat4.M32] = z;
	}

	//TODO: after these operations, some values will be dirty. Implement consideration for this.
	@Override
	public void setViewMatrix(Mat4 viewMatrix) {
		this.viewMatrix = viewMatrix;
	}

	@Override
	public void setProjectionMatrix(Mat4 projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}

}
