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

import java.util.EnumSet;

import ch.ethz.util.IAddOnlyFloatList;
import ch.ethz.util.UpdateRequest;

public abstract class AbstractRenderGroup implements IRenderGroup {
    private final UpdateRequest geometryUpdater = new UpdateRequest();
    private final UpdateRequest textureUpdater = new UpdateRequest();
    private final Source source;
    private final Type type;
    private Pass pass;
    private EnumSet<Flag> flags;

    protected AbstractRenderGroup(Source source, Type type) {
        this(source, type, Pass.DEPTH);
    }

    protected AbstractRenderGroup(Source source, Type type, Pass pass) {
        this(source, type, pass, EnumSet.noneOf(Flag.class));
    }

    protected AbstractRenderGroup(Source source, Type type, Pass pass, EnumSet<Flag> flags) {
        this.source = source;
        this.type = type;
        this.pass = pass;
        this.flags = flags;
    }

    @Override
    public final void requestUpdate() {
        geometryUpdater.requestUpdate();
    }

    @Override
    public final boolean needsUpdate() {
        return geometryUpdater.needsUpdate();
    }

    @Override
    public final Source getSource() {
        return source;
    }

    @Override
    public final Type getType() {
        return type;
    }

    @Override
    public Pass getPass() {
        return pass;
    }

    @Override
    public final void setPass(Pass pass) {
        this.pass = pass;
        requestUpdate();
    }

    public final EnumSet<Flag> getFlags() {
        return flags.clone();
    }

    public final void addFlag(Flag flag) {
        if (flags.add(flag))
            requestUpdate();
    }

    public final void removeFlag(Flag flag) {
        if (flags.remove(flag))
            requestUpdate();
    }

    public final void setFlags(EnumSet<Flag> flags) {
        this.flags = flags.clone();
        requestUpdate();
    }

    @Override
    public final boolean containsFlag(Flag flag) {
        return flags.contains(flag);
    }

    // all operations are no-op
    @Override
    public void getVertices(IAddOnlyFloatList dst) {
    }

    @Override
    public void getNormals(IAddOnlyFloatList dst) {
    }

    @Override
    public void getColors(IAddOnlyFloatList dst) {
    }

    @Override
    public void getTexCoords(IAddOnlyFloatList dst) {
    }

    @Override
    public float[] getColor() {
        return DEFAULT_COLOR;
    }

    @Override
    public float getPointSize() {
        return 1;
    }

    @Override
    public float getLineWidth() {
        return 1;
    }

    @Override
    public final void requestTextureUpdate() {
        textureUpdater.requestUpdate();
    }

    @Override
    public final boolean needsTextureUpdate() {
        return textureUpdater.needsUpdate();
    }

    @Override
    public ITextureData getTextureData() {
        return null;
    }
}
