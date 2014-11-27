package ch.fhnw.ether.render.variable;

import javax.media.opengl.GL3;

public interface IShaderVariable<T> {
	String id();
	
	void dispose(GL3 gl);
}
