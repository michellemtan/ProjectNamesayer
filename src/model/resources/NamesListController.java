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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.*;

public class NamesListController {

    @FXML private Button playButton;
    @FXML private Button setDefaultBtn;
    @FXML private Label defaultLabel;
    @FXML private ProgressBar progressBar;
    @FXML private ListView<String> nameListView;
    @FXML private Button backBtn;
    @FXML private Button ratingsButton;
    @FXML private ComboBox<String> nameMenu;
    private MediaPlayer audioPlayer;
    private String pathToDB;
    private Creation creation;

    @FXML
    public void audioRatingsPressed() throws IOException {
        //Pass current class through to bad recordings
        SetUp.getInstance().audioRatingsController.setPreviousScene("namesListMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void backBtnPressed() throws IOException {
        //Clear list view
        nameListView.getItems().removeAll(nameListView.getItems());
        nameListView.refresh();

//        ConcatService service = new ConcatService();
//        service.setOnSucceeded(concatEvent -> {
//            //Change to practice menu and set the path to have come from the load files menu
//            try {
//
//                String folderName = "created_names/";
//                String tempName = fileName.replaceAll(" ", "");
//                File f = new File(folderName + tempName+".wav");
//                Media media = new Media(f.toURI().toString());
//                SetUp.getInstance().practiceMenuController.setDefault(fileName, media);

                Scene scene = SetUp.getInstance().compareMenu;
                Stage window = (Stage) backBtn.getScene().getWindow();
                window.setScene(scene);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        service.start();
    }
//
//    private class ConcatService extends Service<Void> {
//        @Override
//        protected Task<Void> createTask() {
//            return new Task<Void>() {
//                @Override
//                protected Void call() throws IOException, InterruptedException {
//                    String pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();
//
//                        String concatString;
//
//                        for (int i = 0; i < nameMenu.getItems().size(); i++) {
//                            concatString = defaultFileMap.get(nameMenu.getItems().get(i)).getPath();
//                            addToTextFile(concatString);
//                        }
//
//                        String tempName = fileName.replaceAll(" ","");
//                        ProcessBuilder audioBuilder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -safe 0 -f concat -i ConcatNames.txt -c copy ./created_names/" + tempName+".wav");
//                        Process p = audioBuilder.start();
//                        p.waitFor();
//                        PrintWriter writer = new PrintWriter("ConcatNames.txt", "UTF-8");
//
//                    return null;
//                    }
//                };
//            }
//        }

//    private void addToTextFile(String name) throws IOException {
//        File f = new File("ConcatNames.txt");
//        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
//        bw.append("file '"+name+"'\n");
//        bw.flush();
//        bw.close();
//    }

    @FXML
    void playButtonClicked() throws IOException, UnsupportedAudioFileException {
        //Stop audio player if there's currently one playing
        if (audioPlayer != null && audioPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            audioPlayer.stop();
        }
        //Get strings of audio file paths
        if (nameListView.getSelectionModel().getSelectedIndex()<0) {
            nameListView.getSelectionModel().selectFirst();
        }

        int selectedIndex = nameListView.getSelectionModel().getSelectedIndex();

        String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
        File[] listFiles = new File(folderName).listFiles();

        //Calculate length of audio clip
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(listFiles)[0]);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        double length = (frames+0.0) / format.getFrameRate() + 0.2;


        //Create and start media player
        Media media = new Media(listFiles[selectedIndex].toURI().toString());
        audioPlayer = new MediaPlayer(media);
        audioPlayer.setOnPlaying(new AudioRunnable(false));
        audioPlayer.setOnEndOfMedia(new AudioRunnable(true));
        audioPlayer.play();
        //When audio player is ready, play & set progress bar to run the length of the audio clip
        audioPlayer.setOnReady(() -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                    new KeyFrame(new Duration(length * 1000), new KeyValue(progressBar.progressProperty(), 1))
            );
            timeline.setOnFinished(e -> progressBar.setProgress(0.0));
            timeline.setCycleCount(1);
            timeline.play();
        });

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
    void ratingButtonClicked(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            PopupWindow p = new PopupWindow("model/views/RatingsMessage.fxml", true, creation.getFullName());
        }
    }

    @FXML
    void setDefaultClicked() {
        int selectedIndex = nameListView.getSelectionModel().getSelectedIndex();
        String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
        File[] listFiles = new File(folderName).listFiles();
        Media m = new Media(listFiles[selectedIndex].toURI().toString());

        //Set default audio file
        creation.setDefaultMedia(nameMenu.getSelectionModel().getSelectedItem(), m);
        defaultLabel.setText("Default: " + listFiles[selectedIndex].getName());

    }

    @FXML
    void setUp(Creation c) throws IOException {

        creation = c;
        String fullName = c.getFullName();
        nameListView.getItems().removeAll(nameListView.getItems());

        //Disable buttons
        playButton.setDisable(true);
        setDefaultBtn.setDisable(true);

        //This method sets up the combo box to display all the parts of a name
        pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();

        String[] split = fullName.replaceAll("-", "- ").split("[\\s]");

        ObservableList<String> options = FXCollections.observableArrayList();

        //Add split to options list
        Collections.addAll(options, split);

        //Set the combo box with options
        nameMenu.getItems().setAll(options);
        nameMenu.getSelectionModel().selectFirst();

        //Add listener to listview
        nameListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (nameListView.getSelectionModel().getSelectedItems().size() != 1) {
                setDefaultBtn.setDisable(true);
                playButton.setDisable(true);
            } else {
                setDefaultBtn.setDisable(false);
                playButton.setDisable(false);
            }
        });

        //Add listener to combo option box
        nameMenu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (nameMenu.getSelectionModel().getSelectedIndex()<0){
                playButton.setDisable(true);
                setDefaultBtn.setDisable(true);
            } else {
                playButton.setDisable(false);
                setDefaultBtn.setDisable(false);
            }
        });
        nameMenuAction();
    }

    @FXML
    void nameMenuAction() {

        if (nameMenu.getSelectionModel().getSelectedIndex() >= 0) {
            String folderName = pathToDB + "/" + nameMenu.getSelectionModel().getSelectedItem() + "/";
            File[] listFiles = new File(folderName).listFiles();
            List<String> displayList = new ArrayList<>();

            for (File f : listFiles) {
                displayList.add(f.getName());
            }
            nameListView.getItems().setAll(displayList);

            //Set up the default label
            File f = new File(creation.getFullNameHashMap().get(nameMenu.getSelectionModel().getSelectedItem()).getSource());
            defaultLabel.setText("Default: " + f.getName());
        }
    }
}
