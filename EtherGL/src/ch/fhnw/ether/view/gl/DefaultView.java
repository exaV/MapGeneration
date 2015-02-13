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

import javax.media.nativewindow.util.Point;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.camera.CameraMatrices;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.math.Mat4;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;

/**
 * Default view class that implements some basic functionality. Use as base for more complex implementations.
 * 
 * Thread safety: getCameraMatrices & getViewport are thread safe.
 *
 * @author radar
 */
public class DefaultView implements IView {

	private final Config viewConfig;

	private NEWTWindow window;

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

		window = new NEWTWindow(w, h, title, viewConfig);
		window.getWindow().addGLEventListener(glEventListener);
		window.getWindow().addWindowListener(windowListener);
		window.getWindow().addMouseListener(mouseListener);
		window.getWindow().addKeyListener(keyListener);

		Point p = window.getPosition();
		if (x != -1)
			p.setX(x);
		if (y != -1)
			p.setY(y);
		window.setPosition(p);
	}

	@Override
	public void dispose() {
		// nothing else to be done here, the gl event listener below will deal with disposing
		window.dispose();
	}

	@Override
	public GLAutoDrawable getDrawable() {
		return window.getWindow();
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
	public final void repaint() {
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
			repaint();
		}
	}

	// GLEventListener implementation

	private GLEventListener glEventListener = new GLEventListener() {

		@Override
		public final void init(GLAutoDrawable drawable) {
			try {
				GL gl = drawable.getGL();

				// FIXME: need to make this configurable and move to renderer
				gl.glClearColor(0.1f, 0.2f, 0.3f, 1.0f);
				gl.glClearDepth(1.0f);
				
				if(viewConfig.has(ViewFlag.SMOOTH_LINES)) {
					gl.glEnable(GL.GL_LINE_SMOOTH);
					gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
				}

				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public final void display(GLAutoDrawable drawable) {
			try {
				GL gl = drawable.getGL();
				GL3 gl3 = gl.getGL3();
				// gl3 = new TraceGL3(gl3, System.out);
				// gl3 = new DebugGL3(gl3);

				gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

				gl3.glEnable(GL.GL_MULTISAMPLE);
				
				if (!isEnabled())
					return;
				
				// repaint UI surface to texture if necessary (FIXME: should this be done on model or render thread?)
				UI ui = getController().getUI();
				if (ui != null)
					ui.update();

				// render everything
				getController().getRenderer().render(gl3, DefaultView.this);

				int error = gl.glGetError();
				if (error != 0)
					System.err.println("renderer returned with exisiting GL error 0x" + Integer.toHexString(error));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public final void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			try {
				GL gl = drawable.getGL();

				if (height == 0)
					height = 1; // prevent divide by zero
				gl.glViewport(0, 0, width, height);
				synchronized (this) {
					viewport = new Viewport(0, 0, width, height);
					if (!cameraLocked)
						cameraMatrices = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public final void dispose(GLAutoDrawable drawable) {
			try {
				controller.removeView(DefaultView.this);
				setCamera(null);
				window = null;
				controller = null;
				camera = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	// window listener

	private WindowListener windowListener = new WindowAdapter() {
		@Override
		public void windowGainedFocus(WindowEvent e) {
			try {
				controller.setCurrentView(DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		};
	};

	// key listener

	private KeyListener keyListener = new KeyListener() {

		@Override
		public void keyPressed(KeyEvent e) {
			try {
				controller.keyPressed(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			try {
				controller.keyReleased(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	// mouse listener

	private MouseListener mouseListener = new MouseListener() {

		@Override
		public void mouseEntered(MouseEvent e) {
			try {
				controller.mouseEntered(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			try {
				controller.mouseExited(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			try {
				window.requestFocus();
				controller.mousePressed(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			try {
				controller.mouseReleased(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				controller.mouseClicked(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			try {
				controller.mouseMoved(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				controller.mouseDragged(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void mouseWheelMoved(MouseEvent e) {
			try {
				controller.mouseWheelMoved(e, DefaultView.this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};
}
