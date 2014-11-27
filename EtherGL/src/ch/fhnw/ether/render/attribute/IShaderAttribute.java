package ch.fhnw.ether.render.attribute;

import javax.media.opengl.GL3;

import ch.fhnw.ether.scene.attribute.ITypedAttribute;

public interface IShaderAttribute<T> extends ITypedAttribute<T> {
	void dispose(GL3 gl);
}
