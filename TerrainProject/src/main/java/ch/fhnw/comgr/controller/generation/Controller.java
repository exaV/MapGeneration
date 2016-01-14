package ch.fhnw.comgr.controller.generation;

import ch.fhnw.comgr.model.GraphManager;
import ch.fhnw.comgr.model.GraphToMeshConverter;
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
    long seed;
    //final long seed = 177470702879216; //nice value, for debugging :)

    List<IMesh> world = null;
    IScene scene;

    int resolution = 15000;

    int heightfactor = 150;
    
    boolean drawBiomes = true;
    boolean drawRivers = false;
    boolean drawSites = false;
    boolean drawCorners = false;
    boolean drawDelaunay = false;
    boolean drawVoronoi = false;

    GraphManager graphManager;

	public IController ethergl_controller = new DefaultController();

    public Controller() {
        seed = System.nanoTime();
        init();
    }

    private void init() {
        //System.out.println("seed: " + seed);

        System.out.println("Get the whole EtherGL stuff started.");
        ethergl_controller.run(time -> {
        	System.out.println("Start EtherGL");
            IView view = new DefaultView(ethergl_controller, 100, 100, 1024, 768, IView.INTERACTIVE_VIEW, "Terrain Generator 1.0");

            // Create scene
            System.out.println("Create new scene");
            scene = new DefaultScene(ethergl_controller);
            ethergl_controller.setScene(scene);

            // Create and add camera
            System.out.println("Create camera");
            ICamera camera = new Camera(new Vec3(10, 10, 4), Vec3.ZERO);
            scene.add3DObject(camera);
            
            //generateGraph();
            System.out.println("Set lights and camera");
            ILight light0 = new DirectionalLight(new Vec3(0, -1f, 0), RGB.GRAY, RGB.WHITE);

            scene.add3DObject(light0);


            ethergl_controller.setCamera(view, camera);
            
            // Add the GUI
            //controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
            //controller.getUI().addWidget(new Button(0, 1, "Generate", "Generate", KeyEvent.VK_G, (button, v) -> generateGraph()));
            //controller.getUI().addWidget(new Button(0, 2, "changeResolution", String.valueOf(resolution), KeyEvent.VK_R, (button, v) -> resolutionSteps()));
            //controller.getUI().addWidget(new Button(0, 3, "export", "export", KeyEvent.VK_S, (button, v) -> saveObj()));
        });
    }

    public void setSettings(int resolution, 
    						long randomseed, 
    						int heightfactor, 
    						boolean drawBiomes, 
    						boolean drawRivers, 
    						boolean drawSites, 
    						boolean drawCorners, 
    						boolean drawDelaunay, 
    						boolean drawVoronoi){
    	
    	this.seed = randomseed;
    	this.resolution = resolution;
    	this.heightfactor = heightfactor;
    	this.drawBiomes = drawBiomes;
    	this.drawRivers = drawRivers;
    	this.drawSites = drawSites;
    	this.drawCorners = drawCorners;
    	this.drawDelaunay = drawDelaunay;
    	this.drawVoronoi = drawVoronoi;
    }
    
    public void generateGraph() {
        if (world != null) {
            scene.remove3DObjects(world);
        }

        //long seed = System.nanoTime();
        Random rngesus = new Random(seed);
        graphManager = new GraphManager(rngesus, seed, resolution);

        //world = GraphToMeshConverter.createMapAsMesh(graphManager.getGraph(), true,       false,      false,     false,       false,        false,       150);
        world = GraphToMeshConverter.createMapAsMesh(graphManager.getGraph(), drawBiomes, drawRivers, drawSites, drawCorners, drawDelaunay, drawVoronoi, heightfactor);

        scene.add3DObjects(world);
        
        Mat4 translateToCenter = Mat4.translate(-5, -5, 0);
        Mat4 scaleDown = Mat4.scale(0.01f);
        Mat4 combinedTransformation = Mat4.multiply(translateToCenter, scaleDown);
//        Mat4 translateToCenter = Mat4.translate(-400, 0, -400);
        world.forEach(iMesh -> iMesh.setTransform(combinedTransformation));
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

    public boolean saveObj(File file) {
        try {
            ObjWriter out = new ObjWriter(file);
            world.forEach(out::addMesh);
            out.write();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveObj() {
    	return saveObj(new File("ModelSaves_" + Instant.now().toString().replace("-", "_").replace(":", "_").replace(".", "_") + ".obj"));
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
