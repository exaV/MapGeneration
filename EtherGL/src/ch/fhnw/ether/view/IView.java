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

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.util.Viewport;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;

/**
 * A 'view' here is a view with some control functionality, i.e. it handles the
 * rendering of the model and also the user input specific to the view.
 * 
 * @author radar
 */
public interface IView extends GLEventListener, MouseListener, KeyListener {
	enum ViewType {
		INTERACTIVE_VIEW, MAPPED_VIEW
	}
	
	GLAutoDrawable getDrawable();

	/**
	 * Get the controller this view belongs to.
	 * 
	 * @return the controller
	 */
	IController getController();

	/**
	 * Get associated camera.
	 * 
	 * @return the camera
	 */
	ICamera getCamera();

	/**
	 * Get viewport [x, y, w, h].
	 * 
	 * @return the viewport.
	 */
	Viewport getViewport();

	/**
	 * Get view type.
	 * 
	 * @return the view type
	 */
	ViewType getViewType();

	/**
	 * Check whether view is enabled for rendering.
	 * 
	 * @return true if view is enabled, false otherwise
	 */
	boolean isEnabled();

	/**
	 * Enable or disable view for rendering.
	 * 
	 * @param enabled
	 *            enables view if true, disables otherwise
	 */
	void setEnabled(boolean enabled);

	/**
	 * Check whether view currently receives events.
	 * 
	 * @return true if view receives events, false otherwise
	 */
	boolean isCurrent();

	/**
	 * Request to repaint this view.
	 */
	void repaint();

	/**
	 * Request to update view and its dependencies (e.g. tools) and then
	 * repaint. For instance, this is called when the camera changed.
	 */
	void refresh();
}
