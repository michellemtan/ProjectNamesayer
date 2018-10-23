package app.resources;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ExitPracticeMenuController {

    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML BorderPane borderPane;

    //Run on cancel button being pressed
    @FXML
    void cancelButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.setScene(scene);

    }

    //Run on confirm button being pressed
    @FXML
    void confirmButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().enterNamesMenu;
        Stage window = (Stage) confirmButton.getScene().getWindow();
        window.setScene(scene);
        deleteFromFolder(); //Delete recordings
    }

    //Add correct custom CSS to background upon loading of FXML in setup
    @FXML
    private void initialize(){
        borderPane.getStyleClass().add("root-clear");
    }


    //Helper method to remove recorded names once left practise (session ended)
    private void deleteFromFolder(){
        File dir = new File("recorded_names");
        for(File file: Objects.requireNonNull(dir.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
}
