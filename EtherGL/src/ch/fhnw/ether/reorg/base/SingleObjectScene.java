package ch.fhnw.ether.reorg.base;

import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.Triangles;
import ch.fhnw.ether.reorg.api.I3DObject;
import ch.fhnw.ether.reorg.api.IMesh;
import ch.fhnw.ether.reorg.api.IScene;
import ch.fhnw.util.color.RGBA;

//only for testing purposes
public class SingleObjectScene implements IScene{
	
	private IMesh object;
	private IRenderer r;
	
	public SingleObjectScene(IRenderer r) {
		this.r = r;
	}

	@Override
	public void add3DObject(I3DObject object) {
	}
	
	public void addMesh(IMesh mesh) {
		this.object = mesh;
	}

	@Override
	public void remove3DObject(I3DObject object) {
		this.object = null;
	}

	@Override
	public IRenderable[] getRenderables() {
		if(object != null) {
			IShader s = new Triangles(new RGBA(1.f,1.f,1.f, 1.f));
			return new IRenderable[]{r.createRenderable(Pass.DEPTH, s, object.getGeometry())};
		} else {
			return new IRenderable[0];
		}
	}
	
}
