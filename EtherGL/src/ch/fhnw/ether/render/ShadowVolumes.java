package ch.fhnw.ether.render;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.shader.IShader;

public final class ShadowVolumes {
	IShader shader;
	
	
	public void enable(GL3 gl) {
		//if (shader == null) {
		//	shader = new ShadowVolumeShader(null);
		//}
	}

	public void renderVolumes(GL3 gl, Renderable renderable) {
	}

	public void renderShadows(GL3 gl) {
	}

	public void disable(GL3 gl) {
	}
}
