package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsMenuController {

    @FXML private Button backBtn;
    @FXML private ComboBox<String> chooseDB;

    public void backBtnPressed() throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    public void initialize() {
        chooseDB.getItems().add("Default Database");
        chooseDB.getItems().add("Add new...");
        chooseDB.getSelectionModel().selectFirst();
    }

}
