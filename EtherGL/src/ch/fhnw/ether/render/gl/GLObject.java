package ch.fhnw.ether.render.gl;

import java.lang.ref.ReferenceQueue;

import javax.media.opengl.GL3;

import ch.fhnw.ether.view.gl.GLContextManager;
import ch.fhnw.ether.view.gl.GLContextManager.IGLContext;
import ch.fhnw.util.AutoDisposer;
import ch.fhnw.util.AutoDisposer.Reference;

public class GLObject {
	public enum Type {
		TEXTURE, BUFFER, RENDERBUFFER, FRAMEBUFFER, PROGRAM
	}

	public static class GLObjectRef extends Reference<GLObject> {
		private final Type  type;
		private final int[] id;

		public GLObjectRef(GLObject referent, ReferenceQueue<? super GLObject> q) {
			super(referent, q);
			type = referent.getType();
			id = referent.id;
		}

		@Override
		public void dispose() {
			System.out.println("disposing " + type + " " + id[0]);
			IGLContext context = GLContextManager.acquireContext();
			GL3 gl = context.getGL();
			switch (type) {
			case TEXTURE:
				gl.glDeleteTextures(1, id, 0);
				break;
			case BUFFER:
				gl.glDeleteBuffers(1, id, 0);
				break;
			case RENDERBUFFER:
				gl.glDeleteRenderbuffers(1, id, 0);
				break;
			case FRAMEBUFFER:
				gl.glDeleteFramebuffers(1, id, 0);
				break;
			case PROGRAM:
				gl.glDeleteProgram(id[0]);
				break;
			}
			GLContextManager.releaseContext(context);
		}
	}

	private static final AutoDisposer<GLObject> autoDisposer = new AutoDisposer<>(GLObjectRef.class);

	private final Type  type;
	private final int[] id = new int[1];

	public GLObject(GL3 gl, Type type) {
		this.type = type;
		switch (type) {
		case TEXTURE:
			gl.glGenTextures(1, id, 0);
			break;
		case BUFFER:
			gl.glGenBuffers(1, id, 0);
			break;
		case RENDERBUFFER:
			gl.glGenRenderbuffers(1, id, 0);
			break;
		case FRAMEBUFFER:
			gl.glGenFramebuffers(1, id, 0);
			break;
		case PROGRAM:
			id[0] = gl.glCreateProgram();
			break;
		}
		autoDisposer.add(this);
	}

	public Type getType() {
		return type;
	}

	public int id() {
		return id[0];
	}

	@Override
	public String toString() {
		return type + ":" + id();
	}
}
