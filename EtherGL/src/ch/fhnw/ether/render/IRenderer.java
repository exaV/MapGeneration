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

import java.util.EnumSet;
import java.util.List;

import javax.media.opengl.GL3;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;
import ch.fhnw.ether.render.attribute.IUniformAttributeProvider;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.math.Mat4;

/**
 * Simple rendering interface.
 *
 * @author radar
 */
public interface IRenderer {
	public static class RenderState {
		public Mat4 projMatrix;
		public Mat4 viewMatrix;

		public void setMatrices(Mat4 projMatrix, Mat4 viewMatrix) {
			this.projMatrix = projMatrix;
			this.viewMatrix = viewMatrix;
		}
	}

	public enum Pass {
		DEPTH, TRANSPARENCY, OVERLAY, DEVICE_SPACE_OVERLAY, SCREEN_SPACE_OVERLAY
	}

	enum Flag {
		INTERACTIVE_VIEW_ONLY
	}
	
	public final static EnumSet<Flag> NO_FLAGS = EnumSet.noneOf(Flag.class);

	void render(GL3 gl, ICamera camera, Viewport viewport, boolean interactive);

	IRenderable createRenderable(Pass pass, IShader shader,IUniformAttributeProvider uniforms, List<? extends IArrayAttributeProvider> providers);

	IRenderable createRenderable(Pass pass, EnumSet<Flag> flags, IShader shader, IUniformAttributeProvider uniforms, List<? extends IArrayAttributeProvider> providers);

	void addRenderables(IRenderable... renderables);

	void removeRenderables(IRenderable... renderables);

}
