package ch.fhnw.ether.scene.mesh.material;

import ch.fhnw.ether.render.shader.IShader;

public class CustomMaterial extends AbstractMaterial {
	private final IShader shader;

	public CustomMaterial(IShader shader) {
		this.shader = shader;
	}
	
	public IShader getShader() {
		return shader;
	}
	
	@Override
	public void getAttributeSuppliers(ISuppliers suppliers) {
	}
}
