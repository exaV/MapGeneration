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

package ch.ethz.ether.ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.ether.render.IRenderGroup.Flag;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.ether.render.TextRenderGroup;
import ch.ethz.ether.scene.IScene;
import ch.ethz.ether.view.IView;
import ch.ethz.ether.view.IView.ViewType;
import ch.ethz.util.UpdateRequest;

public final class UI {
    private final IScene scene;
    private final TextRenderGroup group = new TextRenderGroup(Source.TOOL, 0, 0, 512, 512);
    private final UpdateRequest updater = new UpdateRequest();

    private final List<IWidget> widgets = new ArrayList<>();
    private String message;

    public UI(IScene scene) {
        this.scene = scene;
        enable();
        group.addFlag(Flag.INTERACTIVE_VIEW_ONLY);
    }

    public void enable() {
        IRenderer.GROUPS.add(group);
        requestUpdate();
    }

    public void disable() {
        IRenderer.GROUPS.remove(group);
    }

    public void update() {
        if (!updater.needsUpdate())
            return;

        group.clear();

        for (IWidget widget : widgets) {
        	widget.draw(group);
        }

        if (message != null)
            group.drawString(message, 0, group.getHeight() - TextRenderGroup.FONT.getSize());
    }

    public List<IWidget> getWidgets() {
        return Collections.unmodifiableList(widgets);
    }

    public void addWidget(IWidget widget) {
        widget.setUI(this);
        widgets.add(widget);
        requestUpdate();
    }

    public void addWidgets(Collection<? extends IWidget> widgets) {
        for (IWidget widget : widgets) {
            addWidget(widget);
        }
    }

    public void setMessage(String message) {
        if (this.message == message || (this.message != null && this.message.equals(message)))
            return;
        this.message = message;
        requestUpdate();
    }

    public int getX() {
        return group.getX();
    }

    public int getY() {
        return group.getX();
    }

    public int getWidth() {
        return group.getWidth();
    }

    public int getHeight() {
        return group.getHeight();
    }
    
    public void requestUpdate() {
        updater.requestUpdate();
        scene.repaintViews();
    }
    
    
    // key listener

    public boolean keyPressed(KeyEvent e, IView view) {
        if (view.getViewType() == ViewType.INTERACTIVE_VIEW) {
	        for (IWidget widget : getWidgets()) {
	        	if (widget.keyPressed(e, view))
	        		return true;
	        }
        }
        return false;
    }

    // mouse listener

    public void mouseEntered(MouseEvent e, IView view) {
    }

    public void mouseExited(MouseEvent e, IView view) {
    }

    public boolean mousePressed(MouseEvent e, IView view) {
        if (view.getViewType() == ViewType.INTERACTIVE_VIEW) {
            for (IWidget widget : getWidgets()) {
            	if (widget.mousePressed(e, view))
            		return true;
            }
        }
        return false;
    }

    // mouse motion listener

    public void mouseMoved(MouseEvent e, IView view) {
        if (view.getViewType() == ViewType.INTERACTIVE_VIEW) {
            for (IWidget widget : getWidgets()) {
                if (widget.hit(e.getX(), e.getY(), view)) {
                    String message = widget != null ? widget.getHelp() : null;
                    setMessage(message);
                    return;
                }
            }
        }
    }

    public boolean mouseDragged(MouseEvent e, IView view) {
        if (view.getViewType() == ViewType.INTERACTIVE_VIEW) {
            for (IWidget widget : getWidgets()) {
            	if (widget.mouseDragged(e, view))
            		return true;
            }
        }
        return false;
    }
}
