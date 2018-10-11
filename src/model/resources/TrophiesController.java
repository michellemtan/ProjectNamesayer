package model.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class TrophiesController {

    //TODO: WE DON'T NEED THIS ANYMORE YAY

    @FXML
    private Button backButton;

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }
}
