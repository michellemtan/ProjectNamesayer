package model.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class CompareMenuController {

    @FXML
    private Button backButton;

    @FXML
    private ProgressBar existingProgressBar;

    @FXML
    private Button listButton;

    @FXML
    private Button ratingButton;

    @FXML
    private Button playExistingButton;

    @FXML
    private ProgressBar recordProgressBar;

    @FXML
    private Button recordButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button repeatButton;

    @FXML
    private TextField textField;

    @FXML
    private Button micButton;

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }


    @FXML
    void badRecordingsPressed(ActionEvent event) {
    //TODO: FIND WHERE THIS IS FROM??
    }

    @FXML
    void listButtonClicked(MouseEvent event) {

    }

    @FXML
    void playExistingButtonClicked(MouseEvent event) {

    }

    @FXML
    void playPauseButtonClicked(MouseEvent event) {

    }

    @FXML
    void ratingButtonClicked(MouseEvent event) throws IOException {
        //TODO: CHANGE THIS SO RATING THE NAME APPEARS, AND CLICKING ON CONTEXT MENU DOES THIS
        SetUp.getInstance().audioRatingsController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) ratingButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void recordButtonClicked(MouseEvent event) {

    }

    @FXML
    void repeatButtonClicked(MouseEvent event) {

    }

    @FXML
    void micButtonClicked(MouseEvent event) throws IOException {
        SetUp.getInstance().microphoneController.setPreviousScene("compareMenu");
        Scene scene = SetUp.getInstance().microphoneMenu;
        Stage window = (Stage) micButton.getScene().getWindow();
        window.setScene(scene);
    }

}
