package model.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CompareMenuController {

    @FXML
    private Button backButton;

    @FXML
    private ListView<?> recordingsList;

    @FXML
    private ProgressBar existProgressBar;

    @FXML
    private Button sadFaceButton;

    @FXML
    private Button playExistingBtn;

    @FXML
    private StackPane twoReplayButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button oneReplayButton;

    @FXML
    private Button threeReplayButton;

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void badRecordingsPressed(ActionEvent event) {

    }

    @FXML
    void oneReplayButtonClicked(MouseEvent event) {

    }

    @FXML
    void playExistingBtnPressed(ActionEvent event) {

    }

    @FXML
    void playPauseButtonClicked(MouseEvent event) {

    }

    @FXML
    void sadFaceButtonClicked(MouseEvent event) {

    }

    @FXML
    void threeReplayButtonClicked(MouseEvent event) {

    }

    @FXML
    void twoReplayButtonClicked(MouseEvent event) {

    }

}
