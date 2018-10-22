package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.*;

public class PractiseMenuController {

    //TODO: DISABLE SHUFFLE AND LIST PLAY BUTTON WHEN THERE IS ONLY ONE NAME

    //TODO: update label to reflect name being said when list is playing

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
    MediaPlayer audioPlayer;
    private boolean isPlaying;
    private List<String> creationList;
    private String pathToDB;
    private Timeline timeline;
    private HashMap<String,Creation> hashMap;


    private void disableAllButtons(boolean value) {
        if(value) {
            playPauseButton.setDisable(true);
            playSingleButton.setDisable(true);
            shuffleButton.setDisable(true);
            compareButton.setDisable(true);
        } else {
            playPauseButton.setDisable(false);
            playSingleButton.setDisable(false);
            shuffleButton.setDisable(false);
            compareButton.setDisable(false);
        }
    }

    private void disableMostButtons(boolean value) {
        if(value) {
            playSingleButton.setDisable(true);
            shuffleButton.setDisable(true);
            compareButton.setDisable(true);
        } else {
            playSingleButton.setDisable(false);
            shuffleButton.setDisable(false);
            compareButton.setDisable(false);
        }
    }

    void setUpList(List<String> list) throws IOException, UnsupportedAudioFileException {
        //Listener to change label to selected name
        creationsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newI) -> {
            creationName.setText(creationsListView.getSelectionModel().getSelectedItem());
        });

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


        hashMap = new HashMap<>();

        for (String creationName: creationsListView.getItems()){
            Creation c = new Creation(creationName);
            hashMap.put(creationName, c);
        }

        setUpTitle();
    }

    private void setUpTitle(){
        if (creationList!=null){
            creationsListView.getSelectionModel().select(0);
            creationName.setText(creationList.get(0));
        }
    }

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
    void compareButtonClicked() throws IOException {
        SetUp.getInstance().compareMenuController.setUp(hashMap.get(creationsListView.getSelectionModel().getSelectedItem()));
        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) compareButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void shuffleButtonClicked() {
        if (creationsListView.getSelectionModel().getSelectedItem()==null){
            creationsListView.getSelectionModel().selectFirst();
        }
        //Shuffle list
        Collections.shuffle(creationsListView.getItems());
        creationList = new ArrayList<>(new ArrayList<>(creationsListView.getItems()));
    }

    HashMap<String,Creation> getHashMap(){
        return hashMap;
    }

    //Method run when play single button pressed
    @FXML
    private void playSingleNamePressed() {
        disableAllButtons(true);
        //Get list of media from creation object and pass to play list method
        Creation creation = hashMap.get(creationsListView.getSelectionModel().getSelectedItem());
        List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
        playList(fullNameMedia);

        //Set up timeline to run desired length of time
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame((new Duration(creation.getCreationLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setOnFinished(e -> disableAllButtons(false));
        timeline.setCycleCount(1);
        timeline.play();

    }

    @FXML
    private void playSingleFirstNamePressed() {
        disableAllButtons(true);
        Creation creation = hashMap.get(creationsListView.getSelectionModel().getSelectedItem());
        List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
        List<Media> firstNameOnly = new ArrayList<>();
        firstNameOnly.add(fullNameMedia.get(0));

        playList(firstNameOnly);

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame((new Duration(creation.getFirstNameLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setOnFinished(e -> disableAllButtons(false));
        timeline.setCycleCount(1);
        timeline.play();
    }

    @FXML
    private void playListNamesPressed() {
        //Disable clicking things in list while audio is playing
        creationsListView.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if(isPlaying) {
                mouseEvent.consume();
            }
        });

        //If playing, stop
        if(isPlaying) {
            timeline.stop();
            progressBar.setProgress(0.0);
            audioPlayer.stop();
            isPlaying = false;
            playPauseButton.setText("List  ▶");
            disableMostButtons(false);
        } else {
            playPauseButton.setText("Stop ■");
            disableMostButtons(true);
            creationsListView.getSelectionModel().selectFirst();
            //Disable selecting

            List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
            playList(fullNameMedia);
            playListTimeline();
        }
    }

    //Method called by play list names - recursively calls itself until end of list is reached
    private void playListTimeline() {
        isPlaying = true;
        Creation creation = hashMap.get(creationsListView.getSelectionModel().getSelectedItem());
        //Set up timeline to run desired length of time
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame((new Duration(creation.getCreationLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setOnFinished(e -> {
            creationsListView.getSelectionModel().selectNext();
            if(!creationsListView.getSelectionModel().getSelectedItem().equals(creationsListView.getItems().get(creationsListView.getItems().size() - 1))) {
                playList(hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia());
                playListTimeline();
            } else {
                playLast();
            }
        });
        timeline.setCycleCount(1);
        timeline.play();
    }

    //Helper method to play last item in list, invoked by play list timeline
    private void playLast() {
        Creation creation = hashMap.get(creationsListView.getSelectionModel().getSelectedItem());
        List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
        playList(fullNameMedia);
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame((new Duration(creation.getCreationLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setOnFinished(e -> {
            disableAllButtons(false);
            isPlaying = false;
        });
        timeline.setCycleCount(1);
        timeline.play();
    }

    //When given a list of media, plays all items consecutively
    private void playList(List<Media> fullNameMedia) {
        audioPlayer = new MediaPlayer(fullNameMedia.get(0));
        audioPlayer.setAutoPlay(true);
        fullNameMedia.remove(0);

        if(fullNameMedia.size() > 0) {
            audioPlayer.setOnEndOfMedia(() -> playList(fullNameMedia));
        }
    }


}
