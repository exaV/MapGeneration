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

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import ch.ethz.ether.geom.BoundingVolume;
import ch.ethz.ether.gl.VBO;
import ch.ethz.ether.model.IModel;
import ch.ethz.ether.scene.NavigationGrid;
import ch.ethz.ether.view.IView;

/**
 * Simple and straightforward forward renderer.
 * @author radar
 * 
 */
public class ForwardRenderer implements IRenderer {
	private class RenderGroup {
		//private Shader shader;
		//private VAO vao;
		private VBO vbo; // replace with VAO
		private IGeometryGroup geometry;
	}
	
	// XXX work in progress
	private static final float[] MODEL_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };
	
	private final List<RenderGroup> groupShaded = new ArrayList<RenderGroup>();
	private final List<RenderGroup> groupTransparent = new ArrayList<RenderGroup>();
	private final List<RenderGroup> groupOverlay = new ArrayList<RenderGroup>();
	private final List<RenderGroup> groupSSOverlay = new ArrayList<RenderGroup>();

	@Override
	public void renderModel(GL2 gl, IModel model, IView view) {
		BoundingVolume bounds = model.getBounds();
		
		updateRenderGroups(model);

		/*
		
		
		
		// enable depth test
		gl.glEnable(GL2.GL_DEPTH_TEST);

		// render ground plane (XXX FIXME: move to model as rendergroup)
		gl.glColor4fv(NavigationGrid.GRID_COLOR, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(bounds.getMinX(), bounds.getMinY(), -0.001);
		gl.glVertex3d(bounds.getMaxX(), bounds.getMinY(), -0.001);
		gl.glVertex3d(bounds.getMaxX(), bounds.getMaxY(), -0.001);
		gl.glVertex3d(bounds.getMinX(), bounds.getMaxY(), -0.001);
		gl.glEnd();

		// render geometry
		renderGeometry(gl, group);
		
		// cleanup
		gl.glDisable(GL2.GL_DEPTH_TEST);
		*/
	}
	
	private void renderGeometry(GL2 gl, IGeometryGroup geometry) {
		//gl.glColor3fv(MODEL_COLOR, 0);
		//drawTriangles(gl, geometry.getFaces(), geometry.getNormals(), geometry.getColors());
	}
	
	private void updateRenderGroups(IModel model) {
		GeometryGroups geometry = model.getGeometryGroups();
		
		// check if list of groups changed, and create / dispose VBOs accordingly
		if (geometry.needsUpdate()) {
			
		}
		
		// update VBOs for each group
		for (RenderGroup g : groupShaded) {
			if (g.geometry.needsUpdate())
				updateBuffers(g.vbo, g.geometry);
		}
	}
	
	private void updateBuffers(VBO vbo, IGeometryGroup group) {
	}
}
