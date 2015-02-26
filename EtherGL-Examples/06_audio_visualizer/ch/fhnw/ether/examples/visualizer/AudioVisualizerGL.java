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

package ch.fhnw.ether.examples.visualizer;

import java.io.IOException;
import java.net.MalformedURLException;

import ch.fhnw.ether.audio.AbstractAudioSource;
import ch.fhnw.ether.audio.AudioUtilities.Window;
import ch.fhnw.ether.audio.FFT;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.InvFFT;
import ch.fhnw.ether.audio.JavaSoundTarget;
import ch.fhnw.ether.audio.SilenceAudioSource;
import ch.fhnw.ether.audio.SinGen;
import ch.fhnw.ether.audio.fx.AutoGain;
import ch.fhnw.ether.audio.fx.Bands;
import ch.fhnw.ether.audio.fx.Bands.Div;
import ch.fhnw.ether.audio.fx.DCRemove;
import ch.fhnw.ether.audio.fx.PitchDetect;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IScheduler;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.RenderProgram;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.ui.ParameterWindow;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public class AudioVisualizerGL {
	private static final int N_CUBES = 20;
	
	public static void main(String[] args) throws RenderCommandException, MalformedURLException, IOException {
		//AbstractAudioSource<?>            src = new URLAudioSource(new File(args[0]).toURI().toURL());
		AbstractAudioSource<?>            src   = new SilenceAudioSource(1, 44100, 16);
		SinGen                            sin   = new SinGen(0);
		DCRemove                          dcrmv = new DCRemove();
		AutoGain                          gain  = new AutoGain();
		FFT                               fft   = new FFT(25, Window.HANN);
		Bands                             bands = new Bands(fft.state(), 80, 10000, N_CUBES, Div.LOGARITHMIC);
		PitchDetect                       pitch = new PitchDetect(fft.state(), 2);
		InvFFT                            ifft  = new InvFFT(fft.state());
		Robotizer                         robo  = new Robotizer(fft.state());

		final JavaSoundTarget audioOut = new JavaSoundTarget();


		IController controller = new DefaultController();
		
		Camera camera = new Camera();
		camera.setPosition(new Vec3(0, 5, 0));
		camera.setUp(new Vec3(0, 0, 1));
		IView view = new DefaultView(controller, 100, 100, 500, 500, IView.INTERACTIVE_VIEW, "Simple Cube", camera);
		controller.addView(view);

		IScene scene = new DefaultScene(controller);
		controller.setScene(scene);
		
		
		IMesh[] cubes = new IMesh[N_CUBES];
		for (int i = 0; i < N_CUBES; ++i) {
			IMesh cube = MeshLibrary.createCube();
			cube.setPosition(new Vec3(-(N_CUBES - 1)/2f + i, 0, 0));
			cubes[i] = cube;
		}
		scene.add3DObjects(cubes);
		
		controller.getScheduler().repeat(0, 1.0/60.0, new IScheduler.IAction() {
			@Override
			public boolean run(double time, double interval) {
				try {

					for (int i = 0; i < N_CUBES; ++i) {
						IMesh cube = cubes[i];
						float scale = bands.state().get(audioOut).power(i) * 10;
						cube.setTransform(Mat4.scale(0.5f, 0.5f, 0.1f + scale));
						System.out.println(scale);
						cube.requestUpdate(null);
					}
	
					// update view, because we have no fix rendering loop but event-based rendering
					if (view != null)
						view.repaint();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return true;
			}
		});
		
		RenderProgram<IAudioRenderTarget> audio = new RenderProgram<>(src, /*sin,*/ dcrmv, gain, fft, bands, pitch /*, robo, ifft*/);
		//RenderProgram<IVideoRenderTarget> video = new RenderProgram<>();

		//scene.attach(video);

		new ParameterWindow(audio);

		//ViewportTarget  videoOut = new ViewportTarget();

		audioOut.useProgram(audio);
		//videoOut.useProgram(video);

		//videoOut.start();
		audioOut.start();

		// audioOut.stop();
		//videoOut.stop();
	}
}
