package ch.fhnw.ether.render;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import ch.fhnw.ether.render.shader.builtin.ShadowVolumeShader;
import ch.fhnw.ether.render.shader.builtin.TrivialDeviceSpaceShader;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Flags;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.EmptyMaterial;
import ch.fhnw.util.color.RGBA;

public final class ShadowVolumes {
	private static final IMesh OVERLAY_MESH = new DefaultMesh(new EmptyMaterial(), DefaultGeometry.createV(Primitive.TRIANGLES,
			MeshLibrary.DEFAULT_QUAD_VERTICES));

	private ShadowVolumeShader volumeShader;
	private Renderable overlay;

	private int lightIndex;
	private float extrudeDistance = 1000;
	private RGBA volumeColor = new RGBA(1, 0, 0, 0.2f);
	private RGBA overlayColor = new RGBA(0, 0, 0, 0.9f);

	public ShadowVolumes(List<IAttributeProvider> providers) {
		volumeShader = ShaderBuilder.create(new ShadowVolumeShader(() -> lightIndex, () -> extrudeDistance, () -> volumeColor), null, providers);

		overlay = new Renderable(new TrivialDeviceSpaceShader(() -> overlayColor), OVERLAY_MESH, providers);
	}

	// http://ogldev.atspace.co.uk/www/tutorial40/tutorial40.html
	void render(GL3 gl, IMesh.Queue pass, boolean interactive, List<Renderable> renderables, List<GenericLight> lights) {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_ALPHA);
		gl.glDepthMask(false);
		gl.glEnable(GL3.GL_DEPTH_CLAMP);

		lightIndex = 0;
		for (@SuppressWarnings("unused") GenericLight light : lights) {
			gl.glClear(GL.GL_STENCIL_BUFFER_BIT);

			gl.glColorMask(false, false, false, false);

			gl.glEnable(GL.GL_STENCIL_TEST);

			gl.glStencilFuncSeparate(GL.GL_FRONT, GL.GL_ALWAYS, 0, 0xffffffff);
			gl.glStencilOpSeparate(GL.GL_FRONT, GL.GL_KEEP, GL.GL_DECR_WRAP, GL.GL_KEEP);

			gl.glStencilFuncSeparate(GL.GL_BACK, GL.GL_ALWAYS, 0, 0xffffffff);
			gl.glStencilOpSeparate(GL.GL_BACK, GL.GL_KEEP, GL.GL_INCR_WRAP, GL.GL_KEEP);

			volumeShader.update(gl);
			volumeShader.enable(gl);
			for (Renderable renderable : renderables) {
				if (renderable.containsFlag(Flags.INTERACTIVE_VIEWS_ONLY) && !interactive)
					continue;
				if (renderable.containsFlag(Flags.DONT_CAST_SHADOW))
					continue;
				if (renderable.getQueue() != pass)
					continue;

				volumeShader.render(gl, renderable.getBuffer());
			}
			volumeShader.disable(gl);

			gl.glColorMask(true, true, true, true);

			gl.glStencilFunc(GL.GL_NOTEQUAL, 0x0, 0xffffffff);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

			overlay.update(gl);
			overlay.render(gl);

			gl.glDisable(GL.GL_STENCIL_TEST);

			lightIndex++;
		}
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL.GL_BLEND);
		gl.glDepthMask(true);
		gl.glDisable(GL3.GL_DEPTH_CLAMP);
	}
}
