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

import java.util.Timer;
import java.util.TimerTask;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;

public final class SimpleAnimationExample {

	public static void main(String[] args) {
		new SimpleAnimationExample();
	}

	// Let's generate a colored triangle
	static IMesh makeColoredTriangle() {
		IAttribute[] attribs = { IMaterial.POSITION_ARRAY, IMaterial.COLOR_ARRAY };
		float[] position = { 0f, 0, 0, 0, 0, 0.5f, 0.5f, 0, 0.5f };
		float[] color = { 1, 0.1f, 0.1f, 1, 0.1f, 1, 0.1f, 1, 0, 0, 1, 1 };
		float[][] data = { position, color };

		DefaultGeometry g = new DefaultGeometry(Primitive.TRIANGLES, attribs, data);

		g.setOrigin(new Vec3(0, 0, 0.25));
		g.setTranslation(new Vec3(0, 0, 0.5f));

		return new DefaultMesh(new ColorMaterial(RGBA.WHITE), g);
	}

	public SimpleAnimationExample() {
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

		// Animate (Using event timer)
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			private int c = 0;

			@Override
			public void run() {

				// make some heavy animation calculation
				c += 4;
				if (c >= 360)
					c = 0;
				float f = 0.4f + 0.6f * (float) (Math.sin(Math.toRadians(c)) * 0.5 + 1);

				// apply changes to geometry
				mesh.getGeometry().setScale(new Vec3(f, f, f));
				mesh.getGeometry().modify(1, (String id, float[] colors) -> {
					for (int i = 0; i < colors.length; ++i) {
						if (i % 4 == 3)
							continue;
						colors[i] -= 0.2f * (1 - f);
						if (colors[i + 0] <= 0)
							colors[i + 0] = 1;
					}
				});
				
				// update view, because we have no fix rendering loop but event-based rendering
				if (view != null)
					view.repaint();
			}
		}, 1000, 50);
	}
}
