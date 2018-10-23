package app.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class InstructionsMenuController {

    @FXML private Button backButton;

    //Return the user to the start menu when the back button is clicked
    @FXML
    void backButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().settingsMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }
}
