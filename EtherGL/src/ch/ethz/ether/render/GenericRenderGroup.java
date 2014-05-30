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

public class GenericRenderGroup extends AbstractRenderGroup {
    private float[] vertices;
    private float[] normals;
    private float[] colors;
    private float[] texCoords;
    private float pointSize;
    private float lineWidth;
    private ITextureData texData;

    public GenericRenderGroup(Source source, Type type) {
        super(source, type);
    }

    public GenericRenderGroup(Source source, Type type, Pass pass) {
        super(source, type, pass);
    }

    public GenericRenderGroup(Source source, Type type, Pass pass, EnumSet<Flag> flags) {
        super(source, type, pass, flags);
    }

    @Override
    public void getVertices(IAddOnlyFloatList dst) {
        dst.add(vertices);
    }

    @Override
    public void getNormals(IAddOnlyFloatList dst) {
        dst.add(normals);
    }

    @Override
    public void getColors(IAddOnlyFloatList dst) {
        dst.add(colors);
    }

    @Override
    public void getTexCoords(IAddOnlyFloatList dst) {
        dst.add(texCoords);
    }

    @Override
    public float getPointSize() {
        return pointSize;
    }

    @Override
    public float getLineWidth() {
        return lineWidth;
    }

    @Override
    public ITextureData getTextureData() {
        return texData;
    }

    public final void set(float[] vertices, float[] normals, float[] colors, float[] textCoords, float pointSize, float lineWidth, ITextureData texData) {
        this.vertices = vertices;
        this.normals = normals;
        this.colors = colors;
        this.texCoords = textCoords;
        this.pointSize = pointSize;
        this.lineWidth = lineWidth;
        this.texData = texData;
        requestUpdate();
    }
}
