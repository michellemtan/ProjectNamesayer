package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CompareMenuController {

    @FXML private Button backButton;
    @FXML private ListView<String> recordingsList;
    @FXML private Button playPauseButton;
    @FXML private Button sadFaceButton;
    @FXML private ProgressBar progressBar;
    @FXML private Button playExistingBtn;
    @FXML private ProgressBar existProgressBar;
    private String dirName;
    private MediaPlayer audioPlayer;
    private boolean fromCreate;


    //Takes user back to appropriate menu upon back button being pushed
    @FXML
    void backButtonClicked() throws IOException {
        if(fromCreate) {
            Scene scene = SetUp.getInstance().recordCreationMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        } else {
            Scene scene = SetUp.getInstance().recordMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        }
    }

    //Method invoked whenever this scene is switched to, fills list with existing files that can be compared to
    void setUpList(List<String> list, boolean create, String name) {
        fromCreate = create;
        progressBar.setProgress(0.0);
        existProgressBar.setProgress(0.0);
        dirName = name;
        recordingsList.getItems().setAll(list);
        recordingsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        recordingsList.getSelectionModel().select(0);
        //Sort list alphabetically
        Collections.sort(recordingsList.getItems());
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
                backButton.setDisable(false);
                //When the media player is playing the audio file, the buttons will be disabled to prevent the user from navigating away
            } else {
                backButton.setDisable(true);
            }
        }
    }

    //Pauses/plays audio file
    @FXML
    void playPauseButtonClicked() {

        //If an audio file is already playing, stop and play the audio from the start
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            audioPlayer.stop();
        }

        //Create new timeline for progress bar to show how much of the recording is being played
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(5), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.play();
        timeline.setOnFinished(e -> progressBar.setProgress(0.0));

        //Create a new media player instance and set the event handlers to create a thread that listens for when the audio is playing
        Media media = new Media(new File("audio.wav").toURI().toString());
        audioPlayer = new MediaPlayer(media);
        audioPlayer.setOnPlaying(new AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
        audioPlayer.play();
    }

    //Plays audio on button being pressed
    @FXML
    private void playExistingBtnPressed() throws IOException {
        mediaPlayerCreator();
    }

    //Allows user to rate the recording being played
    @FXML
    void sadFaceButtonClicked(MouseEvent event) {
        String selectedName = recordingsList.getSelectionModel().getSelectedItem();
        if (event.getButton() == MouseButton.PRIMARY) {
            //Ask the user to rate their choice
            List<String> choices = new ArrayList<>();
            choices.add("★☆☆☆☆");
            choices.add("★★☆☆☆");
            choices.add("★★★☆☆");
            choices.add("★★★★☆");
            choices.add("★★★★★");
            ChoiceDialog<String> dialog = new ChoiceDialog<>("★☆☆☆☆", choices);
            dialog.setTitle("Recording Rating");
            dialog.setGraphic(null);
            dialog.setHeaderText("Rate " + selectedName + "?");
            dialog.setContentText("Select a rating:");

            //Get rating and format to string
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                try {
                    String rating = result.get();
                    selectedName = selectedName.concat(": " + rating + "\n");
                    SetUp.getInstance().audioRatingsController.addName(selectedName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Context menu to take user to list of audio ratings
    @FXML
    public void badRecordingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().badRecordingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Takes user back to record menu
    @FXML
    public void continueBtnPressed() throws IOException {
        if(fromCreate) {
            Scene scene = SetUp.getInstance().recordCreationMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        } else {
            Scene scene = SetUp.getInstance().recordMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        }
    }

    //Create media player object and initialises behaviour of progress bar
    private void mediaPlayerCreator() throws IOException {
        //Create new media object of audio file
        Media media = new Media(new File(SetUp.getInstance().dbMenuController.getPathToDB() + "/" + dirName + "/" + recordingsList.getSelectionModel().getSelectedItem()).toURI().toString());
        audioPlayer = new MediaPlayer(media);
        audioPlayer.setOnPlaying(new AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
        audioPlayer.play();

        audioPlayer.currentTimeProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            Duration newDuration = (Duration) newValue;
            existProgressBar.setProgress(newDuration.toSeconds()/5);

        });
        audioPlayer.setOnReady(() -> progressBar.setProgress(0.0));
        audioPlayer.setOnReady(this::progressBar);
    }

    //Sets up progress bar to indicate how long the audio player for
    private void progressBar() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(existProgressBar.progressProperty(), 0)),
                new KeyFrame(audioPlayer.getTotalDuration(), new KeyValue(existProgressBar.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.play();
        timeline.setOnFinished(e -> {
            existProgressBar.setProgress(0.0);
        });
    }
}
