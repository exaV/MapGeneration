package ch.fhnw.ether.reorg.api;

import ch.fhnw.ether.render.IRenderable;

public interface IScene {
	
	void add3DObject(I3DObject object);
	
	void remove3DObject(I3DObject object);
	
	IRenderable[] getRenderables();
}
