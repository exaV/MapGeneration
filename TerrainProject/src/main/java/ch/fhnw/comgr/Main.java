package ch.fhnw.comgr;

import ch.fhnw.comgr.controller.generation.Controller;
import ch.fhnw.comgr.view.TerrainGeneratorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 *
 *
 * Created by P on 03.12.2015.
 */

public class Main extends Application{


    private static Controller controller;
	private AnchorPane rootLayout;
	private Stage primaryStage;

	@Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Terrain Generator 1.0");

        initRootLayout();

        //showPersonOverview();
    }
	
	public Stage getStage(){
		return primaryStage;
	}
	
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/TerrainGenerator.fxml"));
            rootLayout = (AnchorPane) loader.load();


            // Give the controller access to the main app.
            TerrainGeneratorController controller = loader.getController();
            controller.setMainApp(this);
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        controller = new Controller();
    	launch(args);
    	
    }

	public void regenerate(int resolution, 
						   long randomseed, 
						   int heightfactor, 
						   boolean drawBiomes, 
						   boolean drawRivers, 
						   boolean drawSites, 
						   boolean drawCorners, 
						   boolean drawDelaunay, 
						   boolean drawVoronoi) {
		controller.setSettings(resolution, randomseed, heightfactor, drawBiomes, drawRivers, drawSites, drawCorners, drawDelaunay, drawVoronoi);
		
		controller.ethergl_controller.run(time -> {controller.generateGraph();});
	}

	public void export(File file) {
		controller.saveObj(file);
	}

}
