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

import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLRunnable;

import ch.fhnw.ether.render.forward.ForwardRenderer;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.camera.ViewMatrices;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IView.ViewType;
import ch.fhnw.util.ViewPort;
import ch.fhnw.util.math.Mat4;

/**
 * Default render manager.
 *
 * @author radar
 */
public class DefaultRenderManager implements IRenderManager {
	private static final class ViewState {
		
	}
	
	private final IRenderer renderer = new ForwardRenderer();
	
	private final IRenderProgram program = new DefaultRenderProgram();
	
	private final Map<IView, ViewState> views = new HashMap<>();

	public DefaultRenderManager() {
	}
	
	@Override
	public void addView(IView view) {
		views.put(view, new ViewState());
	}
	
	@Override
	public void removeView(IView view) {
		views.remove(view);
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
	public void setCamera(IView view, ICamera camera) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void lockCamera(IView view, Mat4 viewMatrix, Mat4 projMatrix) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public Runnable getRenderRunnable() {
		return new RenderRunnable(views, renderer, program);
	}
	
	private static class RenderRunnable implements Runnable {
		final Map<IView, ViewState> views;
		final IRenderer renderer;
		final IRenderProgram program;
		
		RenderRunnable(Map<IView, ViewState> views, IRenderer renderer, IRenderProgram program) {
			this.views = views;
			this.renderer = renderer;
			this.program = program;
		}
		
		@Override
		public void run() {
			for (IView view : views.keySet()) {
				view.getWindow().display(new GLRunnable() {
					@Override
					public boolean run(GLAutoDrawable drawable) {
						render(drawable.getGL().getGL3(), view);
						return true;
					}
				});
			}
		}
		
		void render(GL3 gl, IView view) {
			try {
				// XXX: make sure we only render on render thread (e.g. jogl
				// will do repaints on other threads when resizing windows...)
				if (!view.getController().getScheduler().isRenderThread()) {
					return;
				}

				GL3 gl3 = gl.getGL3();
				// gl3 = new TraceGL3(gl3, System.out);
				// gl3 = new DebugGL3(gl3);

				gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

				gl3.glEnable(GL.GL_MULTISAMPLE);

				if (!view.isEnabled())
					return;

				// repaint UI surface to texture if necessary
				// FIXME: should this be done on model or render thread?
				UI ui = view.getController().getUI();
				if (ui != null)
					ui.update();

				// render everything
				ViewMatrices matrices = view.getViewMatrices();
				ViewPort viewPort = view.getViewPort();
				ViewType viewType = view.getConfig().getViewType();
				program.getViewInfo().update(gl, matrices, viewPort, viewType);
				program.getLightInfo().update(gl, matrices);
				program.getRenderables().update(gl);
				renderer.render(gl, program);

				int error = gl.glGetError();
				if (error != 0)
					System.err.println("renderer returned with exisiting GL error 0x" + Integer.toHexString(error));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
