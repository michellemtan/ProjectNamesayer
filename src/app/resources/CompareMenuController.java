package app.resources;

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
import app.DatabaseProcessor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
    private int audioRecorded;
    private String fileName;
    private Creation creation;
    private Media recordedMedia;
    private double length;
    private boolean recorded;
    private boolean isStopped;

    //Method invoked whenever this scene is switched to, fills list with existing files that can be compared to
    void setUp(Creation c) {
        textLabel.setText(c.getFullName());
        this.creation = c;
        audioRecorded=0;
        isStopped = false;
        //Disable all buttons until audio is recorded
        playPauseButton.setDisable(true);
        repeatButton.setDisable(true);
    }

    //Run on back button being pressed
    @FXML
    void backButtonClicked() throws IOException {
        recorded = false;
        progressBar.setProgress(0.0);
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Run on list button being pressed
    @FXML
    void listButtonClicked() throws IOException {
        SetUp.getInstance().namesListController.setUp(creation);
        Scene scene = SetUp.getInstance().namesListMenu;
        Stage window = (Stage) listButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Run on play recording button being pressed
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

        //Create timeline to monitor playing length
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(new Duration(length*1000), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.setOnFinished(e -> progressBar.setProgress(0.0)); //Reset progress to 0 upon timeline completion
        timeline.play();
    }

    //Run on play db name button being pressed
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
        //Enable correct buttons upon timeline completion
        timeline.setOnFinished(e -> {
            backButton.setDisable(false);
            listButton.setDisable(false);
            recordButton.setDisable(false);
            micButton.setDisable(false);
            playExistingButton.setDisable(false);
            if(recorded) {
                playPauseButton.setDisable(false);
            }
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

    //Helper method to disable button while playing
    private void disablePlaying(){
        playExistingButton.setDisable(true);
        backButton.setDisable(true);
        playPauseButton.setDisable(true);
        listButton.setDisable(true);
        recordButton.setDisable(true);
        micButton.setDisable(true);
        repeatButton.setDisable(true);
    }

    //Helper method to enable button once playing finish
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

    //Run on rating button being pressed
    @FXML
    void ratingButtonClicked(MouseEvent event) throws IOException {
        String selectedName = textLabel.getText();
        if (event.getButton() == MouseButton.PRIMARY) {
            PopupWindow p = new PopupWindow("app/views/RatingsMessage.fxml", true, selectedName);
        }
    }

    //Run on ratings context menu being pressed
    @FXML
    void audioRatingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    //Run on recording button being pressed
    @FXML
    void recordButtonClicked() throws IOException {
        if (audioRecorded==0) {
            record();
        } else if(!(isStopped && audioRecorded==1)) {
                PopupWindow p = new PopupWindow("app/views/OverwriteRecordingMessage.fxml", true,null);
                if (p.getController().getResult()){
                    record();
                }
            }
        backButton.setDisable(true);
    }

    //Simple helper method to record audio
    private void record() {
        //Disable all buttons
        micButton.setDisable(true);
        backButton.setDisable(true);
        listButton.setDisable(true);
        playExistingButton.setDisable(true);
        playPauseButton.setDisable(true);
        repeatButton.setDisable(true);
        ratingButton.setDisable(true);

        //Set record button ready to be stopped
        recordButton.setText("Stop ◼");

        //Use a background thread to record audio files to prevent the GUI from freezing
        RecordAudioService service = new RecordAudioService();
        service.start();

        //Kill the service
        recordButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            service.cancel();
            audioRecorded++;
            isStopped = true;
            System.out.println(audioRecorded);
            recordButton.setText("Record");
            progressBar = new ProgressBar();
        });
    }

    //Trim silence method called upon audio being recorded
    private void trimSilence() throws IOException, UnsupportedAudioFileException {
        recorded = true;
        String command = " ffmpeg -y -i recorded_names/"+ fileName +".wav -af silenceremove=0:0:0:-1:0.5:-35dB recorded_names/"+ fileName +"_trim.wav";
        File file = new File("recorded_names/" + fileName + ".wav");
        DatabaseProcessor.trimAudio(command);
        recordedMedia = new Media(new File("./recorded_names/" + fileName +"_trim.wav").toURI().toString());
        File fileTrim = new File(System.getProperty("user.dir") + "/recorded_names/" + fileName + "_trim.wav");
        //Get length of recorded audio for progress bar use
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(fileTrim));
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        length = (frames+0.0) / format.getFrameRate() + 0.2;
    }

    //Background service for recording audio
    private class RecordAudioService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    //Disable buttons and start progress bar via timeline
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(recordProgressBar.progressProperty(), 0)),
                            new KeyFrame(Duration.seconds(5), new KeyValue(recordProgressBar.progressProperty(), 1))
                    );
                    timeline.setCycleCount(1);
                    timeline.play();

                    new File("./recorded_names").mkdir();

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
                            } catch (IOException | UnsupportedAudioFileException e) {
                                System.out.println("Error trimming silence");
                            }
                            //Enable buttons after recording has finished
                            micButton.setDisable(false);
                            backButton.setDisable(false);
                            listButton.setDisable(false);
                            playExistingButton.setDisable(false);
                            playPauseButton.setDisable(false);
                            repeatButton.setDisable(false);
                            ratingButton.setDisable(false);
                            recordButton.setText("Record");
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
        }
    }

    //Run on repeat button being pressed
    @FXML
    void repeatButtonClicked() throws IOException {

        Stage stage = (Stage) repeatButton.getScene().getWindow();

        if (textField.getText().equals("")) {
            //Show tool tip when nothing has been entered
            Tooltip customTooltip = new CustomTooltip(stage, repeatButton, "Please enter a number up to 5!", null);
        } else if (!isNumeric(textField.getText())){
            //Show tool tip when a non-valid integer has been entered
            Tooltip customTooltip = new CustomTooltip(stage, repeatButton, "Please enter a number up to 5", null);
        } else if(textField.getText() != null && !textField.getText().isEmpty()) {
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

    //Run on mic button being pressed
    @FXML
    void micButtonClicked() throws IOException {
        SetUp.getInstance().microphoneController.setPreviousScene();
        Scene scene = SetUp.getInstance().microphoneMenu;
        Stage window = (Stage) micButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Helper method to determine is user entered a number not a letter
    private boolean isNumeric(String str)
    {
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }

        if (str.equals("0")){
            return false;
        } else return Integer.parseInt(textField.getText()) <= 5;
    }
}
