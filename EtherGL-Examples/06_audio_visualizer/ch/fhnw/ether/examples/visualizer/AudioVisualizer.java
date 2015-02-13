package ch.fhnw.ether.examples.visualizer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.fhnw.ether.audio.AbstractAudioSource;
import ch.fhnw.ether.audio.AudioUtilities.Window;
import ch.fhnw.ether.audio.AudioUtilities;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.InvFFT;
import ch.fhnw.ether.audio.JavaSoundTarget;
import ch.fhnw.ether.audio.SilenceAudioSource;
import ch.fhnw.ether.audio.FFT;
import ch.fhnw.ether.audio.SinGen;
import ch.fhnw.ether.audio.URLAudioSource;
import ch.fhnw.ether.examples.visualizer.Bands.Div;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.RenderProgram;
import ch.fhnw.ether.ui.ParameterWindow;
import ch.fhnw.util.TextUtilities;

public class AudioVisualizer {
	private static final int N_CUBES = 20;
	
	public static void main(String[] args) throws RenderCommandException, MalformedURLException, IOException {
		AbstractAudioSource<?>            src = new URLAudioSource(new File(args[0]).toURI().toURL());
		//AbstractAudioSource<?>            src   = new SilenceAudioSource(1, 44100, 16);
		SinGen                            sin   = new SinGen(0);
		DCRemove                          dcrmv = new DCRemove();
		AutoGain                          gain  = new AutoGain();
		FFT                               fft  = new FFT(25, Window.HANN);
		Bands                             bands = new Bands(fft, 80, 10000, N_CUBES, Div.LOGARITHMIC);
		PitchDetect                       pitch = new PitchDetect(fft, 2);
		InvFFT                            ifft  = new InvFFT(fft);
		Robotizer                         robo  = new Robotizer(fft);

		final JavaSoundTarget audioOut = new JavaSoundTarget();

		final Canvas c = new Canvas() {
			private static final long serialVersionUID = 6220722420324801742L;

			@Override
			public void paint(Graphics g) {
				g.clearRect(0, 0, getWidth(), getHeight());
				int w = getWidth() / N_CUBES;
				for(int i = 0; i < N_CUBES; i++) {
					int h = (int) (bands.power(audioOut, i) * getHeight());
					g.fillRect(i * w, getHeight() - h, w, h);
				}
				g.drawString(TextUtilities.toString(pitch.pitch(audioOut)), 0, 20);
				/*
					g.setColor(Color.RED);
					int count = 0;
					for(float f : pitch.pitch(audioOut[0])) {
						if(f < 200) continue;
						int x = (int) ((f * getWidth()) / 10000f);
						g.drawLine(x, 0, x, getHeight());
						if(++count > 2) break;
					}
				 */
				repaint(20);
			}
		};

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.add(c);
				frame.setSize(1000, 200);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});

		/*
		Scene    scene = new Scene();

		scene.add3DObject(new Mesh(Mat4.ID, new CubeGeoemtry(), new ColorMaterial(RGBA.WHITE)));

		for(int i = 0; i < N_CUBES; i++) {
			scene.add3DObject(new Mesh(()->{
				return Mat4.multiply(Mat4.scale(0.8f, 0.8f, spec.get(i)), Mat4.translate(i - (N_CUBES / 2f), 0, 0));
			},
			new CubeGeometry(),
			new ColorMaterial(()->{
				return new RGBA(spec.get(i),spec.get(i),spec.get(i),1);
				};
			}));
		}
		 */

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
