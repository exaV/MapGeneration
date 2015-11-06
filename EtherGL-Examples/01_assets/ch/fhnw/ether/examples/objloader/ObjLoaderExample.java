/*
 * Copyright (c) 2014, FHNW (Simon Schubiger)
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
 *  Neither the name of FHNW nor the names of its contributors may be
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
package ch.fhnw.ether.examples.objloader;

import java.io.IOException;
import java.net.URL;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.formats.obj.OBJReader;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public class ObjLoaderExample {
	
	public static void main(String[] args) {
		new ObjLoaderExample();
	}

	public ObjLoaderExample() {

		IController controller = new ObjLoaderController();
		controller.run(time -> {
			new DefaultView(controller, 0, 10, 512, 512, IView.INTERACTIVE_VIEW, "Obj View");
	
			IScene scene = new DefaultScene(controller);
			controller.setScene(scene);
			
			scene.add3DObject(new DirectionalLight(new Vec3(0, 0, 1), RGB.BLACK, RGB.RED));
			scene.add3DObject(new DirectionalLight(new Vec3(0, 1, 0.5), RGB.BLACK, RGB.BLUE));
	
			try {
				final URL obj = ObjLoaderExample.class.getResource("fhnw.obj");
				//final URL obj = new URL("file:///Users/radar/Desktop/aventador/aventador_red.obj");
				//final URL obj = new URL("file:///Users/radar/Desktop/berlin_mitte/berlin_mitte_o2_o3/o2_small.obj");
				new OBJReader(obj).getMeshes().forEach(mesh -> {
					mesh.setTransform(Mat4.rotate(90, Vec3.X)); scene.add3DObject(mesh);
				});
				System.out.println("number of meshes: " + scene.getMeshes().size());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
