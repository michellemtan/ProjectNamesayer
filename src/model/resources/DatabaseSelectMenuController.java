package model.resources;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.DatabaseProcessor;

import java.io.File;
import java.io.IOException;
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

        //Set service to change scene upon completion
        service.setOnSucceeded(e -> {
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
            dbPref.put(selectedDirectory.getPath(), selectedDirectory.getPath());
        }
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
                    return null;
                }
            };
        }
    }
}
