package model.resources;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.DatabaseProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SettingsMenuController {

    @FXML private Button backBtn;
    @FXML private ComboBox<String> chooseDB;
    private String pathToDB;
    private TaskService service = new TaskService();


    public void backBtnPressed() throws IOException {
        String dbName;
        if(chooseDB.getSelectionModel().getSelectedItem().equals("Default Database")) {
            dbName = "Default Database";
        } else {
            dbName = chooseDB.getSelectionModel().getSelectedItem().substring(chooseDB.getSelectionModel().getSelectedItem().lastIndexOf("/") +1);
        }
        SetUp.getInstance().enterNamesController.setUpList(getListNames(chooseDB.getSelectionModel().getSelectedItem()), dbName);

        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    public void initialize() {
        chooseDB.getItems().add("Default Database");
        chooseDB.getItems().add("Add new...");
        chooseDB.getSelectionModel().selectFirst();

        //Create progress bar for processing database
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(service.progressProperty());
        progressBar.setPrefSize(200, 50);

        //Create new stage for progress bar
        Stage progressStage = new Stage();
        progressStage.setTitle("Processing Database");
        Scene sceneP = new Scene(new StackPane(progressBar), 400, 150);
        sceneP.getStylesheets().add("/model/resources/themes/Theme.css");
        progressStage.setScene(sceneP);
        progressStage.setAlwaysOnTop(true);

        //Set service to show progress bar while loading
        service.setOnScheduled(e -> progressStage.show());
        //Set service to change scene upon completion
        service.setOnSucceeded(e -> progressStage.hide());
    }

    //Helper method that iterates through files in supplied path & adds ti list of string. Only invoked upon back button being pushed to leave settings
    private List<String> getListNames(String path) {
        List<String> names = new ArrayList<>();
        //If default database being used
        if(path.equals("Default Database")) {
            File dir = new File(System.getProperty("user.dir") + "/names");
            for(File file : Objects.requireNonNull(dir.listFiles())) {
                names.add(file.getName());
            }
            names.remove("uncut_files");
            return names;
        }

        //If new database has been added
        File dir = new File(path);
        File[] namesListing = dir.listFiles();
        //Create list of strings of names
        for(File file : Objects.requireNonNull(namesListing)) {
            names.add(file.getName());
        }
        names.remove("uncut_files");
        return names;
    }

    public void comboAction(ActionEvent event) {
        if(chooseDB.getSelectionModel().getSelectedItem().equals("Add new...")) {

            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Choose database folder");
            Stage dcStage = new Stage();
            File selectedDirectory = dc.showDialog(dcStage);

            if (selectedDirectory != null) {
                //Verify database is valid i.e contains .wav files
                if(verifyDB(selectedDirectory.getPath())) {
                    if(!chooseDB.getItems().contains(selectedDirectory.getPath())) {
                        chooseDB.getItems().remove("Add new...");
                        //Add new database to combo-box (then re-add add new option so it's at the bottom)
                        chooseDB.getItems().add(selectedDirectory.getPath());
                        chooseDB.getItems().add("Add new...");
                        //TODO: if add same database twice, can select 'Add New' option from combo-box - bad
                        chooseDB.getSelectionModel().select(selectedDirectory.getPath());
                        pathToDB = selectedDirectory.getPath();
                        service.restart();
                    }
                } else {
                    //Simple alert if selected database is invalid
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Invalid Database");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid database! The selected folder must only contain files ending in .wav");

                    alert.showAndWait();
                }
            }
        }
    }

    //Helper method that scans through files in a directory and returns true if they're all .wav files
    private boolean verifyDB(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        if(!dir.isDirectory() || files == null || files.length <1){
            return false;
        }
        for(File file : files) {
            if(!file.getName().endsWith(".wav")) {
                return false;
            }
        }
        return true;
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
                    DatabaseProcessor processor = new DatabaseProcessor(chooseDB.getSelectionModel().getSelectedItem());
                    processor.processDB();
                    return null;
                }
            };
        }
    }

}
