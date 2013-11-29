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
package ch.ethz.ether.render;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import ch.ethz.ether.geom.BoundingVolume;
import ch.ethz.ether.gl.Matrix4x4;
import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.scene.NavigationTool;
import ch.ethz.ether.view.IView;

/**
 * Simple and straightforward forward renderer.
 * @author radar
 * 
 */
public class ForwardRenderer implements IRenderer {
	private final float[] projectionMatrix2D = Matrix4x4.identity();
	private final float[] modelviewMatrix2D = Matrix4x4.identity();
	
	@Override
	public void render(GL2 gl, IView view) {
		BoundingVolume bounds = view.getScene().getModel().getBounds();
		
		RenderGroups groups = (RenderGroups)GROUPS;
		groups.update(gl);

		//---- 1. DEPTH PASS (DEPTH WRITE&TEST ENABLED, BLEND OFF)
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadMatrixf(view.getCamera().getProjectionMatrix(), 0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadMatrixf(view.getCamera().getModelviewMatrix(), 0);
		
		// render ground plane (XXX FIXME: move to model as geometry group)
		gl.glColor4fv(NavigationTool.GRID_COLOR, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(2*bounds.getMinX(), 2*bounds.getMinY(), -0.001);
		gl.glVertex3d(2*bounds.getMaxX(), 2*bounds.getMinY(), -0.001);
		gl.glVertex3d(2*bounds.getMaxX(), 2*bounds.getMaxY(), -0.001);
		gl.glVertex3d(2*bounds.getMinX(), 2*bounds.getMaxY(), -0.001);
		gl.glEnd();
		
		groups.render(gl, view, Pass.DEPTH);
		
		
		//---- 2. TRANSPARENCY PASS (DEPTH WRITE DISABLED, DEPTH TEST ENABLED, BLEND ON)
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		groups.render(gl, view, Pass.TRANSPARENCY);
		
		
		//---- 3. OVERLAY PASS (DEPTH WRITE&TEST DISABLED, BLEND ON)
		gl.glDisable(GL.GL_DEPTH_TEST);
		groups.render(gl, view, Pass.OVERLAY);

		
		//---- 4. DEVICE SPACE OVERLAY (DEPTH WRITE&TEST DISABLED, BLEND ON)
		Matrix4x4.identity(projectionMatrix2D);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadMatrixf(projectionMatrix2D, 0);

		Matrix4x4.identity(modelviewMatrix2D);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadMatrixf(modelviewMatrix2D, 0);
		
		groups.render(gl, view, Pass.DEVICE_SPACE_OVERLAY);

		
		//---- 5. SCREEN SPACE OVERLAY (DEPTH WRITE&TEST DISABLED, BLEND ON)
		Matrix4x4.ortho(0, view.getWidth(), view.getHeight(), 0, -1, 1, projectionMatrix2D);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadMatrixf(projectionMatrix2D, 0);

		Matrix4x4.identity(modelviewMatrix2D);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadMatrixf(modelviewMatrix2D, 0);
		
		groups.render(gl, view, Pass.SCREEN_SPACE_OVERLAY);
		
		
		//---- 6. CLEANUP: RETURN TO DEFAULTS (XXX EXCEPT FOR MATRICES, WHICH WILL BE REMOVED ANYWAY)
		gl.glDisable(GL.GL_BLEND);
		gl.glDepthMask(true);
	}	
}
