package model.resources;

import javafx.animation.Timeline;
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
    private List<String> creationList;
    private String pathToDB;
    private Timeline timeline;
    private HashMap<String,Creation> hashMap;


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

        creationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
        //TODO: disable compare while name is playing
        SetUp.getInstance().compareMenuController.setUp(hashMap.get(creationsListView.getSelectionModel().getSelectedItem()));
        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) compareButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void shuffleButtonClicked() {
        //TODO: disable shuffle while audio is playing
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
    private void playSinglePressed() {
        //Get list of media from creation object and pass to play list method
        List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
        playList(fullNameMedia);
    }

    //When given a list of media, plays all items consecutively
    private void playList(List<Media> fullNameMedia) {
        MediaPlayer audioPlayer = new MediaPlayer(fullNameMedia.get(0));
        audioPlayer.setAutoPlay(true);
        fullNameMedia.remove(0);

        if(fullNameMedia.size() > 0) {
            audioPlayer.setOnEndOfMedia(() -> playList(fullNameMedia));
        }
    }


}
