package ch.fhnw.ether.scene.mesh.material;

import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.builtin.TextureUniform;
import ch.fhnw.ether.render.gl.Texture;

public class TextureMaterial implements IMaterial {
	
	private final static TextureUniform TEXTRE_ATTRIBUTE = new TextureUniform();

	private Texture texture;
	
	public TextureMaterial(Texture texture) {
		this.texture = texture;
	}

	@Override
	public void getAttributeSuppliers(ISuppliers dst) {
		dst.add(TEXTRE_ATTRIBUTE.id(), () -> { return texture; });
	}

}
