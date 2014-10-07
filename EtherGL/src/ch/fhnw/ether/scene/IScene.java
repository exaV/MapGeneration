package ch.fhnw.ether.scene;

import java.util.List;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.geometry.I3DObject;

public interface IScene {

	List<? extends I3DObject> getObjects();
	
	List<? extends IMesh> getMeshes();
	
	List<? extends ICamera> getCameras();
	
	List<? extends ILight> getLights();

	IRenderable[] createRenderables(IRenderer renderer);
}
