package app.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class StartMenuController {

    @FXML private Button ratingsButton;
    @FXML private Button startButton;
    @FXML private Button listenBtn;
    @FXML private Button settingsBtn;

    //Run once FXML is loaded, set correct custom CSS themes
    public void initialize() {
        settingsBtn.getStyleClass().add("button-settings");
        listenBtn.getStyleClass().add("button-listen");
        startButton.getStyleClass().add("button-practice");
    }

    //Switch to settings menu & set up
    @FXML
    void settingsBtnPressed() throws IOException {
        SetUp.getInstance().settingsMenuController.startMicVol();
        Scene scene = SetUp.getInstance().settingsMenu;
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(scene);
    }

    //View ratings
    @FXML
    void ratingsButtonClicked() throws IOException {
        SetUp.getInstance().audioRatingsController.setPreviousScene("startMenu");
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) ratingsButton.getScene().getWindow();
        window.setScene(scene);

    }

    //Switch to practise menu
    @FXML
    void startButtonClicked() throws IOException {
        SetUp.getInstance().settingsMenuController.setUpNameLists();
        Scene scene = SetUp.getInstance().enterNamesMenu;
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(scene);

    }

    //Switch to listen menu
    @FXML
    void listenBtnPressed() throws IOException {
        SetUp.getInstance().settingsMenuController.setUpNameLists();
        Scene scene = SetUp.getInstance().listenMenu;
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(scene);

    }

    //Disable buttons if default database missing
    public void buttonsOff(boolean toggle) {
        if(toggle) {
            listenBtn.setDisable(true);
            startButton.setDisable(true);
        } else {
            listenBtn.setDisable(false);
            startButton.setDisable(false);
        }
    }
}
