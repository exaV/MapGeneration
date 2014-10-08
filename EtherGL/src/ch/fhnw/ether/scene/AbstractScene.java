package ch.fhnw.ether.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.geometry.I3DObject;

public abstract class AbstractScene implements IScene {
	
	private final List<IMesh> meshes;
	private final ICamera camera;
	
	public AbstractScene(List<IMesh> meshes, ICamera camera) {
		this(camera);
		meshes.addAll(meshes);
	}

	public AbstractScene(ICamera camera) {
		this.camera = camera;
		meshes = new ArrayList<>(1);
	}

	@Override
	public List<? extends I3DObject> getObjects() {
		return meshes;
	}

	@Override
	public List<IMesh> getMeshes() {
		return meshes;
	}

	@Override
	public List<ICamera> getCameras() {
		return Collections.singletonList(camera);
	}

	@Override
	public List<ILight> getLights() {
		return Collections.emptyList();
	}
	
	@Override
	public void renderUpdate() {
		//no update
	}

}
