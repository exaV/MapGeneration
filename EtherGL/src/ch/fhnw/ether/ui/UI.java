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

package ch.fhnw.ether.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import ch.fhnw.ether.geom.RGBA;
import ch.fhnw.ether.model.TextGeometry;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.shader.Triangles;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.UpdateRequest;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

public final class UI {
    private final IScene scene;
    private final TextGeometry text = new TextGeometry(0, 0, 512, 512);
    private final IRenderable renderable;
    private final UpdateRequest updater = new UpdateRequest();

    private final List<IWidget> widgets = new ArrayList<>();
    private String message;

    public UI(IScene scene) {
        this.scene = scene;
        renderable = scene.getRenderer().createRenderable(IRenderer.Pass.SCREEN_SPACE_OVERLAY, EnumSet.of(IRenderer.Flag.INTERACTIVE_VIEW_ONLY), new Triangles(RGBA.WHITE, text.getTexture()), text);
        text.setRenderable(renderable);
        enable();
    }

    public void enable() {
    	scene.getRenderer().addRenderables(renderable);
        requestUpdate();
    }

    public void disable() {
    	scene.getRenderer().removeRenderables(renderable);
    }

    public void update() {
        if (!updater.needsUpdate())
            return;

        text.clear();

        for (IWidget widget : widgets) {
        	widget.draw(text);
        }

        if (message != null)
            text.drawString(message, 0, text.getHeight() - TextGeometry.FONT.getSize());
 
        renderable.requestUpdate();
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
        widgets.forEach(this::addWidget);
    }

    public void setMessage(String message) {
        if (this.message != null && this.message.equals(message))
            return;
        this.message = message;
        requestUpdate();
    }

    public int getX() {
        return text.getX();
    }

    public int getY() {
        return text.getX();
    }

    public int getWidth() {
        return text.getWidth();
    }

    public int getHeight() {
        return text.getHeight();
    }
    
    public void requestUpdate() {
        updater.requestUpdate();
        scene.repaintViews();
    }
    
    
    // key listener

    public boolean keyPressed(KeyEvent e, IView view) {
        if (view.getViewType() == IView.ViewType.INTERACTIVE_VIEW) {
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
        if (view.getViewType() == IView.ViewType.INTERACTIVE_VIEW) {
            for (IWidget widget : getWidgets()) {
            	if (widget.mousePressed(e, view))
            		return true;
            }
        }
        return false;
    }
    
    public boolean mouseReleased(MouseEvent e, IView view) {
        if (view.getViewType() == IView.ViewType.INTERACTIVE_VIEW) {
            for (IWidget widget : getWidgets()) {
            	if (widget.mouseReleased(e, view))
            		return true;
            }
        }
        return false;
    }


    // mouse motion listener

    public void mouseMoved(MouseEvent e, IView view) {
        if (view.getViewType() == IView.ViewType.INTERACTIVE_VIEW) {
            for (IWidget widget : getWidgets()) {
                if (widget.hit(e.getX(), e.getY(), view)) {
                    String message = widget.getHelp();
                    setMessage(message);
                    return;
                }
            }
        }
    }

    public boolean mouseDragged(MouseEvent e, IView view) {
        if (view.getViewType() == IView.ViewType.INTERACTIVE_VIEW) {
            for (IWidget widget : getWidgets()) {
            	if (widget.mouseDragged(e, view))
            		return true;
            }
        }
        return false;
    }
}
