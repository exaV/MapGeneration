package ch.fhnw.ether.render.gl;

import javax.media.opengl.GL;

public final class GLUtilities {
	public static int getInteger(GL gl, int parameter) {
		int[] v = new int[1];
		gl.glGetIntegerv(parameter, v, 0);
		return v[0];
	}

	public static float getFloat(GL gl, int parameter) {
		float[] v = new float[1];
		gl.glGetFloatv(parameter, v, 0);
		return v[0];
	}
}
