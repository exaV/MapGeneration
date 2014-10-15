/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.ether.examples.raytracing;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.examples.raytracing.util.Ray;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.Renderable;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.IUniformAttributeProvider;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.TexCoordArray;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.render.gl.Texture;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader.ShaderInput;
import ch.fhnw.ether.scene.mesh.geometry.VertexGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.TextureMaterial;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.FloatList;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.Primitives;

public class RayTracingRenderer implements IRenderer {

	private final List<IRenderable> renderables = new ArrayList<>();
	private final ParametricScene scene;
	private final Texture screenTexture = new Texture();
	private final IRenderable plane = createScreenPlane(-1, -1, 2, 2, screenTexture);
	private int[] colors;
	private int w = 0, h = 0;
	private long n = 0;

	public RayTracingRenderer(ParametricScene scene) {
		this.scene = scene;
	}

	@Override
	public void render(GL3 gl, IView view) {
		long t = System.currentTimeMillis();
		Viewport viewport = view.getViewport();
		if (viewport.w != w || viewport.h != h) {
			w = viewport.w;
			h = viewport.h;
			colors = new int[w * h];
		}
		ICamera camera = view.getCamera();
		Vec3 camPos = camera.getPosition();

		float planeWidth = (float) (2 * Math.tan(camera.getFov() / 2) * camera.getNear());
		float planeHeight = planeWidth / viewport.getAspect();

		float deltaX = planeWidth / w;
		float deltaY = planeHeight / h;

		Vec3 lookVector = camera.getTarget().subtract(camera.getPosition()).normalize();
		Vec3 upVector = camera.getUp().normalize();
		Vec3 sideVector = lookVector.cross(upVector).normalize();

		for (int j = -h / 2; j < h / 2; ++j) {
			for (int i = -w / 2; i < w / 2; ++i) {
				Vec3 x = sideVector.scale(i * deltaX);
				Vec3 y = upVector.scale(j * deltaY);
				Vec3 dir = lookVector.add(x).add(y);
				Ray ray = new Ray(camPos, dir);
				RGBA color = scene.intersection(ray);
				colors[(j + h / 2) * w + (i + w / 2)] = color.toInt();
			}
		}

		screenTexture.setData(viewport.w, viewport.h, IntBuffer.wrap(colors), GL.GL_RGBA);
		plane.requestUpdate();
		plane.update(gl, new FloatList());
		plane.render(gl, null);
		System.out.println((System.currentTimeMillis() - t) + "ms for " + ++n + "th frame");
	}

	@Override
	public IRenderable createRenderable(Pass pass, IShader shader, IUniformAttributeProvider uniforms, List<? extends IArrayAttributeProvider> providers) {
		return null;
	}

	@Override
	public IRenderable createRenderable(Pass pass, EnumSet<Flag> flags, IShader shader, IUniformAttributeProvider uniforms,
			List<? extends IArrayAttributeProvider> providers) {
		return null;
	}

	@Override
	public void addRenderables(IRenderable... renderables) {
		this.renderables.addAll(Arrays.asList(renderables));
	}

	@Override
	public void removeRenderables(IRenderable... renderables) {
		this.renderables.removeAll(Arrays.asList(renderables));
	}

	private static IRenderable createScreenPlane(float x, float y, float w, float h, Texture texture) {
		IArrayAttribute[] attribs = new IArrayAttribute[] { new PositionArray(), new TexCoordArray() };
		float[] position = new float[] { x, y, 0, x + w, y, 0, x + w, y + h, 0, x, y, 0, x + w, y + h, 0, x, y + h, 0 };
		float[][] data = new float[][] { position, Primitives.DEFAULT_QUAD_TEX_COORDS };
		List<IArrayAttributeProvider> quad = Collections.singletonList(new VertexGeometry(data, attribs, PrimitiveType.TRIANGLE));
		IMaterial mat = new TextureMaterial(texture);
		IUniformAttributeProvider uniforms = new IUniformAttributeProvider() {
			@Override
			public void getAttributeSuppliers(ISuppliers dst) {
				dst.add(ProjMatrixUniform.ID, () -> Mat4.identityMatrix());
				dst.add(ViewMatrixUniform.ID, () -> Mat4.identityMatrix());
				mat.getAttributeSuppliers(dst);
			}
		};
		return new Renderable(null, null, new MaterialShader(EnumSet.of(ShaderInput.TEXTURE)), uniforms, quad);
	}

}
