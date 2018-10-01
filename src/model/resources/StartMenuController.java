package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class StartMenuController {

    @FXML
    private Button micButton;

    @FXML
    private Button ratingsButton;

    @FXML
    private Button startButton;

    @FXML
    private Button instructionsButton;

    @FXML
    private Button trophiesButton;

    @FXML
    void instructionsButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().instructionsMenu;
        Stage window = (Stage) instructionsButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void trophiesButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().trophiesMenu;
        Stage window = (Stage) trophiesButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void micButtonClicked(MouseEvent event) {

    }

    @FXML
    void ratingsButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().audioRatingsMenu;
        Stage window = (Stage) ratingsButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void startButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(scene);
    }
}
