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

package ch.fhnw.ether.examples.basic;

import java.util.Collections;
import java.util.EnumSet;

import ch.fhnw.ether.camera.Camera;
import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.controller.AbstractController;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader.ShaderInput;
import ch.fhnw.ether.scene.AbstractScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.mesh.GenericMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.VertexGeometry;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.AbstractView;

public final class OwnGeometry {
	
	//This is our own scene. Has its own Shader and own mesh.
	private static class CoolScene extends AbstractScene {
		
		private IShader s = new MaterialShader(EnumSet.of(ShaderInput.VERTEX_COLOR));
		private IMesh mesh = makeColoredTriangle();

		public CoolScene(ICamera camera) {
			super(camera);
		}

		@Override
		public void setRenderer(IRenderer renderer) {
			IRenderable r = renderer.createRenderable(Pass.DEPTH, s, mesh.getMaterial(), Collections.singletonList(mesh.getGeometry()));
			renderer.addRenderables(r);
		}
		
	}
	
	// does anybody know why we need  a "main"-procedure even though we use OOP?
	public static void main(String[] args) {
		new OwnGeometry();
	}
	
	
	// Let's generate a colored triangle
	static IMesh makeColoredTriangle() {
		float[] position = {0f,0,0, 0,0,0.5f, 0.5f,0,0.5f};
		float[] color = {1,0,0,1, 0,1,0,1, 0,0,1,1};
		float[][] data = {position, color};
		IArrayAttribute[] attribs = {new PositionArray(), new ColorArray()};
		
		VertexGeometry g = new VertexGeometry(data, attribs, PrimitiveType.TRIANGLE);
		
		return new GenericMesh(g, null);
	}
	
	
	//Setup the whole thing
	public OwnGeometry() {
		
		// As always, make first a controller
		AbstractController controller = new AbstractController(){};
		
		
		// And now the default view
		ICamera camera = new Camera();
		AbstractView view = new AbstractView(controller, 100, 100, 500, 500, IView.ViewType.INTERACTIVE_VIEW, "Test", camera);
		
		
		// Use our own scene
		IScene scene = new CoolScene(camera);		
		
		
		// Setup MVC
		controller.addView(view);
		controller.setScene(scene);
	}
}
