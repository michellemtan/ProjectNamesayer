package model.resources;

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

        //dbPref.clear();
    }

    public void namesBtnPressed() {
        DatabaseProcessor dbProcessor = new DatabaseProcessor(dbListView.getSelectionModel().getSelectedItem());
        dbProcessor.processDB();
        //TODO: add progress bar for process db
        Scene scene = SetUp.getInstance().enterNamesMenu;
        Stage window = (Stage) namesBtn.getScene().getWindow();
        window.setScene(scene);
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
}
