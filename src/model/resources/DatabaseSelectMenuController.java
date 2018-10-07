package model.resources;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.DatabaseProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class DatabaseSelectMenuController {

    @FXML private ListView<String> dbListView;
    @FXML private Button backButton;
    @FXML private Button namesBtn;
    @FXML private Button loadBtn;
    private Preferences dbPref = Preferences.userRoot();
    private TaskService service = new TaskService();
    private TaskService service2 = new TaskService();
    private Scene scene;
    private Scene scene2;
    private Stage window;
    private Stage progressStage;
    private String pathToDB;

    //TODO: WHAT IF THE USER LOADS A DIRECTORY BUT DELETES IT FROM A FILE EXPLORER
    //TODO: THEN WHEN YOU CLICK ON IT, THE DATABASE PROCESSES FOREVER

    public void initialize() {
        //Create list of keys and add to list view if valid
        String[] prefKeys = new String[0];
        try {
            prefKeys = dbPref.keys();

        } catch (BackingStoreException e) {
        }
        for(String key : prefKeys) {
            if(key.startsWith("/")) {
                dbListView.getItems().add(dbPref.get(key, key));
            }
        }

        //Disable button and enable if 1 list item selected
        namesBtn.setDisable(true);
        loadBtn.setDisable(true);
        dbListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newI) -> {
            if(dbListView.getSelectionModel().getSelectedItems().size() == 1) {
                namesBtn.setDisable(false);
                loadBtn.setDisable(false);
            } else {
                namesBtn.setDisable(true);
                loadBtn.setDisable(true);
            }
        });

        //Create progress bar for processing database
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(service.progressProperty());
        progressBar.setPrefSize(200, 50);

        //Create new stage for progress bar
        progressStage = new Stage();
        progressStage.setTitle("Processing Database");
        Scene sceneP = new Scene(new StackPane(progressBar), 400, 150);
        sceneP.getStylesheets().add("/model/resources/Theme.css");
        progressStage.setScene(sceneP);
        progressStage.setAlwaysOnTop(true);

        //Set service to show progress bar while loading
        service.setOnScheduled(e -> progressStage.show());
        //Set service to change scene upon completion
        service.setOnSucceeded(e -> {
            progressStage.hide();
            window.setScene(scene);
        });

        //Set service to show progress bar while loading
        service2.setOnScheduled(e -> progressStage.show());
        //Set service to change scene upon completion
        service2.setOnSucceeded(e -> {
            progressStage.hide();
            window.setScene(scene2);
        });

        //Set up cell factory for right-click > remove for each database
        dbListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            //Create menu item for remove
            MenuItem editItem = new MenuItem();
            editItem.textProperty().bind((Bindings.format("Remove Database")));
            editItem.setOnAction(event -> {
                if(dbListView.getSelectionModel().getSelectedItem() != null) {
                    dbPref.remove(dbListView.getSelectionModel().getSelectedItem());
                    dbListView.getItems().remove(dbListView.getSelectionModel().getSelectedItem());
                }
            });
            //Add menu item and bind list to list view
            contextMenu.getItems().add(editItem);
            cell.textProperty().bind(cell.itemProperty());
            //If empty set accordingly
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });
        //dbPref.clear();
    }

    //TODO: make multiple databases supported
    public void namesBtnPressed() {
        service.restart();
    }

    public void loadBtnPressed() {
/*        Scene scene = SetUp.getInstance().loadFilesMenu;
        Stage window = (Stage) loadBtn.getScene().getWindow();
        window.setScene(scene);*/
        service2.restart();
    }

    @FXML
    void backButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    public void addBtnPressed() {
        //Create directory chooser to select database and add to preferences
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choose database folder");
        Stage dcStage = new Stage();
        File selectedDirectory = dc.showDialog(dcStage);

        if (selectedDirectory != null) {
            //Add selected file to preferences to be saved
            dbListView.getItems().add(selectedDirectory.getPath());
            pathToDB = selectedDirectory.getPath();
            dbPref.put(selectedDirectory.getPath(), selectedDirectory.getPath());
        }
    }

    private List<String> getListNames() {
        File dir = new File(dbListView.getSelectionModel().getSelectedItem());
        //Code to deal with same names, case insensitive (chen = Chen)
        File[] namesListing = dir.listFiles();
        List<String> names = new ArrayList<>();
        //Create list of strings of names
        for(File file : namesListing) {
            names.add(file.getName());
        }
        names.remove("uncut_files");
        return names;
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
                    DatabaseProcessor processor = new DatabaseProcessor(dbListView.getSelectionModel().getSelectedItem());
                    processor.processDB();

                    scene = SetUp.getInstance().enterNamesMenu;
                    scene2 = SetUp.getInstance().loadFilesMenu;
                    window = (Stage) namesBtn.getScene().getWindow();
                    String dbName = dbListView.getSelectionModel().getSelectedItem().substring(dbListView.getSelectionModel().getSelectedItem().lastIndexOf("/") +1);
                    pathToDB = dbListView.getSelectionModel().getSelectedItem();
                    SetUp.getInstance().enterNamesController.setUpList(getListNames(), dbName);
                    SetUp.getInstance().loadFilesController.setUpList(getListNames(), dbName);
                    return null;
                }
            };
        }
    }

    public String getPathToDB() {
        return pathToDB;
    }
}
