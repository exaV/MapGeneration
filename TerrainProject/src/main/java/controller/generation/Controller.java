package controller.generation;

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
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import model.GraphManager;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

/**
 * ether-gl stuff
 * <p>
 * Created by P on 04.12.2015.
 */
public class Controller {
    final long seed;


    GraphManager graphManager;

    public Controller() {
        seed = System.nanoTime();
        Random rngesus = new Random(seed);

        graphManager = new GraphManager(rngesus, seed);

        init();
    }

    private void init() {
        System.out.println("seed: " + seed);

        final List<IMesh> img = graphManager.getGraph().createMapAsMesh(true, false, false, false, false, false);

        // Create controller
        IController controller = new DefaultController();
        controller.run(time -> {
            IView view = new DefaultView(controller, 100, 100, 800, 800, IView.INTERACTIVE_VIEW, "Map_Generation");

            // Create scene
            IScene scene = new DefaultScene(controller);
            controller.setScene(scene);

            // Create and add camera
            ICamera camera = new Camera(new Vec3(0, -5, 5), Vec3.ZERO);
            scene.add3DObject(camera);
            scene.add3DObjects(img);

            Mat4 translateToCenter = Mat4.translate(-400, 0, -400);
            img.forEach(iMesh -> iMesh.setTransform(translateToCenter));

            controller.setCamera(view, camera);

            // Add an exit button
            controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
        });
    }


}
