package ch.fhnw.ether.reorg.api;

import java.util.List;

import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;

public interface IScene {

	List<? extends I3DObject> getObjects();
	
	List<? extends IMesh> getMeshes();

	IRenderable[] createRenderables(IRenderer renderer);
}
