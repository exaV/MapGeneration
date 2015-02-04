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
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.ether.video.AbstractVideoSource;
import ch.fhnw.ether.video.CameraInfo;
import ch.fhnw.ether.video.CameraSource;
import ch.fhnw.ether.video.URLVideoSource;
import ch.fhnw.ether.view.IView.Config;
import ch.fhnw.ether.view.IView.ViewType;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Transform;

public class SimplePlayerGL implements Runnable {
	private static final double SEC2MS = 1000.0;
	private static final float  SCALE  = 3.5f;

	private final AbstractVideoSource<?>[] sources;
	private final Texture                  texture = new Texture(new RGB8Frame(16, 16));
	private       DefaultView              view;

	public SimplePlayerGL(AbstractVideoSource<?>[] sources) {
		this.sources = sources;
	}

	private static void sleep(long ms) {
		try {Thread.sleep(ms);} catch(InterruptedException e) {}
	}

	@Override
	public void run() {
		if(SwingUtilities.isEventDispatchThread()) {
			IController controller = new DefaultController();

			view = new DefaultView(controller, 0, 10, 1024, 512, new Config(ViewType.INTERACTIVE_VIEW, 2), "SimplePlayerGL", new Camera());
			controller.addView(view);

			IScene scene = new DefaultScene(controller);
			controller.setScene(scene);

			DefaultGeometry g = DefaultGeometry.createVM(Primitive.TRIANGLES, MeshLibrary.DEFAULT_QUAD_VERTICES, MeshLibrary.DEFAULT_QUAD_TEX_COORDS); 
			IMesh mesh = new DefaultMesh(new ColorMapMaterial(texture), g, Queue.TRANSPARENCY);
			mesh.setTransform(Transform.trs(0, 0, 0, 90, 0, 0, SCALE * sources[0].getWidth() / sources[0].getHeight(), SCALE, SCALE));			
			scene.add3DObject(mesh);

			new Thread(this).start();
		} else {
			texture.setData(sources[0]);
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
		AbstractVideoSource<?>[] sources = null;
		if(args.length == 0) {
			sources =  new AbstractVideoSource<?>[] {CameraSource.create(CameraInfo.getInfos()[0])};
		}
		else {
			sources = new AbstractVideoSource<?>[args.length];
			int idx = 0;
			for(String arg : args) {
				try {
					sources[idx] = new URLVideoSource(new URL(arg));
				} catch(MalformedURLException e) {
					sources[idx] = new URLVideoSource(new File(arg).toURI().toURL());
				}
				idx++;
			}
		}
		SwingUtilities.invokeLater(new SimplePlayerGL(sources));
	}
}
