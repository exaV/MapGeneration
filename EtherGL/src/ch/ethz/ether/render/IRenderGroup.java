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

import java.nio.Buffer;
import java.util.EnumSet;

import ch.ethz.util.IAddOnlyFloatList;

public interface IRenderGroup {
    static final float[] DEFAULT_COLOR = new float[]{1, 1, 1, 1};
    static final float[] DEFAULT_QUAD_TEX_COORDS = new float[]{0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1};

    interface ITextureData {
        Buffer getBuffer();

        int getWidth();

        int getHeight();

        int getFormat();
    }

    enum Source {
        MODEL, TOOL;

        public static EnumSet<Source> ALL_SOURCES = EnumSet.allOf(Source.class);
    }

    enum Type {
        POINTS, LINES, TRIANGLES,
    }

    enum Pass {
        DEPTH, TRANSPARENCY, OVERLAY, DEVICE_SPACE_OVERLAY, SCREEN_SPACE_OVERLAY
    }

    enum Flag {
        SHADED, TEXTURED, INTERACTIVE_VIEW_ONLY
    }

    void requestUpdate();

    boolean needsUpdate();

    Source getSource();

    Type getType();

    Pass getPass();

    void setPass(Pass pass);

    boolean containsFlag(Flag flags);

    void getVertices(IAddOnlyFloatList dst);

    void getNormals(IAddOnlyFloatList dst);

    void getColors(IAddOnlyFloatList dst);

    void getTexCoords(IAddOnlyFloatList dst);

    float[] getColor();

    float getPointSize();

    float getLineWidth();

    void requestTextureUpdate();

    boolean needsTextureUpdate();

    ITextureData getTextureData();
}
