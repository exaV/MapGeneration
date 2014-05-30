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

package ch.ethz.ether.examples.metrobuzz.scene;

import ch.ethz.ether.examples.metrobuzz.model.Model;
import ch.ethz.ether.model.GenericMesh;
import ch.ethz.ether.render.AbstractRenderGroup;
import ch.ethz.ether.render.IRenderGroup;
import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.IRenderGroup.Type;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.util.IAddOnlyFloatList;

public final class ModelRenderState {
	private static final float[] NETWORK_NODE_COLOR = new float[] { 0.5f, 0.5f, 0.5f, 1.0f };
	private static final float[] NETWORK_EDGE_COLOR = new float[] { 0.5f, 0.5f, 0.5f, 1.0f };
	private static final float NETWORK_POINT_SIZE = 4.0f;

	private final Model model;

	private final IRenderGroup networkNodes = new AbstractRenderGroup(Source.MODEL, Type.POINTS, Pass.DEPTH) {
		@Override
		public void getVertices(IAddOnlyFloatList dst) {
			model.getNetworkGeometry().getPointVertices(dst);
		}

		@Override
		public float[] getColor() {
			return NETWORK_NODE_COLOR;
		}

		@Override
		public float getPointSize() {
			return NETWORK_POINT_SIZE;
		}
	};

	private final IRenderGroup networkEdges = new AbstractRenderGroup(Source.MODEL, Type.LINES, Pass.DEPTH) {
		@Override
		public void getVertices(IAddOnlyFloatList dst) {
			model.getNetworkGeometry().getEdgeVertices(dst);
		}

		@Override
		public float[] getColor() {
			return NETWORK_EDGE_COLOR;
		}
	};

	private final IRenderGroup agentPaths = new AbstractRenderGroup(Source.MODEL, Type.LINES, Pass.TRANSPARENCY) {
		@Override
		public void getVertices(IAddOnlyFloatList dst) {
			for (GenericMesh agent : model.getAgentGeometries()) {
				agent.getEdgeVertices(dst);
			}
		}
		
		@Override
		public void getColors(IAddOnlyFloatList dst) {
			for (GenericMesh agent : model.getAgentGeometries()) {
				agent.getEdgeColors(dst);
			}			
		};
	};

	public ModelRenderState(Model model) {
		this.model = model;
		IRenderer.GROUPS.add(networkNodes, networkEdges, agentPaths);
	}

	public void updateNetwork() {
		networkNodes.requestUpdate();
		networkEdges.requestUpdate();
	}
	
	public void updateAgents() {
		agentPaths.requestUpdate();
	}
}
