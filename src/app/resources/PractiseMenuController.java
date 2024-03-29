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

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PractiseMenuController {

    @FXML private Button playPauseButton;
    @FXML private Button playSingleButton;
    @FXML private Button shuffleButton;
    @FXML private Button compareButton;
    @FXML private ProgressBar progressBar;
    @FXML private ListView<String> creationsListView;
    @FXML private Label creationName;
    @FXML private Button backButton;
    private MediaPlayer audioPlayer;
    private boolean isPlaying;
    private Timeline timeline;
    private HashMap<String,Creation> hashMap;
    private boolean tinyList;

    //Run on switching to this menu
    void setUpList(List<String> list) throws IOException, UnsupportedAudioFileException {
        //Create folder for recorded names
        new File("./recorded_names").mkdir();

        //Listener to change label to selected name
        creationsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newI) -> creationName.setText(creationsListView.getSelectionModel().getSelectedItem()));

        //Disable/enable buttons to prevent errors
        playPauseButton.setDisable(false);
        if (list.size()<=1){
            playPauseButton.setDisable(true);
            shuffleButton.setDisable(true);
        }
        //Set the list view with creations
        creationsListView.getItems().setAll(list);
        creationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //Instantiate hash map and populate
        hashMap = new HashMap<>();
        for (String creationName: creationsListView.getItems()){
            Creation c = new Creation(creationName);
            hashMap.put(creationName, c);
        }
        setUpTitle();

        //Boolean tiny list if the list is only containing 1 item
        tinyList = list.size() <= 0;
    }

    //Helper method to enable/disable buttons while audio playing
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
        if(tinyList) {
            playPauseButton.setDisable(true);
            shuffleButton.setDisable(true);
        }
    }

    //Hlper method to disable/enable most buttons (for list play, need stop button still enabled)
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
        if(tinyList) {
            playPauseButton.setDisable(true);
            shuffleButton.setDisable(true);
        }
    }

    //Helper method to set title to first selected item by default
    private void setUpTitle(){
        if (creationsListView.getItems()!=null){
            creationsListView.getSelectionModel().selectFirst();
            creationName.setText(creationsListView.getSelectionModel().getSelectedItem());
        }
    }

    //Run on back button being pressed
    @FXML
    void backButtonClicked() throws IOException {
        // Load the new scene - confirm if the user wants to exit practice
        Scene scene = SetUp.getInstance().exitPracticeMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    //Run on ratings button being pressed
    @FXML
    void ratingsButtonClicked(MouseEvent event) throws IOException {
        String selectedName = creationsListView.getSelectionModel().getSelectedItem();

        if (event.getButton() == MouseButton.PRIMARY) {
            PopupWindow p = new PopupWindow("app/views/RatingsMessage.fxml", true, selectedName);
        }
    }

    //Run on ratings context menu being requested
    @FXML
    public void audioRatingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("practiceMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Run on compare button being pressed
    @FXML
    void compareButtonClicked() throws IOException {
        SetUp.getInstance().compareMenuController.setUp(hashMap.get(creationsListView.getSelectionModel().getSelectedItem()));
        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) compareButton.getScene().getWindow();
        window.setScene(scene);
    }

    //Run on shuff;e button being pressed
    @FXML
    void shuffleButtonClicked() {
        if (creationsListView.getSelectionModel().getSelectedItem()==null){
            creationsListView.getSelectionModel().selectFirst();
        }
        //Shuffle list
        Collections.shuffle(creationsListView.getItems());
    }

    //Get hash map helper method
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

    //Method called when right click > play first on single name
    @FXML
    private void playSingleFirstNamePressed() {
        //Disable button while media is playing
        disableAllButtons(true);
        //Get list of media from creation app
        Creation creation = hashMap.get(creationsListView.getSelectionModel().getSelectedItem());
        List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
        //Create list containing only first media name & play
        List<Media> firstNameOnly = new ArrayList<>();
        firstNameOnly.add(fullNameMedia.get(0));

        playList(firstNameOnly);
        //Timeline that runs the correct length of only the first name
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame((new Duration(creation.getFirstNameLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setOnFinished(e -> disableAllButtons(false));
        timeline.setCycleCount(1);
        timeline.play();
    }

    //Method invoked when play list is pressed
    @FXML
    private void playListNamesPressed() {
        //Disable clicking things in list while audio is playing
        creationsListView.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if(isPlaying) {
                mouseEvent.consume();
            }
        });

        //If playing, stop reset buttons
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

            //Get media list & invoke play method & timeline
            List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
            playList(fullNameMedia);
            playListTimeline(false);
        }
    }

    //Method invoked when play list of first name pressed
    @FXML
    private void playListFirstPressed() {
        //If playing, stop & reset buttons
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

            //Make list of first name media only & call play & timeline methods
            List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
            List<Media> firstNameOnly = new ArrayList<>();
            firstNameOnly.add(fullNameMedia.get(0));

            playList(firstNameOnly);
            playListTimeline(true);
        }
    }

    //Method called by play list names - recursively calls itself until end of list is reached
    private void playListTimeline(boolean firstNames) {
        isPlaying = true;
        Creation creation = hashMap.get(creationsListView.getSelectionModel().getSelectedItem());
        //Set up timeline to run desired length of time for full names
        if(!firstNames) {
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame((new Duration(creation.getCreationLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
            );
            timeline.setOnFinished(e -> {
                creationsListView.getSelectionModel().selectNext();
                //Recursively call this method until last item in list reached
                if(!creationsListView.getSelectionModel().getSelectedItem().equals(creationsListView.getItems().get(creationsListView.getItems().size() - 1))) {
                    playList(hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia());
                    playListTimeline(false);
                //Upon last item invoke play last method
                } else {
                    playLast(false);
                }
            });
        //For first names timeline plays a shorter amount of time, code identical otherwise
        } else {
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame((new Duration(creation.getFirstNameLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
            );
            timeline.setOnFinished(e -> {
                creationsListView.getSelectionModel().selectNext();
                if(!creationsListView.getSelectionModel().getSelectedItem().equals(creationsListView.getItems().get(creationsListView.getItems().size() - 1))) {
                    //Create list of first name only
                    List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
                    List<Media> firstNameOnly = new ArrayList<>();
                    firstNameOnly.add(fullNameMedia.get(0));
                    playList(firstNameOnly);
                    playListTimeline(true);
                } else {
                    playLast(true);
                }
            });
        }
        timeline.setCycleCount(1);
        timeline.play();
    }

    //Helper method to play last full name in list, invoked by playListTimeline
    private void playLast(boolean firstNames) {
        Creation creation = hashMap.get(creationsListView.getSelectionModel().getSelectedItem());
        List<Media> fullNameMedia = hashMap.get(creationsListView.getSelectionModel().getSelectedItem()).getFullNameMedia();
        //If first names is true only play first name
        if(!firstNames) {
            playList(fullNameMedia);
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame((new Duration(creation.getCreationLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
            );
        } else {
            List<Media> firstNameOnly = new ArrayList<>();
            firstNameOnly.add(fullNameMedia.get(0));
            playList(firstNameOnly);
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame((new Duration(creation.getFirstNameLength() *1000)), new KeyValue(progressBar.progressProperty(), 1))
            );
        }
        timeline.setOnFinished(e -> {
            disableAllButtons(false);
            isPlaying = false;
            playPauseButton.setText("List  ▶");
        });
        timeline.setCycleCount(1);
        timeline.play();
    }

    //When given a list of media, plays all items consecutively
    void playList(List<Media> fullNameMedia) {
        audioPlayer = new MediaPlayer(fullNameMedia.get(0));
        audioPlayer.setAutoPlay(true);
        fullNameMedia.remove(0);

        if(fullNameMedia.size() > 0) {
            audioPlayer.setOnEndOfMedia(() -> playList(fullNameMedia));
        }
    }


}
