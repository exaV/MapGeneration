package ch.fhnw.ether.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader.ShaderInput;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;

/**
 * @author Samuel Stachelski
 * 
 * A very simple implementation of IScene.</br>
 * - Only triangle geometry will work</br>
 * - One renderable per mesh
 *
 */
public class SimpleScene implements IScene{
	
	private final List<IMesh> meshes = Collections.synchronizedList(new ArrayList<>(10));
	private final List<ILight> lights = Collections.synchronizedList(new ArrayList<>(3));
	private final List<ICamera> cameras = Collections.synchronizedList(new ArrayList<>(3));
	private final Map<IMesh, IRenderable> render_cache = new HashMap<>();
	private IRenderer renderer = null;
	private final IShader shader = new MaterialShader(EnumSet.of(ShaderInput.MATERIAL_COLOR));

	public SimpleScene() {

	}
	
	@Override
	public List<IMesh> getObjects() {
		return Collections.unmodifiableList(meshes);
	}
	
	public boolean addMesh(IMesh mesh) {
		if(renderer != null) {
			IRenderable add = renderer.createRenderable(Pass.DEPTH, shader, mesh.getMaterial(), mesh.getGeometry());
			render_cache.put(mesh, add);
			renderer.addRenderables(add);
		}
		
		return meshes.add(mesh);
	}

	public boolean removeMesh(IMesh mesh) {
		IRenderable remove = render_cache.get(mesh);
		if(renderer != null) renderer.removeRenderables(remove);
		render_cache.remove(mesh);
		return meshes.remove(mesh);
	}
	
	@Override
	public List<IMesh> getMeshes() {
		return Collections.unmodifiableList(meshes);
	}

	public boolean addLight(ILight light) {
		return lights.add(light);
	}

	public boolean removeLight(ILight light) {
		return lights.remove(light);
	}
	
	@Override
	public List<ILight> getLights() {
		return Collections.unmodifiableList(lights);
	}
	
	public boolean addCamera(ICamera camera) {
		return cameras.add(camera);
	}
	
	public boolean removeCamera(ICamera camera) {
		return cameras.remove(camera);
	}

	@Override
	public List<? extends ICamera> getCameras() {
		return Collections.unmodifiableList(cameras);
	}
	
	@Override
	public void setRenderer(IRenderer renderer) {
		if(this.renderer == renderer) return;
		this.renderer = renderer;
		render_cache.clear();
		
		IRenderable[] renderables = new IRenderable[meshes.size()];
		for(int i=0; i<meshes.size(); ++i) {
			IMesh m = meshes.get(i);
			renderables[i] = renderer.createRenderable(Pass.DEPTH, shader, m.getMaterial(), m.getGeometry());
			render_cache.put(m, renderables[i]);
		}
		
		renderer.addRenderables(renderables);
	}

	@Override
	public void renderUpdate() {
		for(IMesh m : meshes) {
			if(m.hasChanged()) {
				render_cache.get(m).requestUpdate();
			}
		}
	}

	
}
