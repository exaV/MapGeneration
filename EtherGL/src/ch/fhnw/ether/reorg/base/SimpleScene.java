package ch.fhnw.ether.reorg.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.Renderable;
import ch.fhnw.ether.render.attribute.IUniformAttributeProvider;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.reorg.api.ICamera;
import ch.fhnw.ether.reorg.api.IGeometry;
import ch.fhnw.ether.reorg.api.ILight;
import ch.fhnw.ether.reorg.api.IMaterial;
import ch.fhnw.ether.reorg.api.IMesh;
import ch.fhnw.ether.reorg.api.IScene;
import ch.fhnw.ether.view.Camera;

//only for testing purposes
public class SimpleScene implements IScene{
	
	private final List<IMesh> meshes = Collections.synchronizedList(new ArrayList<>(10));
	private final List<ILight> lights = Collections.synchronizedList(new ArrayList<>(3));
	private ICamera active_camera = null;

	
	IUniformAttributeProvider uniforms = new IUniformAttributeProvider() {
		@Override
		public void getAttributeSuppliers(ISuppliers dst) {
			
			Set<IMaterial> materials = new HashSet<>();
			
			//for(IMesh m : meshes) {
			for(int i=0; i<meshes.size(); ++i) {
				IMesh m = meshes.get(i);
				IMaterial material = m.getMaterial();
				if(!materials.contains(m)) {
					materials.add(material);
					m.getMaterial().getAttributeSuppliers(dst);
				}
			}
			
			active_camera.getAttributeSuppliers(dst);
		}
	};

	//TODO: only due to compatibility to Camera.java
	public SimpleScene(Camera c) {
		active_camera = new ICamera() {
			@Override
			public void getAttributeSuppliers(ISuppliers dst) {
				dst.add(ProjMatrixUniform.ID, () -> c.getProjMatrix());
				dst.add(ViewMatrixUniform.ID, () -> c.getViewMatrix());

			}
		};
	}
	
	
	@Override
	public boolean addMesh(IMesh mesh) {
		return meshes.add(mesh);
	}

	@Override
	public boolean removeMesh(IMesh mesh) {
		return meshes.remove(mesh);
	}

	@Override
	public List<IMesh> getMeshes() {
		return Collections.unmodifiableList(meshes);
	}

	@Override
	public boolean addLight(ILight light) {
		return lights.add(light);
	}

	@Override
	public boolean removeLight(ILight light) {
		return lights.remove(light);
	}

	@Override
	public List<ILight> getLights() {
		return Collections.unmodifiableList(lights);
	}
	
	@Override
	public void setActiveCamera(ICamera camera) {
		active_camera = camera;
	}
	
	public IRenderable[] getRenderables(IRenderer r, IShader s) {
		
		final List<IGeometry> geo = Collections.synchronizedList(meshes.stream().map((x) -> {return x.getGeometry();}).collect(Collectors.toList()));
		
		IRenderable ret = new Renderable(Pass.DEPTH, EnumSet.noneOf(IRenderer.Flag.class), s, uniforms, geo);
		
		return new IRenderable[]{ret};
	}

}
