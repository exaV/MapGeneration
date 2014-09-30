package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.reorg.api.ICamera;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Vec4;

public class BaseCamera implements ICamera{
	
	private Mat4 projectionMatrix = Mat4.identityMatrix();
	private Mat4 viewMatrix = Mat4.identityMatrix();
	private float fov;
	private float aspect;
	private float near;
	private float far;
	private Vec3 position = Vec3.ZERO;
	private Vec3 direction = Vec3.Z;
	private BoundingBox camera_box = new BoundingBox();
	
	public BaseCamera() {
		this(45, 1, 0.0001f, 10000000);
	}
	
	public BaseCamera(float fov, float aspect, float near, float far) {
		this.fov = fov;
		this.aspect = aspect;
		this.near = near;
		this.far = far;
		updateProjectionMatrix();
	}

	@Override
	public void getAttributeSuppliers(ISuppliers dst) {
		dst.add(ProjMatrixUniform.ID, () -> { return projectionMatrix; });
		dst.add(ViewMatrixUniform.ID, () -> { return viewMatrix; });
	}
	
	@Override
	public void rotate(float angle, Vec3 axis) {
		viewMatrix.rotate(angle, axis);
		direction = viewMatrix.transform(Vec3.Z);
	}
	
	@Override
	public void translate(Vec3 vector) {
		position.add(vector);
		viewMatrix.translate(vector);
	}
	
	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vec3 position) {
		viewMatrix.translate(this.position.negate());
		viewMatrix.translate(position);
		this.position = position;
	}
	
	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		this.fov = fov;
		updateProjectionMatrix();
	}

	public float getAspect() {
		return aspect;
	}

	public void setAspect(float aspect) {
		this.aspect = aspect;
		updateProjectionMatrix();
	}

	public float getNear() {
		return near;
	}

	public void setNear(float near) {
		this.near = near;
		updateProjectionMatrix();
	}

	public float getFar() {
		return far;
	}

	public void setFar(float far) {
		this.far = far;
		updateProjectionMatrix();
	}

	@Override
	public void setProjectionMatrix(Mat4 projectionMat) {
		projectionMatrix = projectionMat;
	}

	@Override
	public Mat4 getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public void setViewMatrix(Mat4 viewMat) {
		viewMatrix = viewMat;
	}

	@Override
	public Mat4 getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public BoundingBox getBoundings() {
		return camera_box;
	}
	
	protected void updateProjectionMatrix() {
		projectionMatrix.perspective(fov, aspect, near, far);
	}

}
