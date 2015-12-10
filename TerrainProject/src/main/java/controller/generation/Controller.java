package controller.generation;

import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.formats.obj.ObjWriter;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import model.GraphManager;
import model.GraphToMeshConverter;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Random;

/**
 * ether-gl stuff
 * <p>
 * Created by P on 04.12.2015.
 */
public class Controller {
    final long seed;
    //final long seed = 177470702879216; //nice value, for debugging :)

    List<IMesh> world = null;
    IScene scene;

    int resolution = 15000;


    GraphManager graphManager;

    public Controller() {
        seed = System.nanoTime();
        init();
    }

    private void init() {
        System.out.println("seed: " + seed);

        // Create controller
        IController controller = new DefaultController();
        controller.run(time -> {
            IView view = new DefaultView(controller, 100, 100, 800, 800, IView.INTERACTIVE_VIEW, "Map_Generation");

            // Create scene
            scene = new DefaultScene(controller);
            controller.setScene(scene);

            // Create and add camera
            ICamera camera = new Camera(new Vec3(0, -5, 5), Vec3.ZERO);
            scene.add3DObject(camera);
            generateGraph();
            ILight light0 = new DirectionalLight(new Vec3(0, -1f, 0), RGB.GRAY, RGB.WHITE);

            scene.add3DObject(light0);


            controller.setCamera(view, camera);

            // Add an exit button
            controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
            controller.getUI().addWidget(new Button(0, 1, "Generate", "Generate", KeyEvent.VK_G, (button, v) -> generateGraph()));
            controller.getUI().addWidget(new Button(0, 2, "changeResolution", String.valueOf(resolution), KeyEvent.VK_R, (button, v) -> resolutionSteps()));
            controller.getUI().addWidget(new Button(0, 3, "export", "export", KeyEvent.VK_S, (button, v) -> saveObj()));
        });
    }

    private void generateGraph() {
        if (world != null) {
            scene.remove3DObjects(world);
        }

        long seed = System.nanoTime();
        Random rngesus = new Random(seed);
        graphManager = new GraphManager(rngesus, seed, resolution);
        world = GraphToMeshConverter.createMapAsMesh(graphManager.getGraph(), true, false, false, false, false, false);

        scene.add3DObjects(world);
        Mat4 translateToCenter = Mat4.translate(-400, 0, -400);
        world.forEach(iMesh -> iMesh.setTransform(translateToCenter));
    }

    private void resolutionSteps() {
        switch (resolution) {
            case 15000:
                resolution = 45000;
                break;
            case 45000:
                resolution = 150000;
                break;
            case 150000:
                resolution = 15000;
                break;
        }

        System.out.println("set resolution to" + resolution);
    }

    private boolean saveObj() {
        try {
            ObjWriter out = new ObjWriter(new File("ModelSaves_" + Instant.now().toString().replace("-", "_").replace(":", "_").replace(".", "_")));
            world.forEach(out::addMesh);
            out.write();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void generateGraphInSeperateThread() {
//        Future<List<IMesh>> loadedMeshes = new Future<List<IMesh>>().
//
//
//
//
//        if(world!= null){
//            scene.remove3DObjects(world);
//        }
//
//
//
//        long seed = System.nanoTime();
//        Random rngesus = new Random(seed);
//        graphManager = new GraphManager(rngesus,seed,resolution);
//        world = GraphToMeshConverter.createMapAsMesh(graphManager.getGraph(), true, false, false, false, false, false);
//
//        scene.add3DObjects(world);
//        Mat4 translateToCenter = Mat4.translate(-400, 0, -400);
//        world.forEach(iMesh -> iMesh.setTransform(translateToCenter));
    }
}
