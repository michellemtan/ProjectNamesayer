package model.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ListenMenuController {

    @FXML private Button backBtn;
    @FXML private ListView<String> namesListView;
    @FXML private Label dbName;

    public void backBtnPressed() throws IOException {
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) backBtn.getScene().getWindow();
        window.setScene(scene);
    }

    void setUpList(List<String> listNames, String name) {
        //Clear names list and sort case insensitive order
        namesListView.getItems().clear();
        namesListView.getItems().addAll(listNames);
        namesListView.getItems().sort(String.CASE_INSENSITIVE_ORDER);
        dbName.setText(name);
    }

}
