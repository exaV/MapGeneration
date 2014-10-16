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

package ch.fhnw.ether.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL3;

import ch.fhnw.ether.controller.event.EventDrivenScheduler;
import ch.fhnw.ether.controller.event.IScheduler;
import ch.fhnw.ether.controller.tool.ITool;
import ch.fhnw.ether.controller.tool.NavigationTool;
import ch.fhnw.ether.controller.tool.PickTool;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.forward.ForwardRenderer;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

/**
 * Default controller that implements some basic common functionality. Use as base for more complex implementations.
 *
 * @author radar
 */
// TODO: PickTool doesn't really belong here (any tools at all?)
public class DefaultController implements IController {
	private final IScheduler scheduler;
	private final IRenderer renderer;

	private IScene scene;

	private final ArrayList<IView> views = new ArrayList<>();
	private final UI ui;

	private final NavigationTool navigationTool;
	private final PickTool pickTool;

	private IView currentView;
	private ITool activeTool;

	public DefaultController() {
		this(new EventDrivenScheduler(), new ForwardRenderer());
	}

	public DefaultController(IScheduler scheduler, IRenderer renderer) {
		this.scheduler = scheduler;
		this.renderer = renderer;
		this.ui = new UI(this);
		this.navigationTool = new NavigationTool(this);
		this.pickTool = new PickTool(this);

		activeTool = pickTool;
	}

	@Override
	public final IScene getScene() {
		return scene;
	}

	@Override
	public final void setScene(IScene scene) {
		this.scene = scene;
		scene.setRenderer(renderer);
	}

	@Override
	public final void addView(IView view) {
		views.add(view);
		if (currentView == null)
			currentView = view;

		scheduler.addDrawable(view.getDrawable());

		view.repaint();
	}

	@Override
	public final List<IView> getViews() {
		return Collections.unmodifiableList(views);
	}

	@Override
	public final IView getCurrentView() {
		return currentView;
	}

	@Override
	public final void enableViews(Collection<IView> views) {
		if (views != null) {
			for (IView view : this.views) {
				view.setEnabled(views.contains(view));
			}
		} else {
			for (IView view : this.views) {
				view.setEnabled(true);
			}
		}
	}

	@Override
	public final void repaintView(IView view) {
		repaintViews();
	}

	@Override
	public final void repaintViews() {
		scheduler.requestUpdate(null);
	}

	@Override
	public final ITool getCurrentTool() {
		return activeTool;
	}

	@Override
	public final void setCurrentTool(ITool tool) {
		if (tool == null)
			tool = pickTool;

		if (activeTool == tool)
			return;

		activeTool.deactivate();
		activeTool = tool;
		activeTool.activate();
		activeTool.refresh(getCurrentView());

		repaintViews();
	}

	@Override
	public final NavigationTool getNavigationTool() {
		return navigationTool;
	}

	@Override
	public final IRenderer getRenderer() {
		return renderer;
	}

	@Override
	public final UI getUI() {
		return ui;
	}

	// key listener

	@Override
	public void keyPressed(KeyEvent e, IView view) {
		setCurrentView(view);

		// ui has precedence over everything else
		if (ui.keyPressed(e, view))
			return;

		// always handle ESC (if not handled by button)
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);

		// finally, pass on to tool
		activeTool.keyPressed(e, view);
	}

	@Override
	public void keyReleased(KeyEvent e, IView view) {
	}

	// mouse listener

	@Override
	public void mouseEntered(MouseEvent e, IView view) {
		ui.mouseEntered(e, view);
	}

	@Override
	public void mouseExited(MouseEvent e, IView view) {
		ui.mouseExited(e, view);
	}

	@Override
	public void mousePressed(MouseEvent e, IView view) {
		setCurrentView(view);

		// ui has precedence over everything else
		if (ui.mousePressed(e, view))
			return;

		// handle tools (with active navigation when modifier is pressed)
		if (!isModifierDown(e))
			activeTool.mousePressed(e, view);
		else
			navigationTool.mousePressed(e, view);
	}

	@Override
	public void mouseReleased(MouseEvent e, IView view) {
		if (ui.mouseReleased(e, view))
			return;

		if (!isModifierDown(e))
			activeTool.mouseReleased(e, view);
		else
			navigationTool.mouseReleased(e, view);
	}

	@Override
	public void mouseClicked(MouseEvent e, IView view) {
	}

	// mouse motion listener

	@Override
	public void mouseMoved(MouseEvent e, IView view) {
		ui.mouseMoved(e, view);
		activeTool.mouseMoved(e, view);
		navigationTool.mouseMoved(e, view);
	}

	@Override
	public void mouseDragged(MouseEvent e, IView view) {
		// ui has precedence over everything else
		if (ui.mouseDragged(e, view))
			return;

		if (!isModifierDown(e))
			activeTool.mouseDragged(e, view);
		else
			navigationTool.mouseDragged(e, view);
	}

	// mouse wheel listener

	@Override
	public void mouseWheelMoved(MouseEvent e, IView view) {
		navigationTool.mouseWheelMoved(e, view);
	}

	// private stuff

	private boolean isModifierDown(MouseEvent e) {
		return e.isShiftDown() || e.isControlDown() || e.isAltDown() || e.isMetaDown();
	}

	private void setCurrentView(IView view) {
		if (currentView != view) {
			currentView = view;
			getCurrentTool().refresh(currentView);
			repaintViews();
		}
	}

	public static void printHelp(String[] help) {
		for (String s : help)
			System.out.println(s);
	}

	@Override
	public void updateUI() {
		ui.update();
	}

	@Override
	public void requestRendering(GL3 gl, IView view) {
		if (scene != null)
			scene.renderUpdate();
		renderer.render(gl, view);
	}

}
