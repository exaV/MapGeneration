/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.ether.render.forward;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import ch.ethz.ether.geom.Mat4;
import ch.ethz.ether.gl.Program;
import ch.ethz.ether.render.AbstractRenderer;
import ch.ethz.ether.render.IRenderEntry;
import ch.ethz.ether.render.IRenderGroup;
import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.view.IView;

/**
 * Simple and straightforward forward renderer.
 *
 * @author radar
 */
public class ForwardRenderer extends AbstractRenderer {
    private final Mat4 projMatrix2D = Mat4.identityMatrix();
    private final Mat4 viewMatrix2D = Mat4.identityMatrix();

    public ForwardRenderer() {
    }

    @Override
    public void render(GL3 gl, IView view) {
        updateGroups(gl, this);

        // ---- 1. DEPTH PASS (DEPTH WRITE&TEST ENABLED, BLEND OFF)
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(1, 3);

        Mat4 projMatrix = view.getCamera().getProjMatrix();
        Mat4 viewMatrix = view.getCamera().getViewMatrix();

        renderGroups(gl, this, view, projMatrix, viewMatrix, Pass.DEPTH);

        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

        // ---- 2. TRANSPARENCY PASS (DEPTH WRITE DISABLED, DEPTH TEST ENABLED,
        // BLEND ON)
        gl.glEnable(GL.GL_BLEND);
        gl.glDepthMask(false);
        renderGroups(gl, this, view, projMatrix, viewMatrix, Pass.TRANSPARENCY);

        // ---- 3. OVERLAY PASS (DEPTH WRITE&TEST DISABLED, BLEND ON)
        gl.glDisable(GL.GL_DEPTH_TEST);
        renderGroups(gl, this, view, projMatrix, viewMatrix, Pass.OVERLAY);

        // ---- 4. DEVICE SPACE OVERLAY (DEPTH WRITE&TEST DISABLED, BLEND ON)
        projMatrix2D.identity();
        renderGroups(gl, this, view, projMatrix2D, viewMatrix2D, Pass.DEVICE_SPACE_OVERLAY);

        // ---- 5. SCREEN SPACE OVERLAY (DEPTH WRITE&TEST DISABLED, BLEND ON)
        projMatrix2D.ortho(0, view.getViewport().w, view.getViewport().h, 0, -1, 1);
        renderGroups(gl, this, view, projMatrix2D, viewMatrix2D, Pass.SCREEN_SPACE_OVERLAY);

        // ---- 6. CLEANUP: RETURN TO DEFAULTS
        gl.glDisable(GL.GL_BLEND);
        gl.glDepthMask(true);
    }

    @Override
    public IRenderEntry getEntry(GL3 gl, IRenderGroup group) {
        // TODO: error handling
        PrintStream out = System.out;
        Program program;
        IRenderEntry entry = null;
        switch (group.getType()) {
            case POINTS:
                program = Program.create(gl, ForwardRenderer.class, "glsl/point_vc_vert.glsl", "glsl/point_vc_frag.glsl", out);
                entry = new RenderEntryPointVC(program, group);
                break;
            case LINES:
            case TRIANGLES:
                program = Program.create(gl, ForwardRenderer.class, "glsl/unshaded_vct_vert.glsl", "glsl/unshaded_vct_frag.glsl", out);
                entry = new RenderEntryUnshadedVCT(program, group);
                break;
        }
        return entry;
    }
}
