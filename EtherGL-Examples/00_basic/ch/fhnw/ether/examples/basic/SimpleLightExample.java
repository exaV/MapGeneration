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
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.light.PointLight;
import ch.fhnw.ether.scene.light.SpotLight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Pass;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
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

import com.jogamp.newt.event.KeyEvent;

public final class SimpleLightExample {
	private static final String[] HELP = { 
		//@formatter:off
		"Simple Light Example", 
		"", 
		"[1] Directional Light [2] Point Light [3] Spot Light",
		"Use cursors to move light position / direction x/y axis",
		"Use q/a to move light position / direction along z axis",
		"", 
		"Use Mouse Buttons + Shift or Mouse Wheel to Navigate" 
		//@formatter:on
	};

	public static void main(String[] args) {
		new SimpleLightExample();
	}

	private static final float INC_XY = 0.25f;
	private static final float INC_Z = 0.25f;
	private static final RGB AMBIENT = RGB.BLACK;
	private static final RGB COLOR = RGB.WHITE;

	private IController controller;
	private IScene scene;
	private ILight light = new DirectionalLight(Vec3.Z, AMBIENT, COLOR);
	private IMesh lightMesh;

	public SimpleLightExample() {
		// Create controller
		controller = new DefaultController() {
			@Override
			public void keyPressed(KeyEvent e, IView view) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_1:
					scene.remove3DObject(light);
					light = new DirectionalLight(light.getPosition(), AMBIENT, COLOR);
					scene.add3DObject(light);
					break;
				case KeyEvent.VK_2:
					scene.remove3DObject(light);
					light = new PointLight(light.getPosition(), AMBIENT, COLOR, 5);
					scene.add3DObject(light);
					break;
				case KeyEvent.VK_3:
					scene.remove3DObject(light);
					light = new SpotLight(light.getPosition(), AMBIENT, COLOR, 5, Vec3.Z_NEG, 15, 0);
					scene.add3DObject(light);
					break;
				case KeyEvent.VK_UP:
					lightMesh.setPosition(lightMesh.getPosition().add(Vec3.Y.scale(INC_XY)));
					break;
				case KeyEvent.VK_DOWN:
					lightMesh.setPosition(lightMesh.getPosition().add(Vec3.Y_NEG.scale(INC_XY)));
					break;
				case KeyEvent.VK_LEFT:
					lightMesh.setPosition(lightMesh.getPosition().add(Vec3.X_NEG.scale(INC_XY)));
					break;
				case KeyEvent.VK_RIGHT:
					lightMesh.setPosition(lightMesh.getPosition().add(Vec3.X.scale(INC_XY)));
					break;
				case KeyEvent.VK_Q:
					lightMesh.setPosition(lightMesh.getPosition().add(Vec3.Z.scale(INC_Z)));
					break;
				case KeyEvent.VK_A:
					lightMesh.setPosition(lightMesh.getPosition().add(Vec3.Z_NEG.scale(INC_Z)));
					break;
				case KeyEvent.VK_H:
					printHelp(HELP);
					break;
				default:
					super.keyPressed(e, view);
				}
				light.setPosition(lightMesh.getPosition());
				repaintViews();
			};
		};

		// Create view
		Camera camera = new Camera();
		camera.setPosition(new Vec3(0, -5, 0));
		camera.setUp(new Vec3(0, 0, 1));
		IView view = new DefaultView(controller, 100, 100, 500, 500, IView.INTERACTIVE_VIEW, "Simple Sphere", camera);
		controller.addView(view);

		// Create scene and add a cube
		scene = new DefaultScene(controller);
		controller.setScene(scene);

		GeodesicSphere s = new GeodesicSphere(4);

		IMaterial solidMaterial = new ShadedMaterial(RGB.BLACK, RGB.BLUE, RGB.GRAY, RGB.WHITE, 10, 1, 1f);
		IMaterial lineMaterial = new ColorMaterial(new RGBA(1, 1, 1, 0.2f));

		Texture t = new Texture(SimpleLightExample.class.getResource("assets/earth_nasa.jpg"));
		IMaterial textureMaterial = new ShadedMaterial(RGB.BLACK, RGB.BLUE, RGB.GRAY, RGB.RED, 10, 1, 1f, t);

		IMesh solidMeshT = new DefaultMesh(solidMaterial, DefaultGeometry.createVN(Primitive.TRIANGLES, s.getTriangles(), s.getNormals()), Pass.DEPTH);
		IMesh solidMeshL = new DefaultMesh(lineMaterial, DefaultGeometry.createV(Primitive.LINES, s.getLines()), Pass.TRANSPARENCY);

		solidMeshT.getGeometry().setTranslation(Vec3.X_NEG);
		solidMeshL.getGeometry().setTranslation(Vec3.X_NEG);

		IMesh texturedMeshT = new DefaultMesh(textureMaterial, DefaultGeometry.createVNM(Primitive.TRIANGLES, s.getTriangles(), s.getNormals(),
				s.getTexCoords()), Pass.DEPTH);
		texturedMeshT.getGeometry().setTranslation(Vec3.X);

		IMesh solidCubeT = MeshLibrary.createCube(solidMaterial);
		solidCubeT.getGeometry().setScale(Vec3.ONE.scale(0.8f));

		lightMesh = new DefaultMesh(new ColorMaterial(RGBA.YELLOW), DefaultGeometry.createV(Primitive.TRIANGLES, s.getTriangles()));
		lightMesh.getGeometry().setScale(new Vec3(0.1, 0.1, 0.1));
		lightMesh.setPosition(Vec3.Z.scale(2));
		light.setPosition(lightMesh.getPosition());
		
		scene.add3DObjects(solidMeshT, solidMeshL, texturedMeshT, solidCubeT, lightMesh, light);

		// Add a second light (now that we have multiple light support...)
		scene.add3DObject(new PointLight(new Vec3(2, 0, 2), RGB.BLACK, RGB.BLUE));
		
		// Add an exit button
		controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
	}
}
