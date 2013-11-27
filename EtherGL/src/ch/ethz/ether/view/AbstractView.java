/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.ether.view;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import ch.ethz.ether.gl.DrawingUtilities;
import ch.ethz.ether.gl.Frame;
import ch.ethz.ether.gl.Matrix4x4;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.ether.scene.IScene;
import ch.ethz.ether.ui.Button;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Abstract view class that implements some basic common functionality. Use as
 * base for common implementations.
 * 
 * @author radar
 * 
 */
public abstract class AbstractView implements IView {

	private static final Font FONT = new Font("SansSerif", Font.BOLD, 12);

	private final Frame frame;
	private final IScene scene;

	private final ViewType viewType;
	private final String id;

	private Camera camera = new Camera();

	private TextRenderer textRenderer;
	
	private int[] viewport = new int[4];
	private float[] projectionMatrix3D = Matrix4x4.identity();
	private float[] modelviewMatrix3D = Matrix4x4.identity();
	private boolean lockMatrices3D = false;

	private float[] projectionMatrix2D = Matrix4x4.identity();
	private float[] modelviewMatrix2D = Matrix4x4.identity();

	protected AbstractView(IScene scene, int x, int y, int w, int h, ViewType viewType, String id, String title) {
		this.frame = new Frame(w, h, title);
		this.scene = scene;
		this.viewType = viewType;
		this.id = id;
		frame.setView(this);
		Point p = frame.getJFrame().getLocation();
		if (x != -1)
			p.x = x;
		if (y != -1)
			p.y = y;
		frame.getJFrame().setLocation(p);
	}

	@Override
	public final Frame getFrame() {
		return frame;
	}

	@Override
	public IScene getScene() {
		return scene;
	}

	@Override
	public IRenderer getRenderer() {
		// TODO: allow support for view-specific renderers
		return scene.getDefaultRenderer();
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public final int getWidth() {
		return viewport[2];
	}

	@Override
	public final int getHeight() {
		return viewport[3];
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ViewType getViewType() {
		return viewType;
	}

	@Override
	public int[] getViewport() {
		return viewport;
	}

	@Override
	public final float[] getProjectionMatrix() {
		return projectionMatrix3D;
	}

	@Override
	public final float[] getModelviewMatrix() {
		return modelviewMatrix3D;
	}

	@Override
	public void setMatrices(float[] projectionMatrix, float[] modelviewMatrix) {
		if (projectionMatrix == null) {
			this.lockMatrices3D = false;
		} else {
			this.lockMatrices3D = true;
			this.projectionMatrix3D = projectionMatrix;
			this.modelviewMatrix3D = modelviewMatrix;
		}
	}
	
	@Override
	public TextRenderer getTextRenderer() {
		return textRenderer;
	}

	@Override
	public boolean isEnabled() {
		return getScene().isEnabled(this);
	}

	@Override
	public boolean isCurrent() {
		return getScene().getCurrentView() == this;
	}

	// opengl handling

	@Override
	public void init(GLAutoDrawable drawable, GL2 gl) {
		textRenderer = new TextRenderer(FONT);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnable(GL2.GL_POINT_SMOOTH);
	}

	@Override
	public void display(GLAutoDrawable drawable, GL2 gl) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

		if (!getScene().isEnabled(this))
			return;

		// fetch viewport
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		// update 3D matrices
		if (!lockMatrices3D) {
			Camera c = getCamera();
			Matrix4x4.perspective(c.getFOV(), (float)getWidth() / (float)getHeight(), c.getNearClippingPlane(), c.getFarClippingPlane(), projectionMatrix3D);

			Matrix4x4.identity(modelviewMatrix3D);
			Matrix4x4.translate(c.getTranslateX(), c.getTranslateY(), -c.getDistance(), modelviewMatrix3D);
			Matrix4x4.rotate(c.getRotateX() - 90, 1, 0, 0, modelviewMatrix3D);
			Matrix4x4.rotate(c.getRotateZ(), 0, 0, 1, modelviewMatrix3D);
		}

		// render model
		getRenderer().render(gl, this);
		

		// render ui (XXX this should probably move)
		if (getViewType() == ViewType.INTERACTIVE_VIEW) {
			Matrix4x4.ortho(0, viewport[2], viewport[3], 0, -1, 1, projectionMatrix2D);
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadMatrixf(projectionMatrix2D, 0);

			Matrix4x4.identity(modelviewMatrix2D);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadMatrixf(modelviewMatrix2D, 0);

			for (Button button : getScene().getButtons()) {
				button.render(gl, this);
			}
			if (Button.getMessage() != null) {
				DrawingUtilities.drawText2D(this, 8, 8, Button.getMessage());
			}
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, GL2 gl, int x, int y, int width, int height) {
		if (height == 0)
			height = 1; // prevent divide by zero
		viewport[2] = width;
		viewport[3] = height;
		gl.glViewport(0, 0, width, height);
	}

	@Override
	public void dispose(GLAutoDrawable drawable, GL2 gl) {
		// XXX TODO
	}

	@Override
	public void repaint() {
		frame.repaint();
	}

	// key listener

	@Override
	public void keyPressed(KeyEvent e) {
		scene.keyPressed(e, this);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		scene.keyReleased(e, this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		scene.keyTyped(e, this);
	}

	// mouse listener

	@Override
	public void mouseEntered(MouseEvent e) {
		scene.mouseEntered(e, this);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		scene.mouseExited(e, this);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		frame.requestFocus();
		scene.mousePressed(e, this);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		scene.mouseReleased(e, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		scene.mouseClicked(e, this);
	}

	// mouse motion listener

	@Override
	public void mouseMoved(MouseEvent e) {
		scene.mouseMoved(e, this);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		scene.mouseDragged(e, this);
	}

	// mouse wheel listener

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scene.mouseWheelMoved(e, this);
	}
}
