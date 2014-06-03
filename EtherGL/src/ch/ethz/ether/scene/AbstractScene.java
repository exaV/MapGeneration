/*
 * Copyright (c) 2013 - 2014, ETH Zurich & FHNW (Stefan Muller Arisona)
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
 *  Neither the name of ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
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

package ch.ethz.ether.scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.ether.model.IModel;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.ether.tools.ITool;
import ch.ethz.ether.tools.NavigationTool;
import ch.ethz.ether.tools.PickTool;
import ch.ethz.ether.ui.Button;
import ch.ethz.ether.ui.UI;
import ch.ethz.ether.view.IView;
import ch.ethz.ether.view.IView.ViewType;

/**
 * Abstract scene class that implements some basic common functionality. Use as
 * base for common implementations.
 *
 * @author radar
 */
// TODO: we should probably move UI event handling to UI...
// TODO: PickTool doesn't really belong here (any tools at all?)
public abstract class AbstractScene implements IScene {
    private IModel model;
    private final ArrayList<IView> views = new ArrayList<>();
    private final IRenderer renderer;
    private final UI ui = new UI(this);

    private final NavigationTool navigationTool = new NavigationTool(this);
    private final PickTool pickTool = new PickTool(this);

    private IView currentView = null;
    private ITool activeTool = pickTool;


    protected AbstractScene(IRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public IModel getModel() {
        return model;
    }

    @Override
    public final void setModel(IModel model) {
        this.model = model;
    }

    @Override
    public final void addView(IView view) {
        views.add(view);
        if (currentView == null)
            currentView = view;
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
    public final void repaintViews() {
        for (IView view : views)
            view.repaint();
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

        // buttons have precedence over tools
        for (Button button : ui.getButtons()) {
            if (button.getKey() == e.getKeyCode()) {
                button.fire(view);
                view.getScene().repaintViews();
                return;
            }
        }

        // always handle ESC (if not handled by button)
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

        // finally, pass on to tool
        activeTool.keyPressed(e, view);
    }

    @Override
    public void keyReleased(KeyEvent e, IView view) {
    }

    @Override
    public void keyTyped(KeyEvent e, IView view) {
    }

    // mouse listener

    @Override
    public void mouseEntered(MouseEvent e, IView view) {
    }

    @Override
    public void mouseExited(MouseEvent e, IView view) {
    }

    @Override
    public void mousePressed(MouseEvent e, IView view) {
        setCurrentView(view);

        // buttons have precedence over tools
        if (view.getViewType() == ViewType.INTERACTIVE_VIEW) {
            for (Button button : ui.getButtons()) {
                if (button.hit(e.getX(), e.getY(), view)) {
                    button.fire(view);
                    view.getScene().repaintViews();
                    return;
                }
            }
        }

        // handle tools (with active navigation when modifier is pressed)
        if (!isModifierDown(e))
            activeTool.mousePressed(e, view);
        else
            navigationTool.mousePressed(e, view);
    }

    @Override
    public void mouseReleased(MouseEvent e, IView view) {
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
        if (view.getViewType() == ViewType.INTERACTIVE_VIEW) {
            Button button = null;
            for (Button b : ui.getButtons()) {
                if (b.hit(e.getX(), e.getY(), view)) {
                    button = b;
                    break;
                }
            }
            String message = button != null ? button.getHelp() : null;
            view.getScene().getUI().setMessage(message);
        }
        activeTool.mouseMoved(e, view);
        navigationTool.mouseMoved(e, view);
    }

    @Override
    public void mouseDragged(MouseEvent e, IView view) {
        if (!isModifierDown(e))
            activeTool.mouseDragged(e, view);
        else
            navigationTool.mouseDragged(e, view);
    }

    // mouse wheel listener

    @Override
    public void mouseWheelMoved(MouseWheelEvent e, IView view) {
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
}
