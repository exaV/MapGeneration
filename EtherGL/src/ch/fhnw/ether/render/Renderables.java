/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
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

package ch.fhnw.ether.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import ch.fhnw.ether.render.forward.ShadowVolumes;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Flags;
import ch.fhnw.util.UpdateRequest;

import com.jogamp.opengl.GL3;

public final class Renderables {
	private final UpdateRequest updater = new UpdateRequest();

	private final Map<IMesh, Renderable> sceneRenderables = new IdentityHashMap<>();
	private final List<Renderable> rendererRenderables = new ArrayList<>();

	public Renderables() {
	}

	public void addMesh(IMesh mesh, List<IAttributeProvider> providers) {
		Renderable renderable = new Renderable(mesh, providers);
		synchronized (sceneRenderables) {
			if (sceneRenderables.putIfAbsent(mesh, renderable) != null)
				throw new IllegalArgumentException("mesh already in renderer: " + mesh);
		}
		updater.requestUpdate();
	}

	public void removeMesh(IMesh mesh) {
		Renderable renderable;
		synchronized (sceneRenderables) {
			renderable = sceneRenderables.remove(mesh);
			if (renderable == null)
				throw new IllegalArgumentException("mesh not in renderer: " + mesh);
		}
		updater.requestUpdate();
	}

	public void update(GL3 gl) {
		if (updater.needsUpdate()) {
			// update added / removed renderables
			synchronized (sceneRenderables) {
				Collection<Renderable> r = sceneRenderables.values();
				rendererRenderables.removeAll(r);
				rendererRenderables.clear();
				rendererRenderables.addAll(r);
			}
		}
		rendererRenderables.forEach((r) -> r.update(gl));
	}

	public void renderObjects(GL3 gl, IMesh.Queue pass, boolean interactive) {
		for (Renderable renderable : rendererRenderables) {
			if (renderable.containsFlag(Flags.INTERACTIVE_VIEWS_ONLY) && !interactive)
				continue;
			if (renderable.getQueue() == pass) {
				renderable.render(gl);
			}
		}
	}

	public void renderShadowVolumes(GL3 gl, IMesh.Queue pass, boolean interactive, ShadowVolumes shadowVolumes, List<GenericLight> lights) {
		shadowVolumes.render(gl, pass, interactive, rendererRenderables, lights);
	}
}
