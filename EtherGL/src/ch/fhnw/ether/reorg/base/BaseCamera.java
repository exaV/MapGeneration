package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.reorg.api.ICamera;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public class BaseCamera implements ICamera{
	
	private Mat4 projectionMatrix = Mat4.identityMatrix();
	private Mat4 cameraMatrix = Mat4.identityMatrix();
	protected float fov;
	protected float aspect;
	protected float near;
	protected float far;
	private BoundingBox camera_box = new BoundingBox();
	
	public BaseCamera() {
		this(45, 1, 0.1f, 1000000000);
	}
	
	public BaseCamera(float fov, float aspect, float near, float far) {
		this.fov = fov;
		this.aspect = aspect;
		this.near = near;
		this.far = far;
	}
	
	@Override
	public float[] getPosition() {
		return new float[]{cameraMatrix.m[Mat4.M03], cameraMatrix.m[Mat4.M13], cameraMatrix.m[Mat4.M23]};
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
		return cameraMatrix.inverse();
	}

	@Override
	public BoundingBox getBoundings() {
		return camera_box;
	}

	@Override
	public Mat4 getViewProjMatrix() {
		return Mat4.product(getProjectionMatrix(), getViewMatrix());
	}

	@Override
	public Mat4 getViewProjInvMatrix() {
		return getViewProjMatrix().inverse();
	}

	@Override
	public void move(float x, float y, float z, boolean local_transformation) {
		Mat4 move = Mat4.identityMatrix();
		move.translate(x,y,z);
		if(local_transformation) {
			cameraMatrix = Mat4.product(cameraMatrix, move);
		} else {
			cameraMatrix = Mat4.product(move, cameraMatrix);
		}
	}

	@Override
	public void turn(float xAxis, float yAxis, float zAxis, boolean local_transformation) {
		Mat4 turn = Mat4.identityMatrix();
		turn.rotate(xAxis, Vec3.X);
		turn.rotate(yAxis, Vec3.Y);
		turn.rotate(zAxis, Vec3.Z);
		if(local_transformation) {
			cameraMatrix = Mat4.product(cameraMatrix, turn);
		} else {
			cameraMatrix = Mat4.product(turn, cameraMatrix);
		}
	}

	@Override
	public void setRotation(float xAxis, float yAxis, float zAxis) {
		float x = cameraMatrix.m[Mat4.M03];
		float y = cameraMatrix.m[Mat4.M13];
		float z = cameraMatrix.m[Mat4.M23]; 
		cameraMatrix = Mat4.identityMatrix();
		cameraMatrix.rotate(xAxis, Vec3.X);
		cameraMatrix.rotate(yAxis, Vec3.Y);
		cameraMatrix.rotate(zAxis, Vec3.Z);
		cameraMatrix.translate(x,y,z);
	}

	@Override
	public void setPosition(float x, float y, float z) {
		cameraMatrix.m[Mat4.M03] = x;
		cameraMatrix.m[Mat4.M13] = y;
		cameraMatrix.m[Mat4.M23] = z;
	}

	@Override
	public void setViewMatrix(Mat4 viewMatrix) {
		this.cameraMatrix = viewMatrix.inverse();
	}

	@Override
	public void setProjectionMatrix(Mat4 projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
		//these values are now dirty
		fov = aspect = near = far = -1;
	}

}
