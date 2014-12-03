package ch.fhnw.ether.scene.mesh;

import java.util.EnumSet;

import ch.fhnw.ether.scene.IStateProxy;
import ch.fhnw.ether.scene.SyncGroup;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class MeshProxy implements IMesh, IStateProxy {
	private final IMesh delegate;
	private final Long  groupId;
	private Vec3        position;
	private String      name;

	public MeshProxy(IMesh delegate, long groupId) {
		this.delegate = delegate;
		this.groupId  = Long.valueOf(groupId);
		SyncGroup.addToGroup(this.groupId, this);
	}

	@Override
	public void sync() {
		if(position != null) {
			delegate.setPosition(position);
			position = null;
		}
		if(name != null) {
			delegate.setName(name);
			name = null;
		}
	}

	@Override
	public BoundingBox getBounds() {
		return delegate.getBounds();
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
	public String getName() {
		return delegate.getName();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void requestUpdate(Object source) {
		delegate.requestUpdate(source);
	}

	@Override
	public Pass getPass() {
		return delegate.getPass();
	}

	@Override
	public EnumSet<Flags> getFlags() {
		return delegate.getFlags();
	}

	@Override
	public IMaterial getMaterial() {
		return delegate.getMaterial();
	}

	@Override
	public IGeometry getGeometry() {
		return delegate.getGeometry();
	}

	@Override
	public boolean needsUpdate() {
		return delegate.needsUpdate();
	}

	public IMesh getDelegate() {
		return delegate;
	}
	
	@Override
	protected void finalize() throws Throwable {
		SyncGroup.removeFromGroup(groupId, this);
		super.finalize();
	}
}
