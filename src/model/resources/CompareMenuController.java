package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CompareMenuController {

    //TODO: MAYBE CHANGE THIS BACK TO PREVIOUS RECORD BUTTON?

    @FXML private Button backButton;
    @FXML private ProgressBar existingProgressBar;
    @FXML private Button listButton;
    @FXML private Button ratingButton;
    @FXML private Button playExistingButton;
    @FXML private ProgressBar recordProgressBar;
    @FXML private Button recordButton;
    @FXML private ProgressBar progressBar;
    @FXML private Button playPauseButton;
    @FXML private Button repeatButton;
    @FXML private TextField textField;
    @FXML private Label textLabel;
    @FXML private Button micButton;
    private MediaPlayer audioPlayer;
    private Timeline timeline;
    private String pathToDB;
    private int audioRecorded;
    private String fileName;
    private Creation creation;
    private Duration fullNameDuration;
    private List<Media> durationList;
    private Duration[] duration = {Duration.ZERO};

    @FXML
    void backButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void listButtonClicked() throws IOException {
        SetUp.getInstance().namesListController.setUp(textLabel.getText());
        Scene scene = SetUp.getInstance().namesListMenu;
        Stage window = (Stage) listButton.getScene().getWindow();
        window.setScene(scene);

    }


    @FXML
    void playPauseButtonClicked() {
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            audioPlayer.stop();
        }

        //Create a new media player instance and set the event handlers to create a thread that listens for when the audio is playing
        Media media = new Media(new File("./recorded_names/" + fileName +".wav").toURI().toString());
        audioPlayer = new MediaPlayer(media);
        audioPlayer.setOnPlaying(new AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
        audioPlayer.play();
        progressBar();
    }

    @FXML
    void playExistingButtonClicked() throws IOException {
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }

        String selectedName = textLabel.getText();

        durationList = creation.getFullNameMedia();
        DurationService service = new DurationService();
        service.setOnSucceeded(event -> {
            fullNameDuration = duration[0];
        });
        service.start();
        playFullName(durationList, false);
    }

    private void playFullName(List<Media> mediaList , boolean isStarted) {
        if  (!(mediaList.size() == 0)) {
            Media playing = mediaList.remove(0);
            audioPlayer = new MediaPlayer(playing);
            audioPlayer.play();
            disablePlaying();
            if (!isStarted) {
                audioPlayer.setOnReady(() -> existingProgressBar.setProgress(0.0));
                audioPlayer.setOnReady(this::fullNameProgressBar);
            }
            audioPlayer.setOnEndOfMedia(() -> {
                playFullName(mediaList, true);
            });
            //Finished!
        } else {
            enableFinished();
            return;
        }
    }

    private void fullNameProgressBar() {
        try {
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(existingProgressBar.progressProperty(), 0)),
                    new KeyFrame(fullNameDuration, new KeyValue(existingProgressBar.progressProperty(), 1))
            );
            timeline.setCycleCount(1);
            timeline.play();
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }
    }

    private class DurationService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws IOException, InterruptedException {
                    for (Media file: durationList){
                        MediaPlayer mediaPlayer = new MediaPlayer(file);
                        mediaPlayer.setOnReady(new Runnable() {
                            @Override
                            public void run() {
                                duration[0] = duration[0].add(file.getDuration());
                            }
                        });
                    }
                    return null;
                }
            };
        }
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
                enableFinished();
                //When the media player is playing the audio file, the buttons will be disabled to prevent the user from navigating away
            } else {
               disablePlaying();
            }
        }
    }

    private void disablePlaying(){
        playExistingButton.setDisable(true);
        backButton.setDisable(true);
        playPauseButton.setDisable(true);
        listButton.setDisable(true);
        recordButton.setDisable(true);
        micButton.setDisable(true);
        repeatButton.setDisable(true);

    }

    private void enableFinished(){
        backButton.setDisable(false);
        playPauseButton.setDisable(false);
        listButton.setDisable(false);
        recordButton.setDisable(false);
        micButton.setDisable(false);
        repeatButton.setDisable(false);
        playExistingButton.setDisable(false);
        existingProgressBar.setProgress(0.0);
        progressBar.setProgress(0.0);
        audioPlayer.dispose();
    }

    private void setExistingProgressBar() {
        try {
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(existingProgressBar.progressProperty(), 0)),
                    new KeyFrame((audioPlayer.getTotalDuration()), new KeyValue(existingProgressBar.progressProperty(), 1))
            );
            timeline.setCycleCount(1);
            timeline.setOnFinished(e -> existingProgressBar.setProgress(0.0));
            timeline.play();
        } catch (IllegalArgumentException e) {
            //WHOOPS
        }
    }


    @FXML
    void ratingButtonClicked(MouseEvent event) throws IOException {
        String selectedName = textLabel.getText();
        if (event.getButton() == MouseButton.PRIMARY) {
            PopupWindow p = new PopupWindow("model/views/RatingsMessage.fxml", true, selectedName);
        }
    }

    @FXML
    void audioRatingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }


    @FXML
    void recordButtonClicked() throws IOException {
        if (audioRecorded==0) {
            record();
        } else {
                PopupWindow p = new PopupWindow("model/views/OverwriteRecordingMessage.fxml", true,null);
                if (p.getController().getResult()){
                    record();
                }
            }
        backButton.setDisable(true);
    }

    private void record() {
        //Disable all buttons
        recordButton.setDisable(true);
        micButton.setDisable(true);
        backButton.setDisable(true);
        listButton.setDisable(true);
        playExistingButton.setDisable(true);
        playPauseButton.setDisable(true);
        repeatButton.setDisable(true);
        ratingButton.setDisable(true);
        //Use a background thread to record audio files to prevent the GUI from freezing
        RecordAudioService service = new RecordAudioService();
        service.setOnSucceeded(e -> {
            audioRecorded++;
        });

        service.start();
    }

    private class RecordAudioService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    //Disable buttons and start progress bar
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(recordProgressBar.progressProperty(), 0)),
                            new KeyFrame(Duration.seconds(5), new KeyValue(recordProgressBar.progressProperty(), 1))
                    );
                    timeline.setCycleCount(1);
                    timeline.play();

                    new File("./recorded_names").mkdir();

                    recordButton.setDisable(true);
                    micButton.setDisable(true);
                    backButton.setDisable(true);

                    try {
                        //Record audio for five seconds

                        fileName = textLabel.getText().replaceAll(" ","");
                        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -f alsa -i default -t 7 ./recorded_names/" + fileName +".wav");
                        Process audio = builder.start();

                        PauseTransition delay = new PauseTransition(Duration.seconds(5));
                        delay.play();
                        delay.setOnFinished(event -> {
                            //Enable buttons after recording has finished
                            recordButton.setDisable(false);
                            micButton.setDisable(false);
                            backButton.setDisable(false);
                            listButton.setDisable(false);
                            playExistingButton.setDisable(false);
                            playPauseButton.setDisable(false);
                            repeatButton.setDisable(false);
                            ratingButton.setDisable(false);
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    }

    //Private method that sets the progress bar
    private void progressBar() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(5), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.setOnFinished(e -> progressBar.setProgress(0.0));
        timeline.play();
    }

    @FXML
    void repeatButtonClicked() throws IOException {
        String str = textField.getText();

        if (str.equals(null)) {

            //TODO: ERROR MESSAGE
        } else if (isNumeric(str)){
            int numRepeats = Integer.parseInt(str);
            for (int i=0; i<numRepeats; i++){
               repeatLoop();
            }
        }
    }

    private void repeatLoop() throws IOException {
        synchronized (this){
            playExistingButtonClicked();
        }
        playPauseButtonClicked();
    }

    private boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    @FXML
    void micButtonClicked() throws IOException {
        SetUp.getInstance().microphoneController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().microphoneMenu;
        Stage window = (Stage) micButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Method invoked whenever this scene is switched to, fills list with existing files that can be compared to
    void setUp(Creation c) throws IOException {
        pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();
        textLabel.setText(c.getFullName());
        this.creation = c;
        audioRecorded=0;

        //Disable all buttons until audio is recorded
        playPauseButton.setDisable(true);
        repeatButton.setDisable(true);
    }

}
