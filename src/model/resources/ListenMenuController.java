package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ListenMenuController {

    @FXML private Button backBtn;

    public void backBtnPressed() throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

}
