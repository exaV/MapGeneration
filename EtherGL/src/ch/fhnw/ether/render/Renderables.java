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

package ch.fhnw.ether.render;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;

import ch.fhnw.ether.view.IView;
import ch.fhnw.util.FloatList;
import ch.fhnw.util.UpdateRequest;

final class Renderables {
    private final UpdateRequest updater = new UpdateRequest();

    private final List<IRenderable> renderables = new ArrayList<>();

    private final List<IRenderable> disposed = new ArrayList<>();

    private final FloatList data = new FloatList();

    public Renderables() {
    }

    public void add(IRenderable renderable) {
        if (!renderables.contains(renderable)) {
            renderables.add(renderable);
            renderable.requestUpdate();
            updater.requestUpdate();
        }
    }

    public void add(List<IRenderable> renderables) {
        renderables.forEach(this::add);
    }

    public void remove(IRenderable renderable) {
        if (renderables.remove(renderable)) {
            updater.requestUpdate();
        }
    }

    public void remove(List<IRenderable> renderables) {
        renderables.forEach(this::remove);
    }
    
    public void dispose(IRenderable renderable) {
    	remove(renderable);
    	disposed.add(renderable);
    }

    public void dispose(List<IRenderable> renderables) {
        renderables.forEach(this::dispose);
    }

    void update(GL3 gl) {
        if (updater.needsUpdate()) {
            // update added / removed groups
            for (IRenderable renderable : disposed) {
            	renderable.dispose(gl);
            }
            disposed.clear();
        }
    }

    void render(GL3 gl, IView view, IRenderer.RenderState state, IRenderer.Pass pass) {
    	for(int i=0; i<renderables.size(); ++i) {
    		IRenderable renderable = renderables.get(i);
            if (renderable.containsFlag(IRenderer.Flag.INTERACTIVE_VIEW_ONLY) && view.getViewType() != IView.ViewType.INTERACTIVE_VIEW)
                continue;
            if (renderable.getPass() == pass) {
                renderable.update(gl, view, data);
                renderable.render(gl, view, state);
            }
        }
    }    
}
