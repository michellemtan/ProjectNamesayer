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

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PracticeMenuController {

    @FXML private Button playPauseButton;
    @FXML private Button sadFaceButton;
    @FXML private Button detailsButton;
    @FXML private Button shuffleButton;
    @FXML private Button recordButton;
    @FXML private ProgressBar progressBar;
    @FXML private ListView<String> creationsListView;
    @FXML private Label creationName;
    @FXML private Button backButton;
    @FXML private Button playSingleButton;
    private MediaPlayer audioPlayer;
    private List<String> creationList;
    private String selectedName;
    private String pathToDB;
    private ObservableList<Media> mediaList;
    private boolean isFinished;
    private Timeline timeline;
    private List<String> deleteList;

    public void clearListView() {
        creationsListView.getItems().clear();
    }

    //Fill list with selected items
    public void setUpList(List<String> list) throws IOException {
        creationList = list;
        pathToDB = SetUp.getInstance().dbMenuController.getPathToDB();
        playPauseButton.setDisable(false);
        if (creationList.size()<=1){
            playPauseButton.setDisable(true);
            shuffleButton.setDisable(true);
        }
        creationsListView.getItems().setAll(creationList);
        creationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pathToDB = SetUp.getInstance().dbMenuController.getPathToDB();


        creationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            creationName.setText(creationsListView.getSelectionModel().getSelectedItem());
            if(creationsListView.getSelectionModel().getSelectedItems().size() != 1) {
                playSingleButton.setDisable(true);
                detailsButton.setDisable(true);
                recordButton.setDisable(true);
                sadFaceButton.setDisable(true);
            } else {
                playSingleButton.setDisable(false);
                detailsButton.setDisable(false);
                recordButton.setDisable(false);
                sadFaceButton.setDisable(false);
            }
        });

        setUpTitle();
    }

    @FXML
    public void badRecordingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("practiceMenu");
        Scene scene = SetUp.getInstance().badRecordingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    private void setUpTitle(){
        if (creationList!=null){
            creationsListView.getSelectionModel().select(0);
            creationName.setText(creationList.get(0));
        }
    }

    @FXML
    void backButtonClicked() throws IOException {
        // Load the new scene
        //Scene scene = backButton.getScene();
        //scene.setRoot(Menu.EXITPRACTICEMENU.loader().load());

        Scene scene = SetUp.getInstance().exitPracticeMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void detailsButtonClicked() throws IOException {

        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        // Load the new scene
        //Scene scene = detailsButton.getScene();
        //scene.setRoot(Menu.NAMEDETAILSMENU.loader().load());

        //Set selected name
        selectedName = creationsListView.getSelectionModel().getSelectedItem();
        //Initialise list of name to
        List<String> list = new ArrayList<>();
        //Add all files in directory to list
        File dir = new File(pathToDB + "/" + selectedName);
        File[] files = dir.listFiles();
        for(File file : files) {
            list.add(file.getName());
        }

        //Add list to name details menu
        SetUp.getInstance().nameDetailsController.setUpList(list, selectedName, "practice");

        String defaultName = SetUp.getInstance().nameDetailsController.returnDefault(selectedName);

        Scene scene = SetUp.getInstance().nameDetailsMenu;
        Stage window = (Stage) detailsButton.getScene().getWindow();
        window.setScene(scene);
    }


    //Method to delete files if coming from the NameDetails menu
    public void deleteAudioFiles(String toDelete) throws IOException {

        String dirName = SetUp.getInstance().nameDetailsController.getName();
        File dir = new File(SetUp.getInstance().dbMenuController.getPathToDB() + "/" + dirName);
        //If deleting the default file, must set another to be default
        if(toDelete.equals(SetUp.getInstance().nameDetailsController.returnDefault(dirName))) {
            if(Objects.requireNonNull(dir.listFiles()).length <= 1) {
                //If was the last file and deleting, no need to set default just delete the file
                File fileToDelete = new File(SetUp.getInstance().dbMenuController.getPathToDB() + "/" + dirName + "/" + toDelete);
                fileToDelete.delete();
            } else {
                //If not last file, a new default must be set
                File fileToDelete = new File(SetUp.getInstance().dbMenuController.getPathToDB() + "/" + dirName + "/" + toDelete);
                fileToDelete.delete();
                File[] listFiles = dir.listFiles();
                File newDefault = listFiles[0];
                SetUp.getInstance().nameDetailsController.setDefaultOnDelete(newDefault.getName());
            }
        }

        File fileToDelete = new File(dir.getPath() + "/" + toDelete);
        fileToDelete.delete();

        //Try to delete directory, will only work if non-empty - correct behaviour
        if(dir.delete()) {
            //Remove name from list if directory now empty
            creationsListView.getItems().remove(dirName);
            //Remove from dbListView also
            SetUp.getInstance().dbMenuController.removeListItem(dirName);
        }
        deleteList = creationsListView.getItems();
    }

    @FXML
    void playButtonClicked() throws IOException {

        if (isFinished) {
            mediaPlayerCreator();
        } else if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            shuffleButton.setDisable(false);
            playPauseButton.setDisable(false);
            backButton.setDisable(false);
            recordButton.setDisable(false);
            detailsButton.setDisable(false);
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


    @FXML
    void recordButtonClicked() throws IOException {

        if (audioPlayer != null) {
            audioPlayer.stop();
        }

        SetUp.getInstance().recordMenuController.setUpRecord(creationsListView.getSelectionModel().getSelectedItem());

        Scene scene = SetUp.getInstance().recordMenu;
        Stage window = (Stage) recordButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void shuffleButtonClicked() {

        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        //Shuffle list
        Collections.shuffle(creationsListView.getItems());
        creationList = new ArrayList<>(new ArrayList<>(creationsListView.getItems()));
    }

    private void mediaPlayerCreator() throws IOException {

        List<String> audioList = new ArrayList<>(new ArrayList<>(creationsListView.getItems()));
        mediaList = FXCollections.observableArrayList();
        String databasePath = SetUp.getInstance().dbMenuController.getPathToDB();

        //System.out.println("Creation list size: " + audioList.size());

        for (String creation : audioList) {
            //Set up the file to be played
            selectedName = creation;
            String defaultName = SetUp.getInstance().nameDetailsController.returnDefault(selectedName).replaceAll(".wav", "");
            Media media = new Media(new File(databasePath + "/" + selectedName + "/" + defaultName).toURI().toString() + ".wav");
            mediaList.add(media);
        }

        playMediaTracks(mediaList, audioList);
    }


    private void playMediaTracks(List<Media> mediaList, List<String> audioList) {

        if (mediaList.size() == 0) {
            isFinished = true;
            detailsButton.setDisable(false);
            shuffleButton.setDisable(false);
            backButton.setDisable(false);
            recordButton.setDisable(false);
            playSingleButton.setDisable(false);
            return;
        } else {
            isFinished = false;
            backButton.setDisable(true);
            recordButton.setDisable(true);
            detailsButton.setDisable(true);
            shuffleButton.setDisable(true);
            playSingleButton.setDisable(true);
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
    public void playSingleButtonClicked() throws IOException {

        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }

        selectedName = creationsListView.getSelectionModel().getSelectedItem();
        creationName.setText(selectedName);

        String defaultName = SetUp.getInstance().nameDetailsController.returnDefault(selectedName);
        String databasePath = SetUp.getInstance().dbMenuController.getPathToDB();

        defaultName = defaultName.replace(".wav", "");
        defaultName = defaultName.concat(".wav");
        Media media = new Media(new File(databasePath + "/" + selectedName + "/" + defaultName).toURI().toString());
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
                recordButton.setDisable(false);
                detailsButton.setDisable(false);
                playPauseButton.setDisable(false);
                audioPlayer.dispose();
                //When the media player is playing the audio file, the buttons will be disabled to prevent the user from navigating away
            } else {
                backButton.setDisable(true);
                recordButton.setDisable(true);
                detailsButton.setDisable(true);
                shuffleButton.setDisable(true);
                playPauseButton.setDisable(true);
            }
        }
    }

    @FXML
    public void sadFaceButtonClicked(MouseEvent event) throws IOException {
        String selectedName = creationsListView.getSelectionModel().getSelectedItem() + ".wav";
        String defaultName = SetUp.getInstance().nameDetailsController.checkDefault(selectedName);

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
                    defaultName = defaultName.concat(": " + rating + "\n");
                    SetUp.getInstance().audioRatingsController.addName(defaultName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String returnSelectedName(){
        String selectedName = creationsListView.getSelectionModel().getSelectedItem();
        return selectedName;
    }

    public int returnListSize() {
        return deleteList.size();
    }
}
