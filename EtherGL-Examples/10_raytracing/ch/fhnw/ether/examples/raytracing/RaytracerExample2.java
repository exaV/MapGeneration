/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
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
 */package ch.fhnw.ether.examples.raytracing;

import java.util.Timer;
import java.util.TimerTask;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.EventDrivenScheduler;
import ch.fhnw.ether.examples.raytracing.surface.Plane;
import ch.fhnw.ether.examples.raytracing.surface.Sphere;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.light.PointLight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;

public class RaytracerExample2 {

	public static void main(String[] args) {
		new RaytracerExample2();
	}

	public RaytracerExample2() {
		RayTracer rt = new RayTracer();

		// create controller, camera, scene and view
		IController controller = new DefaultController(new EventDrivenScheduler(), new RayTracingRenderer(rt));
		
		IScene scene = new DefaultScene(controller);
		controller.setScene(scene);

		ICamera camera = new Camera(new Vec3(0, -2, 1), Vec3.ZERO, Vec3.Z, 2.5f, 0.5f, Float.POSITIVE_INFINITY);
		IView view = new DefaultView(controller, 100, 100, 100, 100, IView.INTERACTIVE_VIEW, "Raytracing", camera);
		controller.addView(view);
		
		// setup scene;
		ILight light = new PointLight(new Vec3(-1, -1, 3), RGB.BLACK, RGB.WHITE);
		scene.add3DObject(light);
		
		Sphere sphere = new Sphere(0.5f);
		sphere.setPosition(new Vec3(0, 0, 0.5f));
		IMesh chugeli        = new RayTraceMesh(sphere);
		IMesh bode           = new RayTraceMesh(new Plane());
		IMesh waendli        = new RayTraceMesh(new Plane(Vec3.X_NEG, 4), RGBA.YELLOW);
		IMesh anders_waendli = new RayTraceMesh(new Plane(Vec3.X, 4), RGBA.RED);
		IMesh wand           = new RayTraceMesh(new Plane(Vec3.Y_NEG, 4), RGBA.GREEN);
		IMesh henderi_wand   = new RayTraceMesh(new Plane(Vec3.Y, 4), RGBA.CYAN);
		IMesh dach           = new RayTraceMesh(new Plane(Vec3.Z_NEG, 4), RGBA.BLUE);
		
		scene.add3DObject(chugeli);
		scene.add3DObject(bode);
		scene.add3DObject(waendli);
		scene.add3DObject(anders_waendli);
		scene.add3DObject(dach);
		scene.add3DObject(wand);
		scene.add3DObject(henderi_wand);		
		
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			private float n = 0;

			@Override
			public void run() {
				chugeli.setPosition(Vec3.Z.scale((float) Math.sin(n) + 0.5f));
				n += 0.1;
				if (n >= Math.PI)
					n = 0;
				view.repaint();
			}
		}, 1000, 50);
	}
}
