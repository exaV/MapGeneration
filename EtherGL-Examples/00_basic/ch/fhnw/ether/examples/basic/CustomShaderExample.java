/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
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

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IAttribute;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.CustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;

public final class CustomShaderExample {

	public static class CustomShader extends AbstractShader {
		public CustomShader() {
			super(CustomShaderExample.class, "custom_shader_example.custom_shader", "custom_shader", Primitive.TRIANGLES);
			addArray(new PositionArray());
			addArray(new ColorArray());

			addUniform(new ProjMatrixUniform());
			addUniform(new ViewMatrixUniform());
		}
	}

	public static void main(String[] args) {
		new CustomShaderExample();
	}

	// Let's generate a colored triangle
	static IMesh makeColoredTriangle() {
		IAttribute[] attribs = { IMaterial.POSITION_ARRAY, IMaterial.COLOR_ARRAY };
		float[] position = { 0f, 0, 0, 0, 0, 0.5f, 0.5f, 0, 0.5f };
		float[] color = { 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1 };
		float[][] data = { position, color };

		DefaultGeometry g = new DefaultGeometry(Primitive.TRIANGLES, attribs, data);

		return new DefaultMesh(new CustomMaterial(new CustomShader()), g);
	}

	// Setup the whole thing
	public CustomShaderExample() {
		// Create controller
		IController controller = new DefaultController();

		// Create view
		ICamera camera = new Camera();
		IView view = new DefaultView(controller, 100, 100, 500, 500, IView.ViewType.INTERACTIVE_VIEW, "Test", camera);
		controller.addView(view);

		// Create scene and add triangle
		IScene scene = new DefaultScene(controller);
		controller.setScene(scene);

		IMesh mesh = makeColoredTriangle();
		scene.add3DObject(mesh);
	}
}
