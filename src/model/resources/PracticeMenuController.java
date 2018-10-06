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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PracticeMenuController {

    @FXML private Button playPauseButton;
    @FXML private Button playSingleButton;
    @FXML private Button shuffleButton;
    @FXML private Button compareButton;
    @FXML private ProgressBar progressBar;
    @FXML private ListView<String> creationsListView;
    @FXML private Label creationName;
    @FXML private Button backButton;
    @FXML private Button ratingsButton;
    @FXML private ContextMenu ratingsContext;
    @FXML private MenuItem audioRatings;
    private List<String> creationList;
    private String pathToDB;
    private MediaPlayer audioPlayer;
    private boolean isFinished;
    private Timeline timeline;
    private ObservableList<Media> mediaList;
    private String selectedName;

    @FXML
    void backButtonClicked() throws IOException {
        // Load the new scene - confirm if the user wants to exit practice
        Scene scene = SetUp.getInstance().exitPracticeMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void ratingsButtonClicked(MouseEvent event)  {
        String selectedName = creationsListView.getSelectionModel().getSelectedItem() + ".wav";

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
    public void audioRatingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("practiceMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void playButtonClicked() throws IOException, InterruptedException {
        if (isFinished) {
            mediaPlayerCreator();
        } else if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            shuffleButton.setDisable(false);
            playPauseButton.setDisable(false);
            backButton.setDisable(false);
            playSingleButton.setDisable(false);
            audioPlayer.pause();
            timeline.pause();
        } else if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            audioPlayer.play();
            timeline.play();
        } else {
            mediaPlayerCreator();
        }

    }

    private void addToTextFile(String name) throws IOException {
        File f = new File("ConcatNames.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        bw.append("file '"+name+"'\n");
        bw.flush();
        bw.close();
    }

    private void mediaPlayerCreator() throws IOException, InterruptedException {

        List<String> audioList = new ArrayList<>(new ArrayList<>(creationsListView.getItems()));
        mediaList = FXCollections.observableArrayList();

        for (String creation : audioList) {
            //Set up the file to be played
            selectedName = creation;

            //Split name up and concat audio files
            String[] split = selectedName.split("[-\\s]");

            for (int i = 0; i < split.length; i++) {
                String folderName = pathToDB + "/" + split[i] + "/";
                File[] listFiles = new File(folderName).listFiles();
                String concatString = listFiles[0].toURI().toString();
                concatString = concatString.replaceAll("file:", "");
                addToTextFile(concatString);
            }

            String newName = selectedName.replaceAll(" ","");
            System.out.println(newName);

            ProcessBuilder audioBuilder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -safe 0 -f concat -i ConcatNames.txt -c copy " + newName +".wav");
            Process p = audioBuilder.start();
            System.out.println("Start");
            p.waitFor();
            System.out.println("Ended");

            //Erase the file after reading
            Media media = new Media(new File(newName).toURI().toString() + ".wav");
            mediaList.add(media);
            System.out.println("Erased");
           PrintWriter writer = new PrintWriter("ConcatNames.txt", "UTF-8");

        }
            playMediaTracks(mediaList, audioList);
    }


    private void playMediaTracks(List<Media> mediaList, List<String> audioList) {

        if (mediaList.size() == 0) {
            isFinished = true;
            shuffleButton.setDisable(false);
            backButton.setDisable(false);
            playSingleButton.setDisable(false);
            compareButton.setDisable(false);
            return;
        } else {
            isFinished = false;
            backButton.setDisable(true);
            shuffleButton.setDisable(true);
            playSingleButton.setDisable(true);
            compareButton.setDisable(true);
        }

        creationName.setText(audioList.get(0));
        audioList.remove(0);

        Media playing = mediaList.remove(0);
        audioPlayer = new MediaPlayer(playing);
        audioPlayer.play();
        audioPlayer.setOnReady(() -> progressBar.setProgress(0.0));
        audioPlayer.setOnReady(this::progressBar);
        audioPlayer.setOnEndOfMedia(() -> {
            playMediaTracks(mediaList, audioList);
        });
    }

    private void progressBar() {
        try {
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame((audioPlayer.getTotalDuration()), new KeyValue(progressBar.progressProperty(), 1))
            );
            timeline.setCycleCount(1);
            timeline.play();
        } catch (IllegalArgumentException e) {
            //WHOOPS
        }
    }


    @FXML
    public void playSingleButtonClicked() {

        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }

        //Change the label of the practice menu to the name being played
        selectedName = creationsListView.getSelectionModel().getSelectedItem();
        creationName.setText(selectedName);

        //Get folder name and find files within
        String folderName = pathToDB + "/" + selectedName;
        File[] listFiles = new File(folderName).listFiles();
        Media media = new Media(listFiles[0].toURI().toString());
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

                if (creationList.size() > 1) {
                    shuffleButton.setDisable(false);
                    playPauseButton.setDisable(false);
                }
                backButton.setDisable(false);
                playPauseButton.setDisable(false);
                compareButton.setDisable(false);
                audioPlayer.dispose();
                //When the media player is playing the audio file, the buttons will be disabled to prevent the user from navigating away
            } else {
                backButton.setDisable(true);
                shuffleButton.setDisable(true);
                playPauseButton.setDisable(true);
                compareButton.setDisable(true);
            }
        }
    }

    @FXML
    void compareButtonClicked(MouseEvent event) throws IOException {

        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        SetUp.getInstance().compareMenuController.setUp(creationsListView.getSelectionModel().getSelectedItem());
        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) compareButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void shuffleButtonClicked(MouseEvent event) {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        //Shuffle list
        Collections.shuffle(creationsListView.getItems());
        creationList = new ArrayList<>(new ArrayList<>(creationsListView.getItems()));
    }

    @FXML
    void audioRatingsPressed(ActionEvent event) throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("practiceMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    public void setUpList(List<String> list) throws IOException {
        creationList = list;
        pathToDB = SetUp.getInstance().databaseSelectMenuController.getPathToDB();
        playPauseButton.setDisable(false);
        if (creationList.size()<=1){
            playPauseButton.setDisable(true);
            shuffleButton.setDisable(true);
        }
        creationsListView.getItems().setAll(creationList);
        creationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pathToDB = SetUp.getInstance().databaseSelectMenuController.getPathToDB();

        creationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            creationName.setText(creationsListView.getSelectionModel().getSelectedItem());
            if(creationsListView.getSelectionModel().getSelectedItems().size() != 1) {
                //If nothing is selected, disable the buttons
                playSingleButton.setDisable(true);
                playPauseButton.setDisable(true);
                ratingsButton.setDisable(true);
                shuffleButton.setDisable(true);
                compareButton.setDisable(true);

            } else {
                playSingleButton.setDisable(false);
                playPauseButton.setDisable(false);
                ratingsButton.setDisable(false);
                shuffleButton.setDisable(false);
                compareButton.setDisable(false);

            }
        });

        setUpTitle();
    }

    private void setUpTitle(){
        if (creationList!=null){
            creationsListView.getSelectionModel().select(0);
            creationName.setText(creationList.get(0));
        }
    }
}


