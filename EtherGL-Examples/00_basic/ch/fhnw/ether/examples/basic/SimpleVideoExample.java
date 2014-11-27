package ch.fhnw.ether.examples.basic;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Pass;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.ether.video.ISequentialVideoTrack;
import ch.fhnw.ether.video.VideoTrackFactory;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;

public class SimpleVideoExample {
	private static final int PRELOAD_FRAMES = 300;

	public static void main(String[] args) {
		new SimpleVideoExample(args[0]);
	}

	@SuppressWarnings("unused")
	public SimpleVideoExample(String path) {
		// Create controller
		IController controller = new DefaultController();

		// Create view
		IView view = new DefaultView(controller, 100, 100, 500, 500, IView.INTERACTIVE_VIEW, "Simple Video Engine", new Camera());
		controller.addView(view);

		// Create scene and add a cube
		IScene scene = new DefaultScene(controller);
		controller.setScene(scene);

		Texture texture = new Texture();

		float[] vertices = MeshLibrary.DEFAULT_QUAD_VERTICES;
		float[] texCoords = MeshLibrary.DEFAULT_QUAD_TEX_COORDS;
		scene.add3DObject(new DefaultMesh(new ColorMapMaterial(texture), DefaultGeometry.createVM(Primitive.TRIANGLES, vertices, texCoords),
				Pass.DEVICE_SPACE_OVERLAY));

		try {
			URL url = new URL(path);
			final ISequentialVideoTrack track = VideoTrackFactory.createSequentialTrack(url);

			long delay = (long) (1000 / track.getFrameRate());

			if (PRELOAD_FRAMES > 0) {
				System.out.println("preloading frames");
				List<Frame> frames = new ArrayList<>();
				for (int i = 0; i < PRELOAD_FRAMES; ++i) {
					Frame f = track.getNextFrame();
					if (f == null)
						System.exit(0);
					frames.add(f.flipJ());
				}
				System.out.println("done.");
				new Timer().schedule(new TimerTask() {
					int frame = 0;
					@Override
					public void run() {
						texture.setData(frames.get(frame));
						view.repaint();
						frame = (frame + 1) % frames.size();
					}
				}, 1000, delay);
			} else {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						Frame frame = track.getNextFrame();
						if (frame == null)
							System.exit(0);
						texture.setData(frame.flipJ());
						view.repaint();
					}
				}, 1000, delay);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}
}