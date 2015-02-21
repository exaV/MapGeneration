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
import ch.fhnw.ether.controller.event.IKeyListener;
import ch.fhnw.ether.controller.event.IMouseListener;
import ch.fhnw.ether.controller.event.IScheduler;
import ch.fhnw.ether.controller.event.IWindowListener;
import ch.fhnw.ether.controller.event.KeyEvent;
import ch.fhnw.ether.controller.event.MouseEvent;
import ch.fhnw.ether.controller.event.WindowEvent;
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
	private static final boolean DBG = false;

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
	public final void addView(IView view) {
		views.add(view);
		if (currentView == null)
			currentView = view;

		scheduler.addView(view);

		view.requestRepaint();
	}

	@Override
	public void removeView(IView view) {
		views.remove(view);
		if (currentView == view)
			currentView = null;

		scheduler.removeView(view);
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
	public void setCurrentView(IView view) {
		if (DBG)
			System.out.println("set current view");
		if (currentView != view) {
			currentView = view;
			getCurrentTool().refresh(currentView);
			repaintViews();
		}
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


	// window listener

	private IWindowListener windowListener = new IWindowListener() {
		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowGainedFocus(WindowEvent e) {
		}

		@Override
		public void windowLostFocus(WindowEvent e) {
		}

		@Override
		public void windowResized(WindowEvent e) {
		}

		@Override
		public void windowScrolled(WindowEvent e) {
		};
	};

	// key listener

	private IKeyListener keyListener = new IKeyListener() {

		@Override
		public void keyPressed(KeyEvent e) {
			if (DBG)
				System.out.println("key pressed");
			setCurrentView(e.getView());

			// controller specialisation comes first
			if (DefaultController.this.keyPressed(e))
				return;
			
			// ui has precedence over tools
			if (ui != null && ui.keyPressed(e))
				return;

			// always handle ESC (if not handled by button)
			if (e.getKey() == KeyEvent.KEY_ESCAPE)
				System.exit(0);

			// finally, pass on to tool
			activeTool.keyPressed(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	};
	
	protected boolean keyPressed(KeyEvent e) {
		return false;
	}

	// mouse listener

	private IMouseListener mouseListener = new IMouseListener() {

		@Override
		public void mouseEntered(MouseEvent e) {
			if (DBG)
				System.out.println("mouse entered");
			hoverView = e.getView();
			navigationTool.activate();
			if (ui != null)
				ui.mouseEntered(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (DBG)
				System.out.println("mouse exited");
			if (ui != null)
				ui.mouseExited(e);
			navigationTool.deactivate();
			hoverView = null;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (DBG)
				System.out.println("moved");
			if (hoverView == null)
				mouseEntered(e);

			if (ui != null)
				ui.mouseMoved(e);
			activeTool.mouseMoved(e);
			navigationTool.mouseMoved(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (DBG)
				System.out.println("dragged");
			if (hoverView == null)
				mouseEntered(e);

			// ui has precedence over everything else
			if (ui != null && ui.mouseDragged(e))
				return;

			if (!e.hasModifiers())
				activeTool.mouseDragged(e);
			else
				navigationTool.mouseDragged(e);
		}

		@Override
		public void mouseButtonPressed(MouseEvent e) {
			if (DBG)
				System.out.println("mouse pressed");
			if (hoverView == null)
				mouseEntered(e);

			setCurrentView(e.getView());

			// ui has precedence over everything else
			if (ui != null && ui.mousePressed(e))
				return;

			// handle tools (with active navigation when modifier is pressed)
			if (!e.hasModifiers())
				activeTool.mousePressed(e);
			else
				navigationTool.mousePressed(e);
		}

		@Override
		public void mouseButtonReleased(MouseEvent e) {
			if (DBG)
				System.out.println("released");
			if (hoverView == null)
				mouseEntered(e);

			if (ui != null && ui.mouseReleased(e))
				return;

			if (!e.hasModifiers())
				activeTool.mouseReleased(e);
			else
				navigationTool.mouseReleased(e);
		}
	};	
	

	// private stuff

	public static void printHelp(String[] help) {
		for (String s : help)
			System.out.println(s);
	}
}
