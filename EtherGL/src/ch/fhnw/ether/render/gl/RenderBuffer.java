package ch.fhnw.ether.render.gl;

import javax.media.opengl.GL3;

public class RenderBuffer {
	private int[] rbo;

	public RenderBuffer() {
	}

	public void dispose(GL3 gl) {
		if (rbo != null) {
			gl.glDeleteRenderbuffers(1, rbo, 0);
			rbo = null;
		}
	}

	public void setup(GL3 gl, int format, int width, int height) {
		if (rbo == null) {
			gl.glGenRenderbuffers(1, rbo, 0);
		}
		gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, rbo[0]);
		gl.glRenderbufferStorage(GL3.GL_RENDERBUFFER, format, width, height);
		unbind(gl);
	}
	
	int id() {
		return rbo[0];
	}

	public static void unbind(GL3 gl) {
		gl.glBindFramebuffer(GL3.GL_RENDERBUFFER, 0);
	}
}
