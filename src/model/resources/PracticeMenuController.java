package model.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class PracticeMenuController {

    @FXML
    private Button playPauseButton;

    @FXML
    private Button playSingleButton;

    @FXML
    private Button shuffleButton;

    @FXML
    private Button compareButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ListView<?> creationsListView;

    @FXML
    private Label creationName;

    @FXML
    private Button backButton;

    @FXML
    private Button ratingsButton;

    @FXML
    private ContextMenu ratingsContext;

    @FXML
    private MenuItem audioRatings;

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().exitPracticeMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void ratingsButtonClicked(ActionEvent event) throws IOException {
        //TODO: CHANGE THIS SO RATING THE NAME APPEARS, AND CLICKING ON CONTEXT MENU DOES THIS
        SetUp.getInstance().audioRatingsController.setPreviousScene("practiceMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) ratingsButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void playButtonClicked(MouseEvent event) {

    }

    @FXML
    void playSingleButtonClicked(MouseEvent event) {

    }

    @FXML
    void compareButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().compareMenu;
        Stage window = (Stage) compareButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void shuffleButtonClicked(MouseEvent event) {

    }

    @FXML
    void audioRatingsPressed(ActionEvent event){

    }
}
