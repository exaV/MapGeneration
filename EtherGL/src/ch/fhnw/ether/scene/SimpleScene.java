package ch.fhnw.ether.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.MaterialTriangles;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;

//only for testing purposes
public class SimpleScene implements IScene{
	
	private final List<IMesh> meshes = Collections.synchronizedList(new ArrayList<>(10));
	private final List<ILight> lights = Collections.synchronizedList(new ArrayList<>(3));

	public SimpleScene() {

	}
	
	public boolean addMesh(IMesh mesh) {
		return meshes.add(mesh);
	}

	public boolean removeMesh(IMesh mesh) {
		return meshes.remove(mesh);
	}

	@Override
	public List<IMesh> getObjects() {
		return Collections.unmodifiableList(meshes);
	}

	public boolean addLight(ILight light) {
		return lights.add(light);
	}

	public boolean removeLight(ILight light) {
		return lights.remove(light);
	}

	public List<ILight> getLights() {
		return Collections.unmodifiableList(lights);
	}

	@Override
	public List<IMesh> getMeshes() {
		return Collections.unmodifiableList(meshes);
	}
	
	@Override
	public IRenderable[] createRenderables(IRenderer renderer) {

		List<IRenderable> renderables;
		IShader shader = new MaterialTriangles(true, false, false, false);
		renderables = meshes.stream().map((x) -> renderer.createRenderable(Pass.DEPTH, shader, x.getMaterial(), x.getGeometry())).collect(Collectors.toList());
		
		return renderables.toArray(new IRenderable[0]);
	}

}
