package ch.fhnw.ether.view;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.reorg.api.ICamera;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public class Camera implements ICamera {
	private static final boolean KEEP_ROT_X_POSITIVE = true;
	private static final float MIN_ZOOM = 0.02f;

	protected float fov;
	protected float aspect;
	protected float near;
	protected float far;

	private Mat4 projectionMatrix = Mat4.identityMatrix();
	private Mat4 cameraMatrix = Mat4.identityMatrix();

	private float orbitRadius = 3;
	private float azimut = 0;
	private float elevation = 0;

	private BoundingBox camera_box = new BoundingBox();

	public Camera() {
		this(45, 1, 0.01f, 100000);
	}

	public Camera(float fov, float aspect, float near, float far) {
		this.fov = fov;
		this.aspect = aspect;
		this.near = near;
		this.far = far;
		move(0, -orbitRadius, 0, true);
	}

	@Override
	public float getFov() {
		return fov;
	}

	@Override
	public void setFov(float fov) {
		this.fov = fov;
	}

	@Override
	public float getAspect() {
		return aspect;
	}

	@Override
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
	public void setProjectionMatrix(Mat4 projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
		// these values are now dirty
		fov = aspect = near = far = -1;
	}

	@Override
	public Mat4 getViewMatrix() {
		Mat4 viewMatrix = cameraMatrix.inverse();
		// Align to coordinate space with Z=up ad Y=depth
		viewMatrix.rotate(-90, Vec3.X);
		return viewMatrix;
	}

	@Override
	public void setViewMatrix(Mat4 viewMatrix) {
		cameraMatrix = viewMatrix.inverse();
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
	public BoundingBox getBoundings() {
		camera_box.reset();
		camera_box.add(getPosition());
		return camera_box;
	}

	@Override
	public void move(float x, float y, float z, boolean localTransformation) {
		Mat4 move = Mat4.identityMatrix();
		move.translate(x, y, z);
		if (localTransformation) {
			cameraMatrix = Mat4.product(cameraMatrix, move);
		} else {
			cameraMatrix = Mat4.product(move, cameraMatrix);
		}
	}

	@Override
	public void turn(float amount, Vec3 axis, boolean localTransformation) {
		Mat4 turn = Mat4.identityMatrix();
		turn.rotate(amount, axis);
		if (localTransformation) {
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
		cameraMatrix.translate(x, y, z);
	}

	@Override
	public float[] getPosition() {
		return new float[] { cameraMatrix.m[Mat4.M03],
				cameraMatrix.m[Mat4.M13], cameraMatrix.m[Mat4.M23] };
	}

	@Override
	public void setPosition(float[] position) {
		setPosition(position[0], position[1], position[2]);
	}

	@Override
	public void setPosition(float x, float y, float z) {
		cameraMatrix.m[Mat4.M03] = x;
		cameraMatrix.m[Mat4.M13] = y;
		cameraMatrix.m[Mat4.M23] = z;
	}

	@Override
	public Vec3 getLookDirection() {
		return new Vec3(cameraMatrix.m[Mat4.M10], cameraMatrix.m[Mat4.M11],
				cameraMatrix.m[Mat4.M12]).normalize();
	}

	// orbit camera
	// methods--------------------------------------------------------------
	// a call of one of this methods will change the camera mode to orbit mode

	@Override
	public void ORBITzoom(float zoomFactor) {
		float old_radius = orbitRadius;
		orbitRadius *= zoomFactor;
		if (orbitRadius < MIN_ZOOM) {
			orbitRadius = old_radius;
			return;
		}
		move(0, old_radius - orbitRadius, 0, true);
	}

	@Override
	public void ORBITturnAzimut(float amount) {
		move(0, orbitRadius, 0, true);
		turn(amount, Vec3.Z, false);
		move(0, -orbitRadius, 0, true);
		azimut += amount;
	}

	@Override
	public void ORBITturnElevation(float amount) {
		if (KEEP_ROT_X_POSITIVE) {
			if (elevation - amount < 0)
				amount = elevation;
			if (elevation - amount > 90)
				amount = 90 - elevation;
		}
		move(0, orbitRadius, 0, true);
		turn(amount, Vec3.X, true);
		move(0, -orbitRadius, 0, true);
		this.elevation -= amount;
	}

	@Override
	public void ORBITsetZoom(float zoom) {
		if (zoom < MIN_ZOOM)
			zoom = MIN_ZOOM;
		move(0, orbitRadius - zoom, 0, true);
		orbitRadius = zoom;
	}

	@Override
	public void ORBITsetAzimut(float azimut) {
		float diff = azimut - this.azimut;
		move(0, orbitRadius, 0, true);
		turn(diff, Vec3.Z, false);
		move(0, -orbitRadius, 0, true);
		this.azimut = azimut;
	}

	@Override
	public void ORBITsetElevation(float elevation) {
		if (KEEP_ROT_X_POSITIVE) {
			if (elevation < 0)
				elevation = 0;
			if (elevation > 90)
				elevation = 90;
		}
		float diff = this.elevation - elevation;
		move(0, orbitRadius, 0, true);
		turn(diff, Vec3.X, true);
		move(0, -orbitRadius, 0, true);
		this.elevation = elevation;
	}

	@Override
	public void ORBITmovePivot(float x, float y, float z,
			boolean localTransformation) {
		float newX = x, newY = y;
		if(localTransformation) {
			float azimut_rad = (float) Math.toRadians(-azimut);
			newX = (float) (Math.cos(azimut_rad)*x + Math.sin(azimut_rad)*y);
			newY = (float) (-Math.sin(azimut_rad)*x + Math.cos(azimut_rad)*y);
		}
		cameraMatrix.translate(newX, newY, 0);
	}

	@Override
	public float ORBIgetZoom() {
		return orbitRadius;
	}

}
