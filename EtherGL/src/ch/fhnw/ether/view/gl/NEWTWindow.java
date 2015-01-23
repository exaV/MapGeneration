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

package ch.fhnw.ether.view.gl;

import javax.media.nativewindow.util.Point;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;

import ch.fhnw.ether.view.IView.Config;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;

/**
 * OpenGL frame class (i.e. an OpenGL window) that combines a GLCanvas and a JFrame.
 *
 * @author radar
 */
public final class NEWTWindow {
	private static GLAutoDrawable sharedDrawable = null;
	private static int numWindows = 0;

	private GLWindow window;

	/**
	 * Creates undecorated frame.
	 *
	 * @param width
	 *            the frame's width
	 * @param height
	 *            the frame's height
	 * @param config 
	 *            The configuration.
	 */
	public NEWTWindow(int width, int height, Config config) {
		this(width, height, null, config);
	}

	/**
	 * Creates a decorated or undecorated frame with given dimensions
	 *
	 * @param width
	 *            the frame's width
	 * @param height
	 *            the frame's height
	 * @param title
	 *            the frame's title, nor null for an undecorated frame
	 * @param config 
	 *            The configuration.
	 */
	public NEWTWindow(int width, int height, String title, Config config) {
		GLCapabilities capabilities = GLContextManager.getCapabilities(config);
		if (sharedDrawable == null) {
			sharedDrawable = GLContextManager.getSharedDrawable(capabilities);
			sharedDrawable.display();			
		}
		numWindows++;
		window = GLWindow.create(capabilities);
		window.setSharedAutoDrawable(sharedDrawable);
		window.setSize(width, height);

		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDestroyed(WindowEvent e) {
				numWindows--;
				if (numWindows == 0)
					System.exit(0);
			}
		});
		if (title != null)
			window.setTitle(title);
		else
			window.setUndecorated(true);
		window.setVisible(true);
	}

	public void dispose() {
		window.destroy();
	}

	public void requestFocus() {
		window.requestFocus();
	}

	public Point getPosition() {
		return window.getLocationOnScreen(null);
	}

	public void setPosition(Point position) {
		window.setPosition(position.getX(), position.getY());
	}

	public GLWindow getWindow() {
		return window;
	}
}
