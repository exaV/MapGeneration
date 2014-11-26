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

package ch.fhnw.ether.render;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;

import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.camera.CameraMatrices;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Pass;

public abstract class AbstractRenderer implements IRenderer {
	private final List<IAttributeProvider> providers = new ArrayList<>();

	private final Lights lights = new Lights(this);
	private final Renderables renderables = new Renderables();
	
	private final ShadowVolumes shadowVolumes = new ShadowVolumes(providers);

	public AbstractRenderer() {
		providers.add(lights.getAttributeProvider());
	}
	
	protected void addAttributeProvider(IAttributeProvider provider) {
		providers.add(provider);		
	}

	@Override
	public void addMesh(IMesh mesh) {
		renderables.addMesh(mesh, providers);
	}

	@Override
	public void removeMesh(IMesh mesh) {
		renderables.removeMesh(mesh);
	}

	@Override
	public void addLight(ILight light) {
		lights.addLight(light);
	}

	@Override
	public void removeLight(ILight light) {
		lights.removeLight(light);
	}

	protected void update(GL3 gl, CameraMatrices cameraMatrices) {
		lights.update(gl, cameraMatrices);
		renderables.update(gl);
	}

	protected void renderObjects(GL3 gl, Pass pass, boolean interactive) {
		renderables.renderObjects(gl, pass, interactive);
	}

	protected void renderShadowVolumes(GL3 gl, Pass pass, boolean interactive) {
		renderables.renderShadowVolumes(gl, pass, interactive, shadowVolumes);
	}
}
