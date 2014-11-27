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

import javax.media.opengl.GL3;

import ch.fhnw.ether.scene.attribute.AbstractAttribute;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.view.IView;

/**
 * Simple rendering interface.
 *
 * @author radar
 */
public interface IRenderer {
	final class RendererAttribute<T> extends AbstractAttribute<T> {
		public RendererAttribute(String id) {
			super(id);
		}
	}

	/**
	 * Add mesh to renderer. Allocates all renderer-dependent resources. Thread-safe.
	 * 
	 * @param mesh
	 *            mesh to be added
	 * @throws IllegalArgumentException
	 *             if mesh already in renderer.
	 */
	void addMesh(IMesh mesh);

	// TODO: we could use a special flag (or similar) to prevent deallocation of resources for cases where meshes are
	// added and removed quickly.
	/**
	 * Remove mesh from renderer. Releases all renderer-dependent resources. Thread-safe.
	 * 
	 * @param mesh
	 *            mesh to be removed
	 * @throws IllegalArgumentException
	 *             if mesh not in renderer.
	 */
	void removeMesh(IMesh mesh);

	void addLight(ILight light);

	void removeLight(ILight light);

	/**
	 * Called view from render thread to render the meshes. Do not call this method otherwise.
	 */
	void render(GL3 gl, IView view);
}
