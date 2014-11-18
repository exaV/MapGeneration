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

import java.awt.event.KeyEvent;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Pass;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.GeodesicSphere;

public final class SimpleLightExample {
	public static void main(String[] args) {
		new SimpleLightExample();
	}

	public SimpleLightExample() {
		// Create controller
		IController controller = new DefaultController();

		// Create view
		Camera camera = new Camera();
		camera.setPosition(new Vec3(0, -5, 0));
		camera.setUp(new Vec3(0, 0, 1));
		IView view = new DefaultView(controller, 100, 100, 500, 500, IView.INTERACTIVE_VIEW, "Simple Sphere", camera);
		controller.addView(view);

		// Create scene and add a cube
		IScene scene = new DefaultScene(controller);
		controller.setScene(scene);

		GeodesicSphere sphere = new GeodesicSphere(2);
		
		IMaterial shaded = new ShadedMaterial(RGB.BLACK, RGB.GRAY, RGB.RED, 100, 1000f, 1f);

		IMesh solidMeshT = new DefaultMesh(shaded, DefaultGeometry.createVN(Primitive.TRIANGLES, sphere.getTriangles(), sphere.getNormals()), Pass.DEPTH);
		IMesh solidMeshL = new DefaultMesh(new ColorMaterial(new RGBA(1, 1, 1, 0.2f)), DefaultGeometry.createV(Primitive.LINES, sphere.getLines()), Pass.TRANSPARENCY);
		
		solidMeshT.getGeometry().setTranslation(Vec3.X_NEG);
		solidMeshL.getGeometry().setTranslation(Vec3.X_NEG);
		
		Texture t = new Texture(SimpleLightExample.class.getResource("assets/earth_nasa.jpg"));
		IMesh texturedMeshT = new DefaultMesh(new ColorMapMaterial(t), DefaultGeometry.createVM(Primitive.TRIANGLES, sphere.getTriangles(), sphere.getTexCoords()), Pass.DEPTH);
		texturedMeshT.getGeometry().setTranslation(Vec3.X);
		
		scene.add3DObjects(solidMeshT, solidMeshL, texturedMeshT);

		// Add an exit button
		controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
	}
}