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

package ch.fhnw.ether.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader.ShaderInput;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;

/**
 * A very simple implementation of IScene.</br> - Only triangle geometry will work</br> - One renderable per mesh - Only
 * material colors work
 *
 * @author Samuel Stachelski
 */
public class SimpleScene extends AbstractScene {

	private final List<ILight> lights = new ArrayList<>(3);
	private final Map<IMesh, IRenderable> renderCache = new HashMap<>();
	private IRenderer renderer = null;
	private final IShader shader = new MaterialShader(EnumSet.of(ShaderInput.MATERIAL_COLOR));

	public SimpleScene(ICamera camera) {
		super(camera);
	}

	@Override
	public List<IMesh> getMeshes() {
		return Collections.unmodifiableList(super.getMeshes());
	}

	public boolean addMesh(IMesh mesh) {
		if (renderer != null) {
			IRenderable add = renderer.createRenderable(Pass.DEPTH, shader, mesh.getMaterial(), Collections.singletonList(mesh.getGeometry()));
			renderCache.put(mesh, add);
			renderer.addRenderables(add);
		}

		return super.getMeshes().add(mesh);
	}

	public boolean removeMesh(IMesh mesh) {
		IRenderable remove = renderCache.get(mesh);
		if (renderer != null)
			renderer.removeRenderables(remove);
		renderCache.remove(mesh);
		return super.getMeshes().remove(mesh);
	}

	public boolean addLight(ILight light) {
		return lights.add(light);
	}

	public boolean removeLight(ILight light) {
		return lights.remove(light);
	}

	@Override
	public List<ILight> getLights() {
		return Collections.unmodifiableList(lights);
	}

	@Override
	public void setRenderer(IRenderer renderer) {
		if (this.renderer == renderer)
			return;
		this.renderer = renderer;
		renderCache.clear();

		List<IMesh> meshes = super.getMeshes();

		IRenderable[] renderables = new IRenderable[meshes.size()];
		for (int i = 0; i < meshes.size(); ++i) {
			IMesh m = meshes.get(i);
			renderables[i] = renderer.createRenderable(Pass.DEPTH, shader, m.getMaterial(), Collections.singletonList(m.getGeometry()));
			renderCache.put(m, renderables[i]);
		}

		renderer.addRenderables(renderables);
	}

	@Override
	public void renderUpdate() {
		for (IMesh m : super.getMeshes()) {
			if (m.hasChanged()) {
				renderCache.get(m).requestUpdate();
			}
		}
	}

}
