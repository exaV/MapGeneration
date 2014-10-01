package ch.fhnw.ether.reorg.api;

import java.util.List;

public interface IScene {
	
	boolean addMesh(IMesh mesh);
	
	boolean removeMesh(IMesh mesh);
	
	List<IMesh> getMeshes();
	
	boolean addLight(ILight light);
	
	boolean removeLight(ILight light);
	
	List<ILight> getLights();
	
	void setActiveCamera(ICamera camera);
}
