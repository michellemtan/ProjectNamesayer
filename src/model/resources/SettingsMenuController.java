package model.resources;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.DatabaseProcessor;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SettingsMenuController {

    //TODO: if the warning that default DB isn't there, avoid the null pointer from pushing back
    //TODO: null pointer from mic menu if help button pushed

    @FXML private Button backBtn;
    @FXML private ComboBox<String> chooseDB;
    @FXML private CheckBox waveBox;
    @FXML private BorderPane backPane;
    @FXML private ProgressBar micBar;
    private String currentTheme;
    private String pathToDB;
    private TaskService service = new TaskService();
    //Themes
    private String themeURL = getClass().getResource("/model/resources/themes/Theme.css").toExternalForm();
    private String dracThemeURL = getClass().getResource("/model/resources/themes/dracTheme.css").toExternalForm();
    private String mThemeURL = getClass().getResource("/model/resources/themes/mTheme.css").toExternalForm();

    String getTheme(){
        return currentTheme;
    }

    String getPathToDB() {
        return pathToDB;
    }

    @FXML
    private void dracBtnPressed() throws IOException {
        currentTheme = dracThemeURL;
        SetUp.getInstance().changeTheme(dracThemeURL);
    }

    @FXML
    private void defBtnPressed() throws IOException {
        currentTheme = themeURL;
        SetUp.getInstance().changeTheme(themeURL);
    }

    @FXML
    private void mBtnPressed() throws IOException {
        currentTheme = mThemeURL;
        SetUp.getInstance().changeTheme(mThemeURL);
    }

    void disableBack(boolean value) {
        if(value) {
            backBtn.setDisable(true);
        } else {
            backBtn.setDisable(false);
        }
    }

    void startMicVol() throws IOException {
        SetUp.getInstance().microphoneController.startMic(micBar, backBtn);
    }

    @FXML
    private void helpBtnPressed() throws IOException {
        Scene scene = SetUp.getInstance().instructionsMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    //Take user back to main menu, and pass list of current db to enter names
    public void backBtnPressed() throws IOException {
        setUpNameLists();
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    //Method to set up list in enter names menu, using names from the database currently selected
    void setUpNameLists() throws IOException {
        String dbName;
        if(chooseDB.getSelectionModel().getSelectedItem().equals("Default Database")) {
            dbName = "Default Database";
            pathToDB = System.getProperty("user.dir") + "/names";
        } else {
            dbName = chooseDB.getSelectionModel().getSelectedItem().substring(chooseDB.getSelectionModel().getSelectedItem().lastIndexOf("/") +1);
            pathToDB = chooseDB.getSelectionModel().getSelectedItem();
        }
        SetUp.getInstance().enterNamesController.setUpList(getListNames(chooseDB.getSelectionModel().getSelectedItem()), dbName);
        SetUp.getInstance().listenMenuController.setUpList(getListNames(chooseDB.getSelectionModel().getSelectedItem()), dbName);
    }

    public void initialize() {
        currentTheme = themeURL;
        //Set up default values for combo-box
        chooseDB.getItems().add("Default Database");
        chooseDB.getItems().add("Add new...");
        chooseDB.getSelectionModel().selectFirst();
        waveBox.setSelected(true);

        //Set up colours for theme buttons
        backPane.getStyleClass().add("root-clear");


        //Listener to deselect 'Add new' option if it is selected because user chooses bad folder
        chooseDB.getSelectionModel().selectedItemProperty().addListener((obs, old, newI) -> {
            if(newI.equals("Add new...")){
                //Must be run later as in JFX the observable list of the combo-box cannot be edited while it's currently being edited (listener gets fired too often)
                Platform.runLater(() -> chooseDB.getSelectionModel().selectFirst());
            }
        });


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
        service.setOnSucceeded(e -> progressStage.hide());
    }

    //Helper method that iterates through files in supplied path & adds to list of string. Only invoked when set up name lists is being called
    private List<String> getListNames(String path) {
        List<String> names = new ArrayList<>();
        //If default database being used
        if(path.equals("Default Database")) {
            File dir = new File(System.getProperty("user.dir") + "/names");
            for(File file : Objects.requireNonNull(dir.listFiles())) {
                names.add(file.getName());
            }
            //Remove folder that's not a name
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

    //Triggered when user selects database
    public void comboAction() throws IOException {
        if(chooseDB.getSelectionModel().getSelectedItem().equals("Add new...")) {
            //User directory chooser to let them choose the folder
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Choose database folder");
            Stage dcStage = new Stage();
            File selectedDirectory = dc.showDialog(dcStage);

            if (selectedDirectory != null) {
                //Verify database is valid i.e contains .wav files
                if(verifyDB(selectedDirectory.getPath())) {
                    //Check for duplicates
                    if(!chooseDB.getItems().contains(selectedDirectory.getPath())) {
                        chooseDB.getItems().remove("Add new...");
                        //Add new database to combo-box (then re-add add new option so it's at the bottom)
                        chooseDB.getItems().add(selectedDirectory.getPath());
                        chooseDB.getItems().add("Add new...");
                        chooseDB.getSelectionModel().select(selectedDirectory.getPath());
                        pathToDB = selectedDirectory.getPath();
                        disableBack(false);
                        SetUp.getInstance().startMenuController.buttonsOff(false);
                        //Process database in background
                        service.restart();
                    }
                } else {
                    //Simple alert if selected database is invalid
                    //TODO: alerts can be skinned, see: https://stackoverflow.com/questions/28417140/styling-default-javafx-dialogs
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Invalid Database");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid database! The selected folder must only contain folders or files ending in .wav");

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
            if(file.isDirectory()) {
                for (File innerFile : Objects.requireNonNull(file.listFiles())) {
                    if(!innerFile.getName().endsWith(".wav")) {
                        return false;
                    }
                }
            } else if(!file.getName().endsWith(".wav")) {
                return false;
            }
        }
        return true;
    }

    @FXML
    private void playDing() {
        try {
            //Use input stream to play ding from within jar
            InputStream is = getClass().getResourceAsStream("/model/resources/images/ding.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start( );
        } catch (Exception ex) {
            ex.printStackTrace();
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
                    DatabaseProcessor processor = new DatabaseProcessor(chooseDB.getSelectionModel().getSelectedItem());
                    processor.processDB();
                    return null;
                }
            };
        }
    }

}
