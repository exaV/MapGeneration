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

package ch.fhnw.ether.mapping.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.tool.AbstractTool;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.shader.builtin.MaterialTriangles;
import ch.fhnw.ether.render.util.Primitives;
import ch.fhnw.ether.reorg.api.IMaterial;
import ch.fhnw.ether.reorg.base.ColorMaterial;
import ch.fhnw.ether.scene.GenericMesh;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;

public final class FillTool extends AbstractTool {
	static final String[] FILL_HELP = { "Fill Tool for Projector Adjustment",
			"", "[0] Return" };

	private final IRenderable quads;

	public FillTool(IController controller) {
		super(controller);
		IMaterial m = new ColorMaterial(RGBA.YELLOW);
		quads = controller.getRenderer().createRenderable(
				Pass.DEVICE_SPACE_OVERLAY,
				new MaterialTriangles(true, false, false, false), m,
				makeQuads().getGeometry());
	}

	@Override
	public void activate() {
		getController().getRenderer().addRenderables(quads);
	}

	@Override
	public void deactivate() {
		getController().getRenderer().removeRenderables(quads);
		getController().enableViews(null);
	}

	@Override
	public void refresh(IView view) {
		getController().enableViews(Collections.singleton(view));
	}

	private static GenericMesh makeQuads() {
		GenericMesh geometry = new GenericMesh(PrimitiveType.TRIANGLE);
		List<Vec3> dst = new ArrayList<>();
		Primitives.addRectangle(dst, -1.0f, -1.0f, -0.1f, -0.1f);
		Primitives.addRectangle(dst, 0.1f, -1.0f, 1.0f, -0.1f);
		Primitives.addRectangle(dst, 0.1f, 0.1f, 1.0f, 1.0f);
		Primitives.addRectangle(dst, -1.0f, 0.1f, -0.1f, 1.0f);
		geometry.setGeometry(Vec3.toArray(dst));
		return geometry;
	}
}
