package model.resources;

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

import java.io.*;
import java.util.*;

public class PracticeMenuController {

    //TODO: DISABLE SHUFFLE AND LIST PLAY BUTTON WHEN THERE IS ONLY ONE NAME
    //TODO: there are also some cases of names being 'squashed' so they're high pitched and play too fast.

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
    private HashMap<String,Creation> hashMap;
    private int delay = 500;

    @FXML
    void backButtonClicked() throws IOException {
        // Load the new scene - confirm if the user wants to exit practice
        Scene scene = SetUp.getInstance().exitPracticeMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void ratingsButtonClicked(MouseEvent event) throws IOException {
        String selectedName = creationsListView.getSelectionModel().getSelectedItem();

        if (event.getButton() == MouseButton.PRIMARY) {
            PopupWindow p = new PopupWindow("model/views/RatingsMessage.fxml", true, selectedName);
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
    void playButtonClicked(MouseEvent event) throws IOException, InterruptedException {
        if (event.getButton() == MouseButton.PRIMARY) {
            playNamesList(false);
        }
    }

    private void mediaPlayerCreator(boolean isFirstName) throws IOException, InterruptedException {

        List<String> audioList = new ArrayList<>(new ArrayList<>(creationsListView.getItems()));
        mediaList = FXCollections.observableArrayList();

        if (isFirstName){
            for (String a: audioList){
                mediaList.add(hashMap.get(a).getFirstNameMedia());
            }
        } else {
            for (String a: audioList){
                mediaList.add(hashMap.get(a).getMedia());
            }
        }

        playMediaTracks(mediaList, audioList, isFirstName);
    }


    private void playMediaTracks(List<Media> mediaList, List<String> audioList, boolean isFirstName) {

        if (mediaList.size() == 0) {
            isFinished = true;
            shuffleButton.setDisable(false);
            backButton.setDisable(false);
            playPauseButton.setDisable(false);
            return;
        } else {
            isFinished = false;
            backButton.setDisable(true);
            shuffleButton.setDisable(true);
            playSingleButton.setDisable(true);
            compareButton.setDisable(true);
        }

        if (isFirstName) {
            creationName.setText(hashMap.get(audioList.get(0)).getFirstName());
        } else {
            creationName.setText(audioList.get(0));
        }

        audioList.remove(0);

        Media playing = mediaList.remove(0);
        audioPlayer = new MediaPlayer(playing);
        audioPlayer.play();
        audioPlayer.setOnReady(() -> progressBar.setProgress(0.0));
        audioPlayer.setOnReady(this::progressBar);
        audioPlayer.setOnEndOfMedia(() -> {
            playMediaTracks(mediaList, audioList, isFirstName);
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
            System.err.println(e);
        }
    }


    @FXML
    public void playSingleButtonClicked(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            playSingleName(false);
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
    void compareButtonClicked() throws IOException {

        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        SetUp.getInstance().compareMenuController.setUp(creationsListView.getSelectionModel().getSelectedItem());
        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) compareButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void shuffleButtonClicked() {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        if (creationsListView.getSelectionModel().getSelectedItem()==null){
            creationsListView.getSelectionModel().selectFirst();
        }

        //Shuffle list
        Collections.shuffle(creationsListView.getItems());
        creationList = new ArrayList<>(new ArrayList<>(creationsListView.getItems()));
    }

    void setUpList(List<String> list) throws IOException {

        //Set up the practice list view
        creationList = list;
        pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();

        playPauseButton.setDisable(false);
        if (creationList.size()<=1){
            playPauseButton.setDisable(true);
            shuffleButton.setDisable(true);
        }

        //Set the list view with creations
        creationsListView.getItems().setAll(creationList);
        creationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //Create hashmap of audio files
        String folderName = "created_names/";
        File[] listFiles = new File(folderName).listFiles();
        Arrays.sort(listFiles);

        hashMap = new HashMap<>();

        for (int i = 0; i < listFiles.length; i++) {
            Media media = new Media(listFiles[i].toURI().toString());
            Creation c = new Creation(creationsListView.getItems().get(i),media);
            hashMap.put(creationsListView.getItems().get(i),c);
        }

        creationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            creationName.setText(creationsListView.getSelectionModel().getSelectedItem());
            if(creationsListView.getSelectionModel().getSelectedItems().size() != 1) {
                //If nothing is selected, disable the buttons
                playSingleButton.setDisable(true);
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

    public void setDefault(String name, Media file){

        Creation c = new Creation(name, file);
        hashMap.put(name,c);
    }

    public Media getDefault(String name){
        return hashMap.get(name).getMedia();
    }

    @FXML
    public void firstNamePlayClicked() throws IOException {
        playSingleName(true);
    }

    @FXML
    public void firstNamesListPlayClicked() throws IOException, InterruptedException {
        playNamesList(true);
    }

    private void playSingleName(boolean isFirstName) throws IOException {
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }

        if (creationsListView.getSelectionModel().getSelectedItem()==null){
            creationsListView.getSelectionModel().selectFirst();
        }

        //Change the label of the practice menu to the name being played
        selectedName = creationsListView.getSelectionModel().getSelectedItem();
        int selectedIndex = creationsListView.getSelectionModel().getSelectedIndex();
        Media media;

        if (isFirstName) {
            creationName.setText(hashMap.get(selectedName).getFirstName());
            media = hashMap.get(selectedName).getFirstNameMedia();
        } else {
            creationName.setText(selectedName);
            media = hashMap.get(selectedName).getMedia();
        }

        audioPlayer = new MediaPlayer(media);

        audioPlayer.setOnPlaying(new AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
        audioPlayer.play();
        audioPlayer.setOnReady(() -> progressBar.setProgress(0.0));
        audioPlayer.setOnReady(this::progressBar);
    }

    private void playNamesList(boolean isFirstName) throws IOException, InterruptedException {
        creationsListView.getSelectionModel().clearSelection();

        if (isFinished) {
            mediaPlayerCreator(isFirstName);
        } else if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            shuffleButton.setDisable(false);
            playPauseButton.setDisable(false);
            backButton.setDisable(false);
            playSingleButton.setDisable(true);
            audioPlayer.pause();
            timeline.pause();
        } else if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            audioPlayer.play();
            timeline.play();
        } else {
            mediaPlayerCreator(isFirstName);
        }

    }
}


