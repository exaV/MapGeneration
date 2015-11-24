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
 */

package ch.fhnw.ether.examples.video.fx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.media.RenderProgram;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.ui.ParameterWindow;
import ch.fhnw.ether.video.AbstractVideoSource;
import ch.fhnw.ether.video.CameraInfo;
import ch.fhnw.ether.video.CameraSource;
import ch.fhnw.ether.video.ColorMapMaterialTarget;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.URLVideoSource;
import ch.fhnw.ether.view.IView.Config;
import ch.fhnw.ether.view.IView.ViewType;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.Log;
import ch.fhnw.util.math.Mat4;

public class SimplePlayerGL {
	private static final float  SCALE  = 2.2f;
	private static final Log    log    = Log.create();
	
	public SimplePlayerGL(AbstractVideoSource source) {
		final IController            controller = new DefaultController();
		final ColorMapMaterialTarget target     = new ColorMapMaterialTarget(new ColorMapMaterial(), controller, true); 
		controller.run(time -> {
			new DefaultView(controller, 0, 10, 1024, 512, new Config(ViewType.INTERACTIVE_VIEW, 2), "SimplePlayerGL");

			IScene scene = new DefaultScene(controller);
			controller.setScene(scene);

			DefaultGeometry g = DefaultGeometry.createVM(Primitive.TRIANGLES, MeshUtilities.DEFAULT_QUAD_TRIANGLES, MeshUtilities.DEFAULT_QUAD_TEX_COORDS); 
			IMesh mesh = new DefaultMesh(target.getMaterial(), g, Queue.TRANSPARENCY);
			mesh.setTransform(Mat4.trs(0, 0, 0, 90, 0, 0, SCALE * source.getWidth() / source.getHeight(), SCALE, SCALE));			
			scene.add3DObject(mesh);

			try {
				RenderProgram<IVideoRenderTarget> video = new RenderProgram<>(source, new RGBGain(), new Convolution()); 
				//RenderProgram<IAudioRenderTarget> audio = new RenderProgram<>(source, new AudioGain()); 
				new ParameterWindow(video);
				target.useProgram(video);
				target.start();
			} catch(Throwable t) {
				log.severe(t);
			}
		});
	}

	public static void main(String[] args) throws IOException {
		AbstractVideoSource video;
		if(args.length == 0)
			video =  CameraSource.create(CameraInfo.getInfos()[0]);
		else {
			try {
				video = new URLVideoSource(new URL(args[0]));
			} catch(MalformedURLException e) {
				video = new URLVideoSource(new File(args[0]).toURI().toURL());
			}
		}
		new SimplePlayerGL(video);
	}
}
