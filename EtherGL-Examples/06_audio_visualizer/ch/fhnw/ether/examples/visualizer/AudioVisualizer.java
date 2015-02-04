package ch.fhnw.ether.examples.visualizer;


public class AudioVisualizer {
	/*
	private static final int N_CUBES = 5;
	
	public static void main(String[] args) {
		URLAudioSource                    track = new URLAudioSource(new File(args[0]).toURI().toURL());
		DCRemove                          dcrmv = new DCRemove();
		AutoGain                          gain  = new AutoGain();
		PowerSpectrum                     spec  = new PowerSpectrum(0, 20000, N_CUBES, LOGARITHMIC);

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
		
		RenderProgram<IAudioRenderTarget> audio = new RenderProgram<>(track, dcrmv, gain, spec);
		RenderProgram<IVideoRenderTarget> video = new RenderProgram<>();

		scene.attach(video);
		
		JavaSoundTarget audioOut = new JavaSoundTarget(track.getSampleRate());
		ViewportTarget  videoOut = new ViewportTarget();
		
		audioOut.useProgram(audio);
		videoOut.useProgram(video);
		
		videoOut.start();
		audioOut.start();
		
		Thread.sleep(5 * 60 * 1000);
		
		audioOut.stop();
		videoOut.stop();
	}
	*/
}
