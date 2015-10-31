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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.render.IRenderer.IRenderState;
import ch.fhnw.ether.render.Renderable.RenderData;
import ch.fhnw.ether.render.variable.builtin.LightUniformBlock;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.camera.IViewCameraState;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

/**
 * Default render manager. This would also be the place to do various
 * optimizations such as geometry / material merging etc. Currently
 * straightforward, as provided by scene.
 *
 * @author radar
 */
public class DefaultRenderManager implements IRenderManager {

	private static final class SceneViewState {
		ICamera camera = new Camera();
		IViewCameraState viewCameraState;

		SceneViewState(IView view) {
			viewCameraState = new ViewCameraState(view, camera);
		}
	}

	private static final class SceneMeshState {
		Renderable renderable;
	}

	private final class SceneState {
		final Map<IView, SceneViewState> views = new IdentityHashMap<>();
		final List<ILight> lights = new ArrayList<>(Collections.singletonList(DEFAULT_LIGHT));
		final Set<IMaterial> materials = Collections.newSetFromMap(new IdentityHashMap<>());
		final Set<IGeometry> geometries = Collections.newSetFromMap(new IdentityHashMap<>());
		final Map<IMesh, SceneMeshState> renderables = new IdentityHashMap<>();

		boolean rebuildMeshes = false;

		SceneState() {
		}

		void addView(IView view) {
			SceneViewState vcs = new SceneViewState(view);
			if (views.putIfAbsent(view, vcs) != null)
				throw new IllegalArgumentException("view already in renderer: " + view);
			setCamera(view, vcs.camera);
		}

		void removeView(IView view) {
			SceneViewState vcs = views.remove(view);
			if (vcs == null)
				throw new IllegalArgumentException("view not in renderer: " + view);
		}

		ICamera getCamera(IView view) {
			return views.get(view).camera;
		}

		void setCamera(IView view, ICamera camera) {
			camera.getUpdater().request();
			views.get(view).camera = camera;
		}

		void lockCamera(IView view, Mat4 viewMatrix, Mat4 projMatrix) {
			views.get(view).viewCameraState = new ViewCameraState(view, viewMatrix, projMatrix);
		}

		IViewCameraState getViewCameraState(IView view) {
			return views.get(view).viewCameraState;
		}

		void addLight(ILight light) {
			if (lights.contains(light))
				throw new IllegalArgumentException("light already in renderer: " + light);
			if (lights.size() == LightUniformBlock.MAX_LIGHTS)
				throw new IllegalStateException("too many lights in renderer: " + LightUniformBlock.MAX_LIGHTS);
			if (lights.get(0) == DEFAULT_LIGHT)
				lights.remove(0);
			lights.add(light);
		}

		void removeLight(ILight light) {
			if (!lights.remove(light))
				throw new IllegalArgumentException("light not in renderer: " + light);
			if (lights.isEmpty())
				lights.add(DEFAULT_LIGHT);
		}

		void addMesh(IMesh mesh) {
			if (renderables.putIfAbsent(mesh, new SceneMeshState()) != null)
				throw new IllegalArgumentException("mesh already in renderer: " + mesh);
			rebuildMeshes = true;
		}

		void removeMesh(IMesh mesh) {
			if (renderables.remove(mesh) == null)
				throw new IllegalArgumentException("mesh not in renderer: " + mesh);
			rebuildMeshes = true;
		}

		/**
		 * Executed at the end of a scene time slice. Will copy all required
		 * render state that can be executed on a separate thread. Resets all
		 * scene update flags, this renderer needs to take care that the
		 * returned render state is always realized, otherwise the states will
		 * get out of sync resulting in undefined overall state.
		 * 
		 * @param renderer
		 * @return
		 */
		RenderState create(IRenderer renderer) {
			RenderState renderState = new RenderState();

			// 1. set view matrices for each updated camera, add to render state
			views.forEach((view, svs) -> {
				if (svs.camera.getUpdater().test())
					svs.viewCameraState = new ViewCameraState(view, svs.camera);
				renderState.views.add(view);
				renderState.viewCameraStates.add(svs.viewCameraState);
			});
			// two loops required since camera can be active in multiple views
			views.forEach((view, svs) -> svs.camera.getUpdater().clear());

			// 2. add lights to render state
			// currently updates are not checked, we simply update everything
			renderState.lights.addAll(lights);

			// 3. add meshes and mesh updates to render state
			if (rebuildMeshes) {
				materials.clear();
				geometries.clear();
				renderables.forEach((mesh, state) -> {
					materials.add(mesh.getMaterial());
					geometries.add(mesh.getGeometry());
				});
				rebuildMeshes = false;
			}
			renderables.forEach((mesh, state) -> {
				IMaterial material = mesh.getMaterial();
				IGeometry geometry = mesh.getGeometry();

				boolean materialChanged;
				boolean geometryChanged;
				if (state.renderable == null) {
					// TODO: optionally we could do the first update() on
					// drawable already here, using a shared context.
					state.renderable = renderer.createRenderable(mesh);
					materialChanged = true;
					geometryChanged = true;
				} else {
					materialChanged = material.getUpdater().test();
					geometryChanged = geometry.getUpdater().test() || mesh.getUpdater().testAndClear();
				}

				RenderData data = (materialChanged || geometryChanged) ? new RenderData(mesh, materialChanged, geometryChanged) : null;
				renderState.renderables.add(state.renderable);
				renderState.data.add(data);
			});
			materials.forEach((material) -> material.getUpdater().clear());
			geometries.forEach((geometry) -> geometry.getUpdater().clear());

			// 4. hey, we're done!
			return renderState;
		}
	}

	private static final class RenderState implements IRenderState {
		final List<IView> views = new ArrayList<>();
		final List<IViewCameraState> viewCameraStates = new ArrayList<>();
		final List<ILight> lights = new ArrayList<>();
		final List<Renderable> renderables = new ArrayList<>();
		final List<RenderData> data = new ArrayList<>();

		@Override
		public List<IView> getViews() {
			return views;
		}

		@Override
		public List<IViewCameraState> getViewCameraStates() {
			return viewCameraStates;
		}

		@Override
		public List<ILight> getLights() {
			return lights;
		}

		@Override
		public List<Renderable> getRenderables() {
			return renderables;
		}

		@Override
		public List<RenderData> getRenderData() {
			return data;
		}
	}

	private static final ILight DEFAULT_LIGHT = new DirectionalLight(Vec3.Z, RGB.BLACK, RGB.WHITE);

	private final IController controller;
	private final IRenderer renderer;

	private final SceneState sceneState = new SceneState();

	public DefaultRenderManager(IRenderer renderer) {
		this(null, renderer);
	}

	public DefaultRenderManager(IController controller, IRenderer renderer) {
		this.controller = controller;
		this.renderer = renderer;
	}

	@Override
	public void addView(IView view) {
		ensureSceneThread();
		sceneState.addView(view);
	}

	@Override
	public void removeView(IView view) {
		ensureSceneThread();
		sceneState.removeView(view);
	}

	@Override
	public ICamera getCamera(IView view) {
		ensureSceneThread();
		return sceneState.getCamera(view);
	}

	@Override
	public void setCamera(IView view, ICamera camera) {
		ensureSceneThread();
		sceneState.setCamera(view, camera);
	}

	@Override
	public void lockCamera(IView view, Mat4 viewMatrix, Mat4 projMatrix) {
		ensureSceneThread();
		sceneState.lockCamera(view, viewMatrix, projMatrix);
	}

	@Override
	public IViewCameraState getViewCameraState(IView view) {
		ensureSceneThread();
		return sceneState.getViewCameraState(view);
	}

	@Override
	public void addLight(ILight light) {
		ensureSceneThread();
		sceneState.addLight(light);
	}

	@Override
	public void removeLight(ILight light) {
		ensureSceneThread();
		sceneState.removeLight(light);
	}

	@Override
	public void addMesh(IMesh mesh) {
		ensureSceneThread();
		sceneState.addMesh(mesh);
	}

	@Override
	public void removeMesh(IMesh mesh) {
		ensureSceneThread();
		sceneState.removeMesh(mesh);
	}

	@Override
	public Runnable getRenderRunnable() {
		return () -> {
			ensureSceneThread();
			if (sceneState.views.isEmpty())
				return;
			renderer.submit(() -> sceneState.create(renderer));
		};
	}

	private void ensureSceneThread() {
		if (controller != null && !controller.getScheduler().isSceneThread())
			throw new IllegalThreadStateException("must be called on scene thread");
	}
}
