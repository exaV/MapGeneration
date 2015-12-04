import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Vec3;
import com.hoten.delaunay.examples.TestGraphImpl;
import com.hoten.delaunay.voronoi.VoronoiGraph;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import controller.generation.VoronoiGraphFor2D;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by P on 03.12.2015.
 */

public class Main {
    public Main() throws IOException {

        int bounds = 1000;
        int numSites = 30000;
        int numLloydRelxations = 2;
        long seed = System.nanoTime();
        System.out.println("seed: " + seed);

        final List<IMesh> img = createVoronoiGraph2D(bounds, numSites, numLloydRelxations, seed).createMapAsMesh(true, false, false, false, false, false);

        // Save the Map to a file
        File file = new File(String.format("output/seed-%s-sites-%d-lloyds-%d.png", seed, numSites, numLloydRelxations));
        file.mkdirs();
        //ImageIO.write(file);

        // Create controller
        IController controller = new DefaultController();
        controller.run(time -> {
            // Create view
            //IView view = new DefaultView(controller, 100, 100, img.getWidth() + 50, img.getHeight() + 50, IView.INTERACTIVE_VIEW, "Map_Generation");
            IView view = new DefaultView(controller, 100, 100, 800, 800, IView.INTERACTIVE_VIEW, "Map_Generation");

            // Create scene
            IScene scene = new DefaultScene(controller);
            controller.setScene(scene);

            // Create and add camera
            ICamera camera = new Camera(new Vec3(0, -5, 5), Vec3.ZERO);
            scene.add3DObject(camera);
            scene.add3DObjects(img);
            controller.setCamera(view, camera);

            // Add cube


            // Add an exit button
            controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
        });
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }

    public static VoronoiGraph createVoronoiGraph2D(int bounds, int numSites, int numLloydRelaxations, long seed) {
        final Random r = new Random(seed);

        //make the intial underlying voronoi structure
        final Voronoi v = new Voronoi(numSites, bounds, bounds, r, null);

        //assemble the voronoi strucutre into a usable graph object representing a map

        return new VoronoiGraphFor2D(v, numLloydRelaxations, r);
    }
}
