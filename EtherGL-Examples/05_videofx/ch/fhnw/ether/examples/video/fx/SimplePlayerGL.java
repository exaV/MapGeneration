package ch.fhnw.ether.examples.video.fx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.ether.video.CameraInfo;
import ch.fhnw.ether.video.CameraSource;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.VideoTrackFactory;
import ch.fhnw.ether.view.IView.Config;
import ch.fhnw.ether.view.IView.ViewType;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Vec3;

public class SimplePlayerGL implements Runnable {
	private static final double SEC2MS = 1000.0;
	private static final float  SCALE  = 2.5f;

	private final IVideoFrameSource[] sources;
	private final Texture             texture = new Texture(new RGB8Frame(16, 16));
	private       DefaultView         view;

	public SimplePlayerGL(IVideoFrameSource[] sources) {
		this.sources = sources;
	}

	private static void sleep(long ms) {
		try {Thread.sleep(ms);} catch(InterruptedException e) {}
	}

	@Override
	public void run() {
		if(SwingUtilities.isEventDispatchThread()) {
			IController controller = new DefaultController();

			view = new DefaultView(controller, 0, 10, 512, 512, new Config(ViewType.INTERACTIVE_VIEW, 2), "SimplePlayerGL", new Camera());
			controller.addView(view);

			IScene scene = new DefaultScene(controller);
			controller.setScene(scene);

			DefaultGeometry g = DefaultGeometry.createVM(Primitive.TRIANGLES, MeshLibrary.DEFAULT_QUAD_VERTICES, MeshLibrary.DEFAULT_QUAD_TEX_COORDS); 

			g.setRotation(new Vec3(90, 0, 0));
			g.setScale(new Vec3(SCALE * sources[0].getWidth() / sources[0].getHeight(), SCALE, SCALE));

			scene.add3DObject(new DefaultMesh(new ColorMapMaterial(texture), g, Queue.TRANSPARENCY));

			new Thread(this).start();
		} else {
			IVideoFrameSource fx = new RGBGain(sources[0]);
			//IVideoFrameSource fx = new ChromaKey(sources[1], sources[0]);
			//IVideoFrameSource fx = new AnalogTVFX(sources[0]);
			//IVideoFrameSource fx = new BandPass(sources[0]);
			//IVideoFrameSource fx = new Convolution(sources[0]);
			//IVideoFrameSource fx = new MotionBlur(sources[0]);
			new ParamWindow(fx);
			texture.setData(fx);
			for(;;) {
				long before = System.currentTimeMillis();
				texture.update();
				view.repaint();
				long elapsed = System.currentTimeMillis() - before;
				long sleep   = (long) ((SEC2MS / sources[0].getFrameRate()) - elapsed);
				if(sleep > 0)
					sleep(sleep);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		IVideoFrameSource[] sources = null;
		if(args.length == 0) {
			sources =  new IVideoFrameSource[] {CameraSource.create(CameraInfo.getInfos()[0])};
		}
		else {
			sources = new IVideoFrameSource[args.length];
			int idx = 0;
			for(String arg : args) {
				try {
					sources[idx] = VideoTrackFactory.createSequentialTrack(new URL(arg));
				} catch(MalformedURLException e) {
					sources[idx] = VideoTrackFactory.createSequentialTrack(new File(arg).toURI().toURL());
				}
				idx++;
			}
		}
		SwingUtilities.invokeLater(new SimplePlayerGL(sources));
	}
}
