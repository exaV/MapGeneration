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

package ch.fhnw.ether.view.gl;

import org.lwjgl.opengl.GL11;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.camera.CameraMatrices;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec2;

/**
 * Default view class that implements some basic functionality. Use as base for more complex implementations.
 * 
 * Thread safety: getCameraMatrices & getViewport are thread safe.
 *
 * @author radar
 */
public class DefaultView implements IView {

	private final Config viewConfig;

	private GLFWWindow window;

	private IController controller;

	private ICamera camera;
	private CameraMatrices cameraMatrices = null;
	private boolean cameraLocked = false;

	private Viewport viewport = new Viewport(0, 0, 1, 1);

	private boolean enabled = true;

	public DefaultView(IController controller, int x, int y, int w, int h, Config viewConfig, String title, ICamera camera) {
		this.controller = controller;
		this.viewConfig = viewConfig;
		setCamera(camera);

		window = new GLFWWindow(w, h, title, viewConfig);

		Vec2 p = window.getPosition();
		if (x == -1)
			x = (int) p.x;
		if (y == -1)
			y = (int) p.y;
		window.setPosition(new Vec2(x, y));
	}

	@Override
	public void dispose() {
		window.dispose();
	}

	@Override
	public final IController getController() {
		return controller;
	}

	@Override
	public final ICamera getCamera() {
		return camera;
	}

	@Override
	public final void setCamera(ICamera camera) {
		synchronized (this) {
			if (this.camera != null)
				this.camera.removeUpdateListener(this);
			this.camera = camera;
			if (camera != null)
				this.camera.addUpdateListener(this);
		}
	}

	@Override
	public final CameraMatrices getCameraMatrices() {
		synchronized (this) {
			ICamera c = camera;
			if (cameraMatrices == null)
				cameraMatrices = new CameraMatrices(c.getPosition(), c.getTarget(), c.getUp(), c.getFov(), c.getNear(), c.getFar(), viewport.getAspect());
			return cameraMatrices;
		}
	}

	@Override
	public void setCameraMatrices(Mat4 viewMatrix, Mat4 projMatrix) {
		synchronized (this) {
			if (viewMatrix == null && projMatrix == null) {
				cameraMatrices = null;
				cameraLocked = false;
			} else {
				cameraMatrices = new CameraMatrices(viewMatrix, projMatrix);
				cameraLocked = true;
			}
		}
	}

	@Override
	public final Viewport getViewport() {
		synchronized (this) {
			return viewport;
		}
	}

	@Override
	public Config getConfig() {
		return viewConfig;
	}

	@Override
	public final boolean isEnabled() {
		return enabled;
	}

	@Override
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public final boolean isCurrent() {
		return getController().getCurrentView() == this;
	}

	@Override
	public final void requestRepaint() {
		getController().repaintView(this);
	}

	@Override
	public void requestUpdate(Object source) {
		if (source instanceof ICamera) {
			synchronized (this) {
				if (!cameraLocked)
					cameraMatrices = null;
			}
			if (isCurrent())
				getController().getCurrentTool().refresh(this);
			requestRepaint();
		}
	}
	
	@Override
	public void doRepaint() {
		try {
			// FIXME: need to make this configurable and move to renderer
			GL11.glClearColor(0.1f, 0.2f, 0.3f, 1.0f);
			GL11.glClearDepth(1.0f);

			if (viewConfig.has(ViewFlag.SMOOTH_LINES)) {
				GL11.glEnable(GL11.GL_LINE_SMOOTH);
				GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
			}

			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

			if (!isEnabled())
				return;

			Vec2 size = window.getSize();
			
			GL11.glViewport(0, 0, (int)size.x, (int)size.y);
			
			// repaint UI surface to texture if necessary (FIXME: should this be done on model or render thread?)
			UI ui = getController().getUI();
			if (ui != null)
				ui.update();

			// render everything
			getController().getRenderer().render(this);

			int error = GL11.glGetError();
			if (error != 0)
				System.err.println("renderer returned with exisiting GL error 0x" + Integer.toHexString(error));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
