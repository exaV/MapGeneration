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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLRunnable;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.render.forward.ForwardRenderer;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.camera.ViewCameraState;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IView.ViewType;
import ch.fhnw.util.math.Mat4;

/**
 * Default render manager.
 *
 * @author radar
 */
public class DefaultRenderManager implements IRenderManager {
	private static class CameraSceneState {
		final List<IView> views = new ArrayList<>();
		
		public CameraSceneState() {
		}
	}
	
	private static class ViewSceneState {
		ICamera camera = new Camera();
		ViewCameraState viewCameraState;
		
		ViewSceneState(IView view) {
			viewCameraState = new ViewCameraState(view, camera);
		}
	}

	private final IController controller;
	
	private final IRenderer renderer = new ForwardRenderer();
	
	private final IRenderProgram program = new DefaultRenderProgram();
	
	private final Map<ICamera, CameraSceneState> cameras = new IdentityHashMap<>();

	private final Map<IView, ViewSceneState> views = new IdentityHashMap<>();

	public DefaultRenderManager(IController controller) {
		this.controller = controller;
	}
	
	@Override
	public void addView(IView view) {
		ViewSceneState vcs = new ViewSceneState(view);
		views.put(view, vcs);
		setCamera(view, vcs.camera);
	}
	
	@Override
	public void removeView(IView view) {
		views.remove(view);
		setCamera(view, null);
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
	public ICamera getCamera(IView view) {
		return views.get(view).camera;
	}
	
	@Override
	public void setCamera(IView view, ICamera camera) {
		views.get(view).camera = camera;
		cameras.clear();
		for (Map.Entry<IView, ViewSceneState> e : views.entrySet()) {
			IView v = e.getKey();
			ICamera c = e.getValue().camera;
			CameraSceneState css = cameras.get(c);
			if (css == null) {
				css = new CameraSceneState();
				cameras.put(c, css);
			}
			css.views.add(v);
		}
	}
	
	@Override
	public void lockCamera(IView view, Mat4 viewMatrix, Mat4 projMatrix) {
		views.get(view).viewCameraState = new ViewCameraState(view, viewMatrix, projMatrix);
	}
	
	@Override
	public ViewCameraState getViewCameraState(IView view) {
		return views.get(view).viewCameraState;
	}
	
	@Override
	public Runnable getRenderRunnable() {
		// 1. set new view matrices for each updated camera
		for (Map.Entry<ICamera, CameraSceneState> e : cameras.entrySet()) {
			ICamera camera = e.getKey();
			if (camera.needsUpdate()) {
				for (IView view : e.getValue().views) {
					views.get(view).viewCameraState = new ViewCameraState(view, camera);
				}
			}
		}

		// 2. create runnable
		return new RenderRunnable(controller, views, renderer, program);
	}
	
	/**
	 * This runnable is created on scene thread and then run on render thread.
	 */
	private static class RenderRunnable implements Runnable {
		static class ViewRenderState {
			final IView view;
			final ViewCameraState viewCameraState;
			
			ViewRenderState(IView view, ViewCameraState vcs) {
				this.view = view;
				this.viewCameraState = vcs;
			}
		}
		
		final IController controller;
		final List<ViewRenderState> viewStates = new ArrayList<>();
		final IRenderer renderer;
		final IRenderProgram program;
		
		RenderRunnable(IController controller, Map<IView, ViewSceneState> views, IRenderer renderer, IRenderProgram program) {
			this.controller = controller;
			for (Map.Entry<IView, ViewSceneState> e : views.entrySet()) {
				IView view = e.getKey();
				ViewCameraState vcs = e.getValue().viewCameraState;
				viewStates.add(new ViewRenderState(view, vcs));
			}
			this.renderer = renderer;
			this.program = program;
		}
		
		@Override
		public void run() {
			for (ViewRenderState viewState : viewStates) {
				viewState.view.getWindow().display(new GLRunnable() {
					@Override
					public boolean run(GLAutoDrawable drawable) {
						render(drawable.getGL().getGL3(), viewState);
						return true;
					}
				});
			}
		}
		
		void render(GL3 gl, ViewRenderState viewState) {
			try {
				// XXX: make sure we only render on render thread (e.g. jogl
				// will do repaints on other threads when resizing windows...)
				if (!controller.getScheduler().isRenderThread()) {
					return;
				}

				GL3 gl3 = gl.getGL3();
				// gl3 = new TraceGL3(gl3, System.out);
				// gl3 = new DebugGL3(gl3);

				gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

				gl3.glEnable(GL.GL_MULTISAMPLE);

				if (!viewState.view.isEnabled())
					return;

				// repaint UI surface to texture if necessary
				// FIXME: should this be done on model or render thread?
				UI ui = controller.getUI();
				if (ui != null)
					ui.update();

				// render everything
				ViewType type = viewState.view.getConfig().getViewType();
				program.getViewInfo().update(gl, viewState.viewCameraState, type);
				program.getLightInfo().update(gl, viewState.viewCameraState);
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