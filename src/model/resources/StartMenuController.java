package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class StartMenuController {

    @FXML private Button ratingsButton;
    @FXML private Button startButton;

    @FXML
    void settingsBtnPressed() throws IOException {
        SetUp.getInstance().settingsMenuController.startMicVol();
        Scene scene = SetUp.getInstance().settingsMenu;
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void ratingsButtonClicked() throws IOException {
        SetUp.getInstance().audioRatingsController.setPreviousScene("startMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) ratingsButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void startButtonClicked() throws IOException {
        SetUp.getInstance().settingsMenuController.setUpNameLists();
        Scene scene = SetUp.getInstance().enterNamesMenu;
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(scene);
    }
}
