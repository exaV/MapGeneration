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

package ch.ethz.ether.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL3;

import ch.ethz.ether.geom.Mat4;
import ch.ethz.ether.render.IRenderGroup.Flag;
import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.util.FloatList;
import ch.ethz.ether.view.IView;
import ch.ethz.ether.view.IView.ViewType;
import ch.ethz.util.UpdateRequest;

final class RenderGroups implements IRenderGroups {
    private final UpdateRequest updater = new UpdateRequest();

    private final List<IRenderGroup> added = new ArrayList<>();
    private final List<IRenderGroup> removed = new ArrayList<>();

    private final Map<IRenderGroup, IRenderEntry> groups = new HashMap<>();
    private EnumSet<Source> sources = Source.ALL_SOURCES;

    private final FloatList data = new FloatList();

    public RenderGroups() {
    }

    @Override
    public void add(IRenderGroup group) {
        removed.remove(group);
        if (!groups.containsKey(group)) {
            added.add(group);
            group.requestUpdate();
            group.requestTextureUpdate();
            updater.requestUpdate();
        }
    }

    @Override
    public void add(IRenderGroup group, IRenderGroup... groups) {
        add(group);
        for (IRenderGroup g : groups)
            add(g);
    }

    @Override
    public void remove(IRenderGroup group) {
        added.remove(group);
        if (groups.containsKey(group) & !removed.contains(group)) {
            removed.add(group);
            updater.requestUpdate();
        }
    }

    @Override
    public void remove(IRenderGroup group, IRenderGroup... groups) {
        remove(group);
        for (IRenderGroup g : groups)
            remove(g);
    }

    @Override
    public void setSource(Source source) {
        if (source != null)
            sources = EnumSet.of(source);
        else
            sources = Source.ALL_SOURCES;
    }

    void update(GL3 gl, IRenderer renderer) {
        if (updater.needsUpdate()) {
            // update added / removed groups
            for (IRenderGroup group : added) {
                groups.put(group, renderer.getEntry(gl, group));
            }
            added.clear();

            for (IRenderGroup group : removed) {
                IRenderEntry entry = groups.remove(group);
                entry.dispose(gl);
            }
            removed.clear();
        }
    }

    void render(GL3 gl, IRenderer renderer, IView view, Mat4 projMatrix, Mat4 viewMatrix, Pass pass) {
        for (IRenderEntry entry : groups.values()) {
            if (!sources.contains(entry.getGroup().getSource()))
                continue;
            if (entry.getGroup().containsFlag(Flag.INTERACTIVE_VIEW_ONLY) && view.getViewType() != ViewType.INTERACTIVE_VIEW)
                continue;
            if (entry.getGroup().getPass() == pass) {
                entry.update(gl, renderer, view, data);
                entry.render(gl, renderer, view, projMatrix, viewMatrix);
            }
        }
    }
}
