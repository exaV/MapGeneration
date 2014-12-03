package ch.fhnw.ether.scene.camera;

import ch.fhnw.ether.scene.IStateProxy;
import ch.fhnw.ether.scene.SyncGroup;
import ch.fhnw.util.IUpdateListener;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class CameraProxy implements ICamera, IStateProxy {
	private final ICamera delegate;
	private final Long    groupId;
	
	private String name;
	private Vec3   position;
	private Vec3   target;
	private Vec3   up;
	private Float  near;
	private Float  far;
	private Float  fov;
	
	public CameraProxy(ICamera camera, long groupId) {
		this.delegate = camera;
		this.groupId  = Long.valueOf(groupId);
		SyncGroup.addToGroup(this.groupId, this);
	}

	@Override
	public void sync() {
		if(name != null) {
			delegate.setName(name);
			name = null;
		}
		if(position != null) {
			delegate.setPosition(position);
			position = null;
		}
		if(target != null) {
			delegate.setTarget(target);
			target = null;
		}
		if(up != null) {
			delegate.setUp(up);
			up = null;
		}
		if(near != null) {
			delegate.setNear(near.floatValue());
			near = null;
		}
		if(far != null) {
			delegate.setFar(far.floatValue());
			far = null;
		}
		if(fov != null) {
			delegate.setFov(fov.floatValue());
			fov = null;
		}
	}
	
	@Override
	public BoundingBox getBounds() {
		return delegate.getBounds();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addUpdateListener(IUpdateListener listener) {
		delegate.addUpdateListener(listener);
	}

	@Override
	public void removeUpdateListener(IUpdateListener listener) {
		delegate.removeUpdateListener(listener);
	}

	@Override
	public Vec3 getPosition() {
		return delegate.getPosition();
	}

	@Override
	public void setPosition(Vec3 position) {
		this.position = position;
	}

	@Override
	public Vec3 getTarget() {
		return delegate.getTarget();
	}

	@Override
	public void setTarget(Vec3 target) {
		this.target = target;
	}

	@Override
	public Vec3 getUp() {
		return delegate.getUp();
	}

	@Override
	public void setUp(Vec3 up) {
		this.up = up;
	}

	@Override
	public float getFov() {
		return delegate.getFov();
	}

	@Override
	public void setFov(float fov) {
		fov = Float.valueOf(fov);
	}

	@Override
	public float getNear() {
		return delegate.getNear();
	}

	@Override
	public void setNear(float near) {
		this.near = Float.valueOf(near);
	}

	@Override
	public float getFar() {
		return delegate.getFar();
	}

	@Override
	public void setFar(float far) {
		this.far = Float.valueOf(far);
	}

	public ICamera getDelegate() {
		return delegate;
	}
	
	@Override
	protected void finalize() throws Throwable {
		SyncGroup.removeFromGroup(groupId, this);
		super.finalize();
	}
}
