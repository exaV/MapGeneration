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

package ch.ethz.ether.render.forward;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import ch.ethz.ether.geom.Mat4;
import ch.ethz.ether.gl.Program;
import ch.ethz.ether.gl.Texture;
import ch.ethz.ether.gl.VertexAttribute;
import ch.ethz.ether.render.AbstractRenderEntry;
import ch.ethz.ether.render.IRenderGroup;
import ch.ethz.ether.render.IRenderGroup.ITextureData;
import ch.ethz.ether.render.IRenderGroup.Type;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.ether.render.util.FloatList;
import ch.ethz.ether.view.IView;

public class RenderEntryUnshadedVCT extends AbstractRenderEntry {
    VertexAttribute vertices = new VertexAttribute();
    VertexAttribute colors = new VertexAttribute();
    VertexAttribute texCoords = new VertexAttribute();
    Texture texture = new Texture();

    public RenderEntryUnshadedVCT(Program program, IRenderGroup group) {
        super(program, group);
    }

    @Override
    public void dispose(GL3 gl) {
        if (vertices != null)
            vertices.dispose(gl);
        if (colors != null)
            colors.dispose(gl);
        if (texCoords != null)
            texCoords.dispose(gl);

        vertices = colors = texCoords = null;

        if (texture != null)
            texture.dispose(gl);
        texture = null;
        super.dispose(gl);
    }

    @Override
    public void update(GL3 gl, IRenderer renderer, IView view, FloatList data) {
        IRenderGroup group = getGroup();
        ITextureData texData = group.getTextureData();
        boolean needsTextureUpdate = group.needsTextureUpdate();

        if (!group.needsUpdate() && !needsTextureUpdate)
            return;

        data.clear();
        group.getVertices(data);
        vertices.load(gl, data.buffer());
        if (vertices.isEmpty())
            return;

        data.clear();
        group.getColors(data);
        colors.load(gl, data.buffer());

        data.clear();
        group.getTexCoords(data);
        texCoords.load(gl, data.buffer());

        if (texData != null && needsTextureUpdate) {
            texture.load(gl, texData.getWidth(), texData.getHeight(), texData.getBuffer(), texData.getFormat());
        }
    }

    @Override
    public void render(GL3 gl, IRenderer renderer, IView view, Mat4 projMatrix, Mat4 viewMatrix) {
        if (vertices.isEmpty())
            return;

        Program program = getProgram();
        IRenderGroup group = getGroup();

        program.enable(gl);

        program.setUniformMat4(gl, "projMatrix", projMatrix.m);
        program.setUniformMat4(gl, "viewMatrix", viewMatrix.m);

        int verticesIndex = program.getAttributeLocation(gl, "vertexPosition");
        int colorsIndex = program.getAttributeLocation(gl, "vertexColor");
        int texCoordsIndex = program.getAttributeLocation(gl, "vertexTexCoord");

        vertices.enable(gl, 3, verticesIndex);

        if (!colors.isEmpty()) {
            program.setUniform(gl, "hasColor", true);
            colors.enable(gl, 4, colorsIndex);
        } else {
            program.setUniform(gl, "hasColor", false);
            program.setUniformVec4(gl, "color", group.getColor());
            colors.disable(gl, colorsIndex);
        }

        ITextureData texData = group.getTextureData();
        if (!texCoords.isEmpty() && texData != null) {
            texture.enable(gl);
            program.setUniform(gl, "hasTex", true);
            program.setUniformSampler(gl, "tex", 0);
            texCoords.enable(gl, 2, texCoordsIndex);
        } else {
            texture.disable(gl);
            program.setUniform(gl, "hasTex", false);
            program.setUniformSampler(gl, "tex", 0);
            texCoords.disable(gl, texCoordsIndex);
        }

        if (group.getType() == Type.LINES) {
            gl.glLineWidth(group.getLineWidth());
            gl.glDrawArrays(GL.GL_LINES, 0, vertices.size() / 3);
        } else {
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertices.size() / 3);
        }

        vertices.disable(gl, verticesIndex);
        colors.disable(gl, colorsIndex);
        texCoords.disable(gl, texCoordsIndex);

        texture.disable(gl);

        program.disable(gl);
    }
}
