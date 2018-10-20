package model.resources;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;


public class OverwriteRecordingController extends AbstractController {

    @FXML private Button yesButton;
    @FXML private Button cancelButton;
    @FXML private BorderPane bground;

    private boolean isOverwritten;

    @FXML
    void cancelButtonClicked() {
        isOverwritten = false;
        if(stage!=null) {
            stage.close();
        }
    }

    @FXML
    void yesButtonClicked() {
        isOverwritten = true;
        if(stage!=null) {
            stage.close();
        }
    }

    public void setUp(String name) {
        bground.getStyleClass().add("root-clean");
    }

    @Override
    public boolean getResult(){
        return isOverwritten;
    }
}
