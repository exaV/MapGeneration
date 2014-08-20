package ch.fhnw.ether.formats;

import java.util.List;

import ch.fhnw.ether.model.AbstractModel;
import ch.fhnw.ether.model.IGeometry;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;
import ch.fhnw.ether.render.shader.Lines;
import ch.fhnw.ether.render.shader.Triangles;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.util.CollectionUtil;

public class StaticModel extends AbstractModel {
	private IRenderable triangles;
	private IRenderable edges;

	private boolean solid;
	private boolean wireframe;

	public StaticModel(IScene scene, Iterable<IGeometry> geometries) {
		super(scene);
		for(IGeometry g : geometries)
			addGeometry(g);
		List<IArrayAttributeProvider> providers = CollectionUtil.filterType(IArrayAttributeProvider.class, getGeometries());
		triangles = scene.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Triangles(), providers);
		edges     = scene.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Lines(),     providers);
		setSolid(true);
		setWireframe(true);
	}
	
	protected void addRenderables(IScene scene) {
		if (triangles == null) {
			scene.getRenderer().addRenderables(triangles, edges);			
		}
	}
	

	public void setSolid(boolean solid) {
		if(this.solid != solid) {
			this.solid = solid;
			if(solid) scene.getRenderer().addRenderables(triangles);
			else      scene.getRenderer().removeRenderables(triangles);
		}
	}

	public void setWireframe(boolean wireframe) {
		if(this.wireframe != wireframe) {
			this.wireframe = wireframe;
			if(wireframe) scene.getRenderer().addRenderables(edges);
			else          scene.getRenderer().removeRenderables(edges);
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for(IGeometry g : getGeometries())
			result.append(g.toString()).append(' ');
		return result.toString();
	}
}
