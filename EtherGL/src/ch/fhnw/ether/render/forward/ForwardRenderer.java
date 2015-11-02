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

package ch.fhnw.ether.render.forward;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import ch.fhnw.ether.render.AbstractRenderer;
import ch.fhnw.ether.render.Renderable;
import ch.fhnw.ether.scene.camera.IViewCameraState;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.GLContextManager;
import ch.fhnw.ether.view.gl.GLContextManager.IGLContext;

/*
 * General flow:
 * - foreach viewport
 * -- only use geometry assigned to this viewport
 * 
 * - foreach pass
 * -- setup opengl params specific to pass
 * 
 * - foreach material
 * -- enable shader
 * -- write uniforms
 * 
 * - foreach material instance (texture set + uniforms)
 * -- setup texture
 * -- write uniforms
 * -- refresh buffers
 * 
 * - foreach buffer (assembled objects)
 * -- setup buffer
 * -- draw
 */

/**
 * Simple and straightforward forward renderer.
 *
 * @author radar
 */
public final class ForwardRenderer extends AbstractRenderer {
	private static final boolean GL_NO_THREADS = System.getProperties().containsKey("gl_no_threads");
	
	// TODO: much of the render queue / threading / view handling can be
	// extracted into base class or separate execution manager

	private static final int MAX_RENDER_QUEUE_SIZE = 3;

	private final Thread renderThread;
	private final BlockingQueue<Runnable> renderQueue = new ArrayBlockingQueue<>(MAX_RENDER_QUEUE_SIZE);

	public ForwardRenderer() {
		this.renderThread = new Thread(this::runRenderThread, "renderthread");
		renderThread.start();
	}

	@Override
	public ExecutionPolicy getExecutionPolicy() {
		return ExecutionPolicy.DUAL_THREADED;
	}

	@Override
	public Renderable createRenderable(IMesh mesh) {
		return new Renderable(mesh, globals.attributes);
	}

	@Override
	public void submit(Supplier<IRenderState> supplier) {
		try {
			if (renderQueue.size() < MAX_RENDER_QUEUE_SIZE) {
				final IRenderState state = supplier.get();
				renderQueue.put(() -> render(state));
			} else {
				System.err.println("renderer: render queue full");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void render(IRenderState renderState) {
		// XXX GL_NO_THREADS -> LINUX/MESA/INTEL HACK (execute updates only once with first window to render)
		
		// update renderables (only once for all views)
		// note that it's absolutely imperative that this is executed for
		// every render runnable created. otherwise scene-render state will
		// get out of sync resulting in ugly fails.
		if (!GL_NO_THREADS) {
			try (IGLContext ctx = GLContextManager.acquireContext()) {
				renderState.getRenderUpdates().forEach(update -> update.getRenderable().update(ctx.getGL(), update));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		final AtomicBoolean once = GL_NO_THREADS ? new AtomicBoolean(false) : null;

		// render all views
		renderState.getRenderStates().forEach(targetState -> {
			IView view = targetState.getView();
			IViewCameraState vcs = targetState.getViewCameraState();
			targetState.getView().getWindow().display(drawable -> {
                try {
                	GL3 gl = drawable.getGL().getGL3();
                	if (GL_NO_THREADS && !once.getAndSet(true)) {
            			renderState.getRenderUpdates().forEach(update -> update.getRenderable().update(gl, update));                		
                	}
                    render(gl, targetState, view, vcs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
				return true;
			});
		});
	}

	private void render(GL3 gl, IRenderTargetState renderState, IView view, IViewCameraState vcs) {
		try {
			// XXX: make sure we only render on render thread (e.g. jogl
			// will do repaints on other threads when resizing windows...)
			if (!isRenderThread()) {
				return;
			}

			// gl = new TraceGL3(gl, System.out);
			// gl = new DebugGL3(gl);

			// FIXME: currently we clear in DefaultView.display() ... needs
			// to move
			// gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT |
			// GL.GL_STENCIL_BUFFER_BIT);

			if (!view.isEnabled())
				return;

			// update views and lights
			globals.viewInfo.update(gl, vcs);
			globals.lightInfo.update(gl, vcs, renderState.getLights());

			// render everything
			render(gl, renderState);

			int error = gl.glGetError();
			if (error != 0)
				System.err.println("renderer returned with exisiting GL error 0x" + Integer.toHexString(error));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void render(GL3 gl, IRenderTargetState state) {

		globals.viewInfo.setCameraSpace(gl);

		// ---- 1. DEPTH QUEUE (DEPTH WRITE&TEST ENABLED, BLEND OFF)
		// FIXME: where do we deal with two-sided vs one-sided? mesh options?
		// shader dependent?
		// gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(1, 3);
		renderObjects(gl, state, Queue.DEPTH);
		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
		// gl.glDisable(GL.GL_CULL_FACE);

		if (false)
			renderShadowVolumes(gl, state, Queue.DEPTH);

		// ---- 2. TRANSPARENCY QUEUE (DEPTH WRITE DISABLED, DEPTH TEST ENABLED,
		// BLEND ON)
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		renderObjects(gl, state, Queue.TRANSPARENCY);

		// ---- 3. OVERLAY QUEUE (DEPTH WRITE&TEST DISABLED, BLEND ON)
		gl.glDisable(GL.GL_DEPTH_TEST);
		renderObjects(gl, state, Queue.OVERLAY);

		// ---- 4. DEVICE SPACE OVERLAY QUEUE (DEPTH WRITE&TEST DISABLED, BLEND
		// ON)
		globals.viewInfo.setOrthoDeviceSpace(gl);
		renderObjects(gl, state, Queue.DEVICE_SPACE_OVERLAY);

		// ---- 5. SCREEN SPACE OVERLAY QUEUE(DEPTH WRITE&TEST DISABLED, BLEND
		// ON)
		globals.viewInfo.setOrthoScreenSpace(gl);
		renderObjects(gl, state, Queue.SCREEN_SPACE_OVERLAY);

		// ---- 6. CLEANUP: RETURN TO DEFAULTS
		gl.glDisable(GL.GL_BLEND);
		gl.glDepthMask(true);
	}

	private void runRenderThread() {
		while (true) {
			try {
				renderQueue.take().run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isRenderThread() {
		return Thread.currentThread().equals(renderThread);
	}
}
