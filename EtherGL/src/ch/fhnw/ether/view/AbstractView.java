/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
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

package ch.fhnw.ether.view;

import javax.media.nativewindow.util.Point;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.gl.NEWTWindow;
import ch.fhnw.ether.gl.Viewport;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

/**
 * Abstract view class that implements some basic functionality. Use as base for implementations.
 *
 * @author radar
 */
public abstract class AbstractView implements IView {
	private final NEWTWindow window;

	private final IController controller;

	private final ViewType viewType;

	private final Camera camera = new Camera(this);

	private Viewport viewport = new Viewport(0, 0, 1, 1);

	private boolean enabled = true;

	protected AbstractView(IController controller, int x, int y, int w, int h, ViewType viewType, String title) {
		this.window = new NEWTWindow(w, h, title);
		this.controller = controller;
		this.viewType = viewType;
		window.setView(this);
		Point p = window.getPosition();
		if (x != -1)
			p.setX(x);
		if (y != -1)
			p.setY(y);
		window.setPosition(p);
	}

	@Override
	public GLAutoDrawable getDrawable() {
		return window.getDrawable();
	}

	@Override
	public final IController getController() {
		return controller;
	}

	@Override
	public final Camera getCamera() {
		return camera;
	}

	@Override
	public final Viewport getViewport() {
		return viewport;
	}

	@Override
	public final ViewType getViewType() {
		return viewType;
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
	public final void repaint() {
		getController().repaintView(this);
	}

	@Override
	public final void refresh() {
		getController().getCurrentTool().refresh(this);
		repaint();
	}

	// GLEventListener implementation

	@Override
	public final void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public final void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

		if (!isEnabled())
			return;

		// fetch viewport
		int[] vp = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);
		viewport = new Viewport(vp[0], vp[1], vp[2], vp[3]);

		// FIXME testing
		// getCamera().addToRotateZ(1);
		// System.out.println("display" + this);

		// repaint UI surface if necessary
		getController().getUI().update();

		// render everything
		try {
			getController().getRenderer().render(gl.getGL3(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public final void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL();

		if (height == 0)
			height = 1; // prevent divide by zero
		gl.glViewport(0, 0, width, height);
		viewport = new Viewport(0, 0, width, height);
		camera.refresh();
	}

	@Override
	public final void dispose(GLAutoDrawable drawable) {
		// FIXME implement
	}

	// key listener

	@Override
	public void keyPressed(KeyEvent e) {
		controller.keyPressed(e, this);
		// TODO: should we set e to "consumed", i.e. e.setConsumed(true)?
	}

	@Override
	public void keyReleased(KeyEvent e) {
		controller.keyReleased(e, this);
	}

	// mouse listener

	@Override
	public void mouseEntered(MouseEvent e) {
		controller.mouseEntered(e, this);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		controller.mouseExited(e, this);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		window.requestFocus();
		controller.mousePressed(e, this);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		controller.mouseReleased(e, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		controller.mouseClicked(e, this);
	}

	// mouse motion listener

	@Override
	public void mouseMoved(MouseEvent e) {
		controller.mouseMoved(e, this);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		controller.mouseDragged(e, this);
	}

	// mouse wheel listener

	@Override
	public void mouseWheelMoved(MouseEvent e) {
		controller.mouseWheelMoved(e, this);
	}
}
