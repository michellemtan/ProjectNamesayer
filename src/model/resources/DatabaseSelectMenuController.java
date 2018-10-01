package model.resources;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.DatabaseProcessor;

import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class DatabaseSelectMenuController {

    @FXML private ListView<String> dbListview;
    @FXML private Button deleteBtn;
    @FXML private Button continueBtn;
    @FXML private Button backButton;
    private Preferences addPref = Preferences.userRoot();
    private Stage progressStage;
    private TaskService service;
    private Stage window; //Main window which all scenes are in
    private Scene scene; //The database menu scene which is next scene
    private String selectedDir;

    public void addBtnPressed() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choose database folder");
        Stage dcStage = new Stage();
        File selectedDirectory = dc.showDialog(dcStage);

        if (selectedDirectory != null) {
            //Add selected file to preferences to be saved
            dbListview.getItems().add(selectedDirectory.getPath());
            addPref.put(selectedDirectory.getPath(), selectedDirectory.getPath());

            selectedDir = selectedDirectory.getPath();
        }
    }

    //Code to be run when delete directory is pressed, removes selected directory from directory listview and preferences
    public void deleteBtnPressed() {
        if(dbListview.getSelectionModel().getSelectedIndex() != -1){
            addPref.remove(dbListview.getSelectionModel().getSelectedItem());
            dbListview.getItems().remove(dbListview.getSelectionModel().getSelectedIndex());
        }
    }

    //Code to be run when continue is pressed, changes scene root to be database menu fxml file
    public void continueBtnPressed() {
        if(dbListview.getSelectionModel().getSelectedIndex() != -1){
            //The name that should be used in label for DatabaseMenu
            String dbName = dbListview.getSelectionModel().getSelectedItem();

            service.restart();
        }
    }

    //When the back button is pressed, changes scene root to start menu fxml file
    public void backButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Initialize method is called when the fxml file is loaded, this code just iterates through previously loaded
    //databases and ensures they're still in the list view.
    public void initialize() {

        String[] prefKeys = new String[0];
        try {
            prefKeys = addPref.keys();

        } catch (BackingStoreException e) {
        }

        for(String key : prefKeys) {
            if(key.startsWith("/")) {
                dbListview.getItems().add(addPref.get(key, key));
            }
        }

        checkButtonBehaviour();

        //Set database list to only select one database at a time
        dbListview.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dbListview.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                checkButtonBehaviour();
            }
        });

        //New background service for processing database
        service = new TaskService();
        service.setOnScheduled(e -> progressStage.show());
        service.setOnSucceeded(e -> {
            progressStage.hide();
            window.setScene(scene);
        });

        //Create progress bar for processing database
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(service.progressProperty());
        progressBar.setPrefSize(200, 50);

        //Create new stage for progress bar
        progressStage = new Stage();
        progressStage.setTitle("Processing Database");
        Scene scene = new Scene(new StackPane(progressBar), 400, 150);
        scene.getStylesheets().add("/model/resources/Theme.css");
        progressStage.setScene(scene);
        progressStage.setAlwaysOnTop(true);
    }


    /**
     * Class that creates/runs the task to process the database in the background
     */
    private class TaskService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws IOException {
                    //Instantiate database processor and start processing
                    DatabaseProcessor processor = new DatabaseProcessor(dbListview.getSelectionModel().getSelectedItem());
                    processor.processDB();

                    //Get scene of next menu, and initialize tree view of files
                    scene = SetUp.getInstance().databaseMenu;
                    window = (Stage) continueBtn.getScene().getWindow();
                    SetUp.getInstance().dbMenuController.initialize(dbListview.getSelectionModel().getSelectedItem());
                    return null;
                }
            };
        }
    }

    //This method determines if the buttons are disabled or not, depending on the state of the database list view
    private void checkButtonBehaviour(){
        //If the database list is empty, disable the Continue and Delete buttons so user must add a directory
        if (dbListview.getItems().isEmpty()) {
            continueBtn.setDisable(true);
            deleteBtn.setDisable(true);
        } else if (dbListview.getSelectionModel().getSelectedItems().isEmpty()){
            //If no item in the list is selected, the delete and continue buttons should be disabled
            deleteBtn.setDisable(true);
            continueBtn.setDisable(true);
        } else {
            //If the database list is not empty and an item has been selected, allow the user to delete and continue
            deleteBtn.setDisable(false);
            continueBtn.setDisable(false);
        }
    }
}
