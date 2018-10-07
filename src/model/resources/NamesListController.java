package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NamesListController {

    @FXML
    private Button playButton;

    @FXML
    private Button setDefaultBtn;

    @FXML
    private Label defaultLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ListView<String> nameListView;

    @FXML
    private Label nameName;

    @FXML
    private Button backBtn;

    @FXML
    private Button ratingsButton;

    @FXML
    private ComboBox<String> nameMenu;

    private MediaPlayer audioPlayer;

    private String pathToDB;

    @FXML
    void audioRatingsPressed(ActionEvent event) {

    }

    @FXML
    void backBtnPressed(ActionEvent event) throws IOException, InterruptedException {

        //Re-concatenate name after changing
/*        ProcessBuilder audioBuilder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -safe 0 -f concat -i ConcatNames.txt -c copy ./created_names/" + audioNumber + "_" + newName +".wav");
        Process p = audioBuilder.start();
        p.waitFor();*/

        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void playButtonClicked(MouseEvent event) throws IOException {
        //Stop audio player if there's currently one playing
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }

        //Get strings of audio file paths
        int selectedIndex = nameListView.getSelectionModel().getSelectedIndex();

        String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
        File[] listFiles = new File(folderName).listFiles();

        //Create and start media player
        Media media = new Media(listFiles[selectedIndex].toURI().toString());
        audioPlayer = new MediaPlayer(media);
        audioPlayer.setOnPlaying(new AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
        audioPlayer.play();
        //Set progress bar to 0 on start
        audioPlayer.setOnReady(() -> progressBar.setProgress(0.0));
        audioPlayer.setOnReady(this::progressBar);

    }

    //Creates and starts progress bar for creation playing
    private void progressBar() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame((audioPlayer.getTotalDuration().add(Duration.millis(1000))), new KeyValue(progressBar.progressProperty(), 1))
        );
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

    @FXML
    void ratingsButtonClicked(MouseEvent event) {

    }

    @FXML
    void setDefaultClicked(MouseEvent event) throws IOException {

        int selectedIndex = nameListView.getSelectionModel().getSelectedIndex();

        String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
        File[] listFiles = new File(folderName).listFiles();

        Media media = new Media(listFiles[selectedIndex].toURI().toString());

        //Set default
        SetUp.getInstance().practiceMenuController.setDefault(nameMenu.getSelectionModel().getSelectedItem(), media);
        defaultLabel.setText("Default: " + listFiles[selectedIndex].getName());

    }

    @FXML
    void setUp(String wholeName) throws IOException {

        pathToDB = SetUp.getInstance().databaseSelectMenuController.getPathToDB();

        String[] split = wholeName.replaceAll("-", " -").split("[\\s]");

        ObservableList<String> options = FXCollections.observableArrayList();

        for(int i=0; i<split.length; i++) {
           options.add(split[i]);
           System.out.println(split[i]);
        }

        nameMenu.getItems().addAll(options);

    }

    @FXML
    void nameMenuAction(ActionEvent event) throws IOException {

        String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
        File[] listFiles = new File(folderName).listFiles();

        List<String> displayName = new ArrayList<>();

        for (File f: listFiles){
            displayName.add(f.getName());
        }

        nameListView.getItems().setAll(displayName);

        //Set up the default label
        String defaultName = SetUp.getInstance().practiceMenuController.getDefault(nameMenu.getSelectionModel().getSelectedItem()).toString();
        defaultLabel.setText("Default: " + defaultName);
    }

}
