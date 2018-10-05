package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
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

    @FXML
    private Button backButton;

    @FXML
    private ProgressBar existingProgressBar;

    @FXML
    private Button listButton;

    @FXML
    private Button ratingButton;

    @FXML
    private Button playExistingButton;

    @FXML
    private ProgressBar recordProgressBar;

    @FXML
    private Button recordButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button repeatButton;

    @FXML
    private TextField textField;

    @FXML
    private Label textLabel;

    @FXML
    private Button micButton;

    private MediaPlayer audioPlayer;

    private Timeline timeline;

    String pathToDB;

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }


    @FXML
    void badRecordingsPressed(ActionEvent event) {
    //TODO: FIND WHERE THIS IS FROM??
    }

    @FXML
    void listButtonClicked(MouseEvent event) {

    }


    @FXML
    void playPauseButtonClicked(MouseEvent event) {


    }

    @FXML
    void playExistingButtonClicked(MouseEvent event) {
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }

        String selectedName = textLabel.getText();

        //Get folder name and find files within
        String folderName = pathToDB + "/" + selectedName + "/";
        File[] listFiles = new File(folderName).listFiles();
        Media media = new Media(listFiles[0].toURI().toString());
        audioPlayer = new MediaPlayer(media);
        audioPlayer.setOnPlaying(new CompareMenuController.AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new CompareMenuController.AudioRunnable(true));
        audioPlayer.play();
        audioPlayer.setOnReady(() -> existingProgressBar.setProgress(0.0));
        audioPlayer.setOnReady(this::setExistingProgressBar);

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
                playPauseButton.setDisable(false);
                listButton.setDisable(false);
                recordButton.setDisable(false);
                existingProgressBar.setProgress(0.0);
                audioPlayer.dispose();
                //When the media player is playing the audio file, the buttons will be disabled to prevent the user from navigating away
            } else {
                backButton.setDisable(true);
                playPauseButton.setDisable(true);
                listButton.setDisable(true);
                recordButton.setDisable(true);
            }
        }
    }

    private void setExistingProgressBar() {
        try {
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(existingProgressBar.progressProperty(), 0)),
                    new KeyFrame((audioPlayer.getTotalDuration()), new KeyValue(existingProgressBar.progressProperty(), 1))
            );
            timeline.setCycleCount(1);
            timeline.play();
        } catch (IllegalArgumentException e) {
            //WHOOPS
        }
    }


    @FXML
    void ratingButtonClicked(MouseEvent event) throws IOException {
        String selectedName = textLabel.getText();

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
                    String defaultName = selectedName.concat(": " + rating + "\n");
                    SetUp.getInstance().audioRatingsController.addName(defaultName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @FXML
    void audioRatingsPressed(ActionEvent event) throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }


    @FXML
    void recordButtonClicked(MouseEvent event) {

    }

    @FXML
    void repeatButtonClicked(MouseEvent event) {

    }

    @FXML
    void micButtonClicked(MouseEvent event) throws IOException {
        SetUp.getInstance().microphoneController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().microphoneMenu;
        Stage window = (Stage) micButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Method invoked whenever this scene is switched to, fills list with existing files that can be compared to
    void setUp(String name) throws IOException {
        pathToDB = SetUp.getInstance().databaseSelectMenuController.getPathToDB();
       textLabel.setText(name);
    }

}
