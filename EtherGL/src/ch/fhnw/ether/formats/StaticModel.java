package ch.fhnw.ether.formats;

import java.util.List;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;
import ch.fhnw.ether.render.shader.builtin.Lines;
import ch.fhnw.ether.render.shader.builtin.Triangles;
import ch.fhnw.ether.scene.AbstractModel;
import ch.fhnw.ether.scene.IGeometry;
import ch.fhnw.util.CollectionUtil;

public class StaticModel extends AbstractModel {
	private IRenderable triangles;
	private IRenderable edges;

	private boolean solid;
	private boolean wireframe;

	public StaticModel(IController controller, Iterable<IGeometry> geometries) {
		super(controller);
		for (IGeometry g : geometries)
			addGeometry(g);
		List<IArrayAttributeProvider> providers = CollectionUtil.filterType(IArrayAttributeProvider.class, getGeometries());
		triangles = controller.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Triangles(), providers);
		edges = controller.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Lines(), providers);
		setSolid(true);
		setWireframe(true);
	}

	protected void addRenderables(IController controller) {
		if (triangles == null) {
			controller.getRenderer().addRenderables(triangles, edges);
		}
	}

	public void setSolid(boolean solid) {
		if (this.solid != solid) {
			this.solid = solid;
			if (solid)
				controller.getRenderer().addRenderables(triangles);
			else
				controller.getRenderer().removeRenderables(triangles);
		}
	}

	public void setWireframe(boolean wireframe) {
		if (this.wireframe != wireframe) {
			this.wireframe = wireframe;
			if (wireframe)
				controller.getRenderer().addRenderables(edges);
			else
				controller.getRenderer().removeRenderables(edges);
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (IGeometry g : getGeometries())
			result.append(g.toString()).append(' ');
		return result.toString();
	}
}
