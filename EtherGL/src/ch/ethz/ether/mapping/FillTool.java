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
package ch.ethz.ether.mapping;

import java.util.Collections;

import ch.ethz.ether.render.AbstractRenderGroup;
import ch.ethz.ether.render.IRenderGroup;
import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.IRenderGroup.Type;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.ether.render.util.IAddOnlyFloatList;
import ch.ethz.ether.render.util.Primitives;
import ch.ethz.ether.scene.AbstractTool;
import ch.ethz.ether.scene.IScene;
import ch.ethz.ether.view.IView;

public final class FillTool extends AbstractTool {
	// @formatter:off
	private static final String[] FILL_HELP = {
		"Fill Tool for Projector Adjustment",
		"",
		"[0] Return"
	};
	// @formatter:on
	
	private IRenderGroup quads = new AbstractRenderGroup(Source.TOOL, Type.TRIANGLES, Pass.DEVICE_SPACE_OVERLAY) {
		@Override
		public void getVertices(IAddOnlyFloatList dst) {
			Primitives.addRectangle(dst, -1.0f, -1.0f, -0.1f, -0.1f);
			Primitives.addRectangle(dst, 0.1f, -1.0f, 1.0f, -0.1f);
			Primitives.addRectangle(dst, 0.1f, 0.1f, 1.0f, 1.0f);
			Primitives.addRectangle(dst, -1.0f, 0.1f, -0.1f, 1.0f);
		};
	};
	
	public FillTool(IScene scene) {
		super(scene);
	}
	
	@Override
	public void activate() {
		IRenderer.GROUPS.add(quads);
		IRenderer.GROUPS.setSource(Source.TOOL);
	}
	
	@Override
	public void deactivate() {
		IRenderer.GROUPS.remove(quads);
		IRenderer.GROUPS.setSource(null);
		getScene().enableViews(null);
	}	
	
	@Override
	public void viewChanged(IView view) {
		getScene().enableViews(Collections.singleton(view));
	}
}
