package ch.fhnw.ether.render.gl;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.gl.GLObject.Type;

public class RenderBuffer {
	private GLObject rbo;

	public RenderBuffer() {
	}

	public void setup(GL3 gl, int format, int width, int height) {
		if (rbo == null) {
			rbo = new GLObject(gl, Type.RENDERBUFFER);
		}
		gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, rbo.id());
		gl.glRenderbufferStorage(GL3.GL_RENDERBUFFER, format, width, height);
		unbind(gl);
	}
	
	int id() {
		return rbo.id();
	}

	public static void unbind(GL3 gl) {
		gl.glBindFramebuffer(GL3.GL_RENDERBUFFER, 0);
	}
}
