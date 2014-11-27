package ch.fhnw.ether.render;

import java.util.List;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.shader.builtin.ShadowVolumeShader;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;

public final class ShadowVolumes {
	private ShadowVolumeShader shader;

	public ShadowVolumes(List<IAttributeProvider> providers) {
		shader = new ShadowVolumeShader(null);
		ShaderBuilder.attachAttributes(shader, providers);
	}

	public void enable(GL3 gl) {
		shader.update(gl);
		shader.enable(gl);
		// shader.setLightIndex(lightIndex);
	}

	public void renderVolumes(GL3 gl, Renderable renderable) {
		// FIXME: this is quite hacky... need a cleaner solution to setup an array (offset/stride) on the fly
		shader.getArrays().get(0).setup(renderable.getStride(), 0);
		shader.render(gl, renderable.getCount(), renderable.getBuffer());
	}

	public void renderShadows(GL3 gl) {
	}

	public void disable(GL3 gl) {
		shader.disable(gl);
	}
}
