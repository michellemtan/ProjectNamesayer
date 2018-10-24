package app.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.*;

public class NamesListController {

    @FXML private Button playButton;
    @FXML private Button setDefaultBtn;
    @FXML private Label defaultLabel;
    @FXML private ProgressBar progressBar;
    @FXML private ListView<String> nameListView;
    @FXML private Button backBtn;
    @FXML private ComboBox<String> nameMenu;
    private MediaPlayer audioPlayer;
    private String pathToDB;
    private Creation creation;

    //Run on ratings button being pressed
    @FXML
    void ratingButtonClicked(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            PopupWindow p = new PopupWindow("app/views/RatingsMessage.fxml", true, creation.getFullName());
        }
    }

    //Run on audio context menu being pressed
    @FXML
    public void audioRatingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("namesListMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    //Run on back button being pressed
    @FXML
    void backBtnPressed() throws IOException {
        //Clear list view
        nameListView.getItems().removeAll(nameListView.getItems());
        nameListView.refresh();

        //Change scene back
        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    //Run on play button being pressed
    @FXML
    void playButtonClicked() throws IOException, UnsupportedAudioFileException {
        //Stop audio player if there's currently one playing
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }
        //Get strings of audio file paths
        if (nameListView.getSelectionModel().getSelectedIndex()<0) {
            nameListView.getSelectionModel().selectFirst();
        }

        int selectedIndex = nameListView.getSelectionModel().getSelectedIndex();

        String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
        File[] listFiles = new File(folderName).listFiles();

        //Calculate length of audio clip
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(listFiles)[0]);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        double length = (frames+0.0) / format.getFrameRate() + 0.2;


        //Create and start media player
        Media media = new Media(listFiles[selectedIndex].toURI().toString());
        audioPlayer = new MediaPlayer(media);
        audioPlayer.setOnPlaying(new AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
        audioPlayer.play();
        //When audio player is ready, play & set progress bar to run the length of the audio clip
        audioPlayer.setOnReady(() -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame(new Duration(length * 1000), new KeyValue(progressBar.progressProperty(), 1))
            );
            timeline.setOnFinished(e -> progressBar.setProgress(0.0));
            timeline.setCycleCount(1);
            timeline.play();
        });

    }

    //AudioRunnable is a thread that runs in the background and acts as a listener for the media player to ensure buttons are enabled/disabled correctly
    private class AudioRunnable implements Runnable {
        private boolean isFinished;
        private AudioRunnable(boolean status){
            isFinished = status;
        }
        @Override
        public void run() {
            //When the media player has finished, the buttons will be enabled
            if (isFinished) {
                if(nameListView.getItems().size() <= 1) {
                    setDefaultBtn.setDisable(true);
                } else {
                    setDefaultBtn.setDisable(false);
                }
                backBtn.setDisable(false);
                playButton.setDisable(false);
                //When the media player is playing the audio file, the buttons will be disabled to prevent the user from navigating away
            } else {
                setDefaultBtn.setDisable(true);
                backBtn.setDisable(true);
                playButton.setDisable(true);
            }
        }
    }

    //Run on set default button being pressed
    @FXML
    void setDefaultClicked() {
        //Get default and instantiate new media object
        int selectedIndex = nameListView.getSelectionModel().getSelectedIndex();
        String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
        File[] listFiles = new File(folderName).listFiles();
        Media m = new Media(Objects.requireNonNull(listFiles)[selectedIndex].toURI().toString());

        //Set default audio file
        creation.setDefaultMedia(nameMenu.getSelectionModel().getSelectedItem(), m);
        defaultLabel.setText("Default: " + listFiles[selectedIndex].getName());
    }

    //Run switching to this scene
    @FXML
    void setUp(Creation c) throws IOException {
        creation = c;
        String fullName = c.getFullName();
        nameListView.getItems().removeAll(nameListView.getItems());

        //Disable buttons
        playButton.setDisable(true);
        setDefaultBtn.setDisable(true);

        //This method sets up the combo box to display all the parts of a name
        pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();

        String[] split = fullName.replaceAll("-", "- ").split("[\\s]");

        ObservableList<String> options = FXCollections.observableArrayList();

        //Add split to options list
        Collections.addAll(options, split);

        //Set the combo box with options
        nameMenu.getItems().setAll(options);
        nameMenu.getSelectionModel().selectFirst();

        //Add listener to listview
        nameListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (nameListView.getSelectionModel().getSelectedItems().size() != 1) {
                setDefaultBtn.setDisable(true);
                playButton.setDisable(true);
            } else {
                setDefaultBtn.setDisable(false);
                playButton.setDisable(false);
            }
        });

        //Add listener to combo option box
        nameMenu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (nameMenu.getSelectionModel().getSelectedIndex()<0){
                playButton.setDisable(true);
                setDefaultBtn.setDisable(true);
            } else {
                playButton.setDisable(false);
                setDefaultBtn.setDisable(false);
            }
        });
        nameMenuAction();
    }

    //Run on user pressing combo-box
    @FXML
    void nameMenuAction() {
        //If valid selection
        if (nameMenu.getSelectionModel().getSelectedIndex() >= 0) {
            String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
            File[] listFiles = new File(folderName).listFiles();
            List<String> displayList = new ArrayList<>();

            //Add names to combo-box
            for (File f : Objects.requireNonNull(listFiles)) {
                displayList.add(f.getName());
            }
            nameListView.getItems().setAll(displayList);

            //Set up the default label
            File f = new File(creation.getFullNameHashMap().get(nameMenu.getSelectionModel().getSelectedItem()).getSource());
            defaultLabel.setText("Default: " + f.getName());
        }
    }
}
