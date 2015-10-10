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

package ch.fhnw.ether.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.fhnw.ether.controller.event.EventDrivenScheduler;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.controller.event.IScheduler;
import ch.fhnw.ether.controller.tool.ITool;
import ch.fhnw.ether.controller.tool.NavigationTool;
import ch.fhnw.ether.controller.tool.PickTool;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.forward.ForwardRenderer;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.ui.UI;
import ch.fhnw.ether.view.IView;

/**
 * Default controller that implements some basic common functionality. Use as base for more complex implementations.
 *
 * @author radar
 */
// FIXME: PickTool doesn't really belong here (any tools at all?)
public class DefaultController implements IController {
	private static final boolean DBG = true;

	private final IScheduler scheduler;
	private final IRenderer renderer;

	private IScene scene;

	private final ArrayList<IView> views = new ArrayList<>();
	private final UI ui;

	private final NavigationTool navigationTool;
	private final PickTool pickTool;

	private IView currentView;
	private IView hoverView;
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
	public IScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public final IRenderer getRenderer() {
		return renderer;
	}

	@Override
	public final UI getUI() {
		return ui;
	}
	
	// view listener
	
	@Override
	public final void viewCreated(IView view) {
		if (DBG)
			System.out.println("view created");

		views.add(view);
		scheduler.addDrawable(view.getDrawable());
	}

	@Override
	public void viewDisposed(IView view) {
		if (DBG)
			System.out.println("view disposed");

		views.remove(view);
		if (currentView == view)
			currentView = null;
		scheduler.removeDrawable(view.getDrawable());
	}

	@Override
	public void viewGainedFocus(IView view) {
		if (DBG)
			System.out.println("view gained focus");

		setCurrentView(view);
	}
	
	@Override
	public void viewLostFocus(IView view) {
		if (DBG)
			System.out.println("view lost focus");
	}

	// key listener

	@Override
	public void keyPressed(IKeyEvent e) {
		if (DBG)
			System.out.println("key pressed");
	
		setCurrentView(e.getView());

		// ui has precedence over everything else
		if (ui != null && ui.keyPressed(e))
			return;

		// always handle ESC (if not handled by button)
		if (e.getKey() == IKeyEvent.VK_ESCAPE)
			System.exit(0);

		// finally, pass on to tool
		activeTool.keyPressed(e);
	}

	@Override
	public void keyReleased(IKeyEvent e) {
	}

	// pointer listener

	@Override
	public void pointerEntered(IPointerEvent e) {
		if (DBG)
			System.out.println("pointer entered");
		hoverView = e.getView();
		navigationTool.activate();
		if (ui != null)
			ui.pointerEntered(e);
	}

	@Override
	public void pointerExited(IPointerEvent e) {
		if (DBG)
			System.out.println("pointer exited");

		if (ui != null)
			ui.pointerExited(e);
		navigationTool.deactivate();
		hoverView = null;
	}

	@Override
	public void pointerPressed(IPointerEvent e) {
		if (DBG)
			System.out.println("pointer pressed");
		
		if (hoverView == null)
			pointerEntered(e);

		setCurrentView(e.getView());

		// ui has precedence over everything else
		if (ui != null && ui.pointerPressed(e))
			return;

		// handle tools (with active navigation when modifier is pressed)
		if (!e.isModifierDown())
			activeTool.pointerPressed(e);
		else
			navigationTool.pointerPressed(e);
	}

	@Override
	public void pointerReleased(IPointerEvent e) {
		if (DBG)
			System.out.println("pointer released");

		if (hoverView == null)
			pointerEntered(e);

		if (ui != null && ui.pointerReleased(e))
			return;

		if (!e.isModifierDown())
			activeTool.pointerReleased(e);
		else
			navigationTool.pointerReleased(e);
	}

	@Override
	public void pointerClicked(IPointerEvent e) {
		if (DBG)
			System.out.println("pointer clicked");
		
		if (hoverView == null)
			pointerEntered(e);
	}

	// pointer motion listener

	@Override
	public void pointerMoved(IPointerEvent e) {
		//if (DBG)
		//	System.out.println("pointer moved");
		
		if (hoverView == null)
			pointerEntered(e);

		if (ui != null)
			ui.pointerMoved(e);
		activeTool.pointerMoved(e);
		navigationTool.pointerMoved(e);
	}

	@Override
	public void pointerDragged(IPointerEvent e) {
		//if (DBG)
		//	System.out.println("pointer dragged");
		
		if (hoverView == null)
			pointerEntered(e);

		// ui has precedence over everything else
		if (ui != null && ui.pointerDragged(e))
			return;
		
		if (!e.isModifierDown())
			activeTool.pointerDragged(e);
		else
			navigationTool.pointerDragged(e);
	}

	// pointer scrolled listener

	@Override
	public void pointerScrolled(IPointerEvent e) {
		//if (DBG)
		//	System.out.println("pointer scrolled");

		if (hoverView == null)
			pointerEntered(e);

		navigationTool.pointerScrolled(e);
	}


	public static void printHelp(String[] help) {
		for (String s : help)
			System.out.println(s);
	}
	
	// private stuff

	private void setCurrentView(IView view) {
		if (DBG)
			System.out.println("set current view");
		if (currentView != view) {
			currentView = view;
			getCurrentTool().refresh(currentView);
			repaintViews();
		}
	}
}
