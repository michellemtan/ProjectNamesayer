package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.DatabaseProcessor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class CompareMenuController {

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
    private Media recordedMedia;
    private double length;

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

    @FXML
    void backButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void listButtonClicked() throws IOException {
        SetUp.getInstance().namesListController.setUp(creation);
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
        audioPlayer = new MediaPlayer(recordedMedia);
        audioPlayer.setOnPlaying(new AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
        audioPlayer.play();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(new Duration(length), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.setOnFinished(e -> progressBar.setProgress(0.0));
        timeline.play();
    }

    @FXML
    void playExistingButtonClicked() throws IOException {
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }
        disablePlaying();
        SetUp.getInstance().practiceMenuController.playList(creation.getFullNameMedia());
        //Timeline that runs the correct length of only the first name
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(existingProgressBar.progressProperty(), 0)),
                new KeyFrame((new Duration(creation.getCreationLength() *1000)), new KeyValue(existingProgressBar.progressProperty(), 1))
        );
        timeline.setOnFinished(e -> {
            backButton.setDisable(false);
            listButton.setDisable(false);
            recordButton.setDisable(false);
            micButton.setDisable(false);
            playExistingButton.setDisable(false);
            existingProgressBar.setProgress(0.0);
        });
        timeline.setCycleCount(1);
        timeline.play();
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

    private void trimSilence() throws InterruptedException, IOException, UnsupportedAudioFileException {
        Thread.sleep(400);
        String command = " ffmpeg -y -i recorded_names/"+ fileName +".wav -af silenceremove=0:0:0:-1:0.5:-35dB recorded_names/"+ fileName +"_trim.wav";
        Thread.sleep(400);
        File file = new File("recorded_names/" + fileName + ".wav");
        DatabaseProcessor.trimAudio(command);
        recordedMedia = new Media(new File("./recorded_names/" + fileName +"_trim.wav").toURI().toString());
        File fileTrim = new File(System.getProperty("user.dir") + "/recorded_names/" + fileName + "_trim.wav");
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(fileTrim));
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        length = (frames+0.0) / format.getFrameRate() + 0.2;
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
                            try {
                                trimSilence();
                            } catch (InterruptedException | IOException | UnsupportedAudioFileException e) {
                                System.out.println("Error trimming silence");
                            }
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

    @FXML
    void repeatButtonClicked() throws IOException {

        //TODO: add a tooltip/check input it correct before playing

        if(textField.getText() != null && !textField.getText().isEmpty()) {
            List<Media> repeatList = new ArrayList<>();
            int repeat = Integer.parseInt(textField.getText());
            double totalLength = (length + creation.getCreationLength()) * repeat;
            for(int i=0; i<repeat; i++) {
                repeatList.addAll(creation.getFullNameMedia());
                repeatList.add(recordedMedia);
            }

            //Disable all buttons
            recordButton.setDisable(true);
            backButton.setDisable(true);
            listButton.setDisable(true);
            playExistingButton.setDisable(true);
            playPauseButton.setDisable(true);
            repeatButton.setDisable(true);
            micButton.setDisable(true);

            SetUp.getInstance().practiceMenuController.playList(repeatList);
            //Timeline for both progress bars
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame((new Duration(totalLength*1000)), new KeyValue(progressBar.progressProperty(), 1))
            );
            timeline.setOnFinished(e -> {
                progressBar.setProgress(0.0);
                recordButton.setDisable(false);
                backButton.setDisable(false);
                listButton.setDisable(false);
                playExistingButton.setDisable(false);
                playPauseButton.setDisable(false);
                repeatButton.setDisable(false);
                micButton.setDisable(false);
            });
            timeline.setCycleCount(1);
            timeline.play();
            Timeline timeline2 = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(existingProgressBar.progressProperty(), 0)),
                    new KeyFrame((new Duration(totalLength*1000)), new KeyValue(existingProgressBar.progressProperty(), 1))
            );
            timeline2.setOnFinished(e -> existingProgressBar.setProgress(0.0));
            timeline2.setCycleCount(1);
            timeline2.play();
        }

    }

    @FXML
    void micButtonClicked() throws IOException {
        SetUp.getInstance().microphoneController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().microphoneMenu;
        Stage window = (Stage) micButton.getScene().getWindow();
        window.setScene(scene);
    }
}
