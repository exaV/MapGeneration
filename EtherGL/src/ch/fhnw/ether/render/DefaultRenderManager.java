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

import com.jogamp.opengl.GL3;

import ch.fhnw.ether.scene.camera.ViewMatrices;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.view.IView.ViewType;
import ch.fhnw.util.ViewPort;

/**
 * Default render manager.
 *
 * @author radar
 */
public class DefaultRenderManager implements IRenderManager {
	private final IRenderProgram program = new DefaultRenderProgram();

	public DefaultRenderManager() {
	}
	
	@Override
	public void addMesh(IMesh mesh) {
		program.getRenderables().addMesh(mesh, program.getProviders());
	}

	@Override
	public void removeMesh(IMesh mesh) {
		program.getRenderables().removeMesh(mesh);
	}

	@Override
	public void addLight(ILight light) {
		program.getLightInfo().addLight(light);
	}

	@Override
	public void removeLight(ILight light) {
		program.getLightInfo().removeLight(light);
	}
	
	@Override
	public IRenderProgram getProgram() {
		return program;
	}

	@Override
	public void update(GL3 gl, ViewMatrices matrices, ViewPort viewPort, ViewType viewType) {
		program.getViewInfo().update(gl, matrices, viewPort, viewType);
		program.getLightInfo().update(gl, matrices);
		program.getRenderables().update(gl);
	}
}
