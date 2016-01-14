package ch.fhnw.comgr.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

import ch.fhnw.comgr.Main;

public class TerrainGeneratorController {

    @FXML
    private TextField resolution;
    @FXML
    private TextField randomseed;
    @FXML
    private TextField heightfactor;
    @FXML
    private Button export;
    @FXML
    private Button quit;
    @FXML
    private Button regenerate;
    @FXML
    private CheckBox drawBiomes;
    @FXML
    private CheckBox drawRivers;
    @FXML
    private CheckBox drawSites;
    @FXML
    private CheckBox drawCorners;
    @FXML
    private CheckBox drawDelaunay;
    @FXML
    private CheckBox drawVoronoi;
    @FXML
    private CheckBox randomizeSeed;
    
    // Reference to the main application.
    private Main mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public TerrainGeneratorController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    }

    @FXML
    private void quitAction() {
    	System.out.println("Quit!");
    	System.exit(0);
    }
    
    @FXML
    private void exportAction() {
    	System.out.println("Export");
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save OBJ");

        File file = fileChooser.showSaveDialog(mainApp.getStage());
        if (file != null) {
            this.mainApp.export(file);
        }
    }
    
    @FXML
    private void regenerate() {
    	System.out.println("Regenerate");
    	long seed;
    	if (randomizeSeed.isSelected()){
    		seed = System.nanoTime();
    		randomseed.setText(Long.toString(seed, 10));
    	}
    	else {
    		seed = Long.parseLong(randomseed.getText());
    	}
    	this.mainApp.regenerate(Integer.parseInt(resolution.getText()),
    							seed,
								Integer.parseInt(heightfactor.getText()),
    							drawBiomes.isSelected(),
    							drawRivers.isSelected(),
    							drawSites.isSelected(),
    							drawCorners.isSelected(),
    							drawDelaunay.isSelected(),
    							drawVoronoi.isSelected());
    }
    
    @FXML
    private void setRandomizeSeed() {
    	//System.out.println("test");
    	randomseed.setDisable(randomizeSeed.isSelected());
    }
    
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
//        personTable.setItems(mainApp.getPersonData());
    }
}
