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

package ch.fhnw.ether.scene;

import java.util.List;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.geom.RGBA;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;
import ch.fhnw.ether.render.shader.builtin.Lines;
import ch.fhnw.ether.render.shader.builtin.Points;
import ch.fhnw.ether.render.shader.builtin.Triangles;
import ch.fhnw.util.CollectionUtil;
import ch.fhnw.ether.render.IRenderer;

// TODO: this class is doomed, mainly for testing right now
public class GenericMeshModel extends AbstractModel {
	private IRenderable triangles;
	private IRenderable edges;
	private IRenderable points;
	private IRenderable bounds;

	public GenericMeshModel(IController controller) {
		super(controller);
	}
	
	protected void addRenderables(IController controller) {
		if (triangles == null) {
			List<IArrayAttributeProvider> providers = CollectionUtil.filterType(IArrayAttributeProvider.class, getGeometries());
			triangles = controller.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Triangles(RGBA.WHITE, false), providers);
			edges = controller.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Lines(RGBA.WHITE), providers);
			points = controller.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Points(RGBA.GREEN, 5, 0), providers);
			bounds = controller.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Lines(RGBA.YELLOW), providers);
			controller.getRenderer().addRenderables(triangles, edges, points, bounds);			
		}
	}
}
