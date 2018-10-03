package model.resources;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
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
    private Scene scene;
    private Stage window;
    private Stage progressStage;
    private String pathToDB;

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

        //dbPref.clear();
    }

    //TODO: make multiple databases supported
    public void namesBtnPressed() {
        service.restart();
    }

    public void loadBtnPressed() throws IOException {
        Scene scene = SetUp.getInstance().loadFilesMenu;
        Stage window = (Stage) loadBtn.getScene().getWindow();
        window.setScene(scene);
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
                    window = (Stage) namesBtn.getScene().getWindow();
                    SetUp.getInstance().enterNamesController.setUpList(getListNames());
                    return null;
                }
            };
        }
    }
}
