package app.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import java.util.List;
import java.util.Objects;

public class ListenMenuController {

    @FXML private Button backBtn;
    @FXML private ListView<String> namesListView;
    @FXML private Label dbName;
    @FXML private ProgressBar progressBar;
    @FXML private Button ratingsButton;
    private MediaPlayer audioPlayer;

    //Run on back button being pressed
    public void backBtnPressed() throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    //Run on switching to this scene
    void setUpList(List<String> listNames, String name) {
        //Clear names list and sort case insensitive order
        namesListView.getItems().clear();
        namesListView.getItems().addAll(listNames);
        namesListView.getItems().sort(String.CASE_INSENSITIVE_ORDER);
        dbName.setText(name);
    }

    //Run on play single button being pressed
    @FXML
    void playSingleButtonClicked() throws IOException {
        //Stop audio playing if currently it is
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }
        //Select first
        if (namesListView.getSelectionModel().getSelectedItem()==null){
            namesListView.getSelectionModel().selectFirst();
        }

        //Change the label of the practice menu to the name being played
        String selectedName = namesListView.getSelectionModel().getSelectedItem();
        Media media;
        try {
            media = SetUp.getInstance().practiceMenuController.getHashMap().get(selectedName).getFirstNameMedia();
        } catch (NullPointerException e){
            String pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();
            String folderName = pathToDB + "/" + selectedName + "/";
            File[] listFiles = new File(folderName).listFiles();
            media = new Media(Objects.requireNonNull(listFiles)[0].toURI().toString());
        }
            //Instantiate audio player and set according initial and completion tasks
            audioPlayer = new MediaPlayer(media);
            audioPlayer.setOnPlaying(new AudioRunnable(false));
            audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
            audioPlayer.play();
            audioPlayer.setOnReady(() -> progressBar.setProgress(0.0));
            audioPlayer.setOnReady(this::progressBar);
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
                backBtn.setDisable(false);
                audioPlayer.dispose();
                //When the media player is playing the audio file, the buttons will be disabled to prevent the user from navigating away
            } else {
                backBtn.setDisable(true);
            }
        }
    }

    //Helper method to play name if double clicked
    @FXML
    private void doubleClick(MouseEvent click) throws IOException {
        if (click.getClickCount() == 2) {
            playSingleButtonClicked();
        }
    }

    //Helper method to bind timeline to progress bar
    private void progressBar() {
        try {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame((audioPlayer.getTotalDuration()), new KeyValue(progressBar.progressProperty(), 1))
            );
            timeline.setCycleCount(1);
            timeline.play();
        } catch (IllegalArgumentException e) {
            System.out.println("Could not get progress bar duration");
        }
    }

    //Run on ratings button being pressed
    @FXML
    public void ratingsButtonClicked(MouseEvent event) throws IOException {
        String selectedName = namesListView.getSelectionModel().getSelectedItem();

        if (event.getButton() == MouseButton.PRIMARY) {
            if (selectedName == null){
                Stage stage = (Stage) ratingsButton.getScene().getWindow();
                Tooltip customTooltip = new CustomTooltip(stage, ratingsButton, "Please select a name!", null);
            } else {
                PopupWindow p = new PopupWindow("app/views/RatingsMessage.fxml", true, selectedName);
            }
        }
    }

    //Run on ratings context menu being pressed
    @FXML
    public void audioRatingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("listenMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }
}
