package model.resources;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class OverwriteRecordingController {

    @FXML
    private Button yesButton;

    @FXML
    private Button cancelButton;

    private Stage stage = null;
    private boolean isOverwritten;

    @FXML
    void cancelButtonClicked(MouseEvent event) {
        isOverwritten = false;
        if(stage!=null) {
            stage.close();
        }
    }

    @FXML
    void yesButtonClicked(MouseEvent event) {
        isOverwritten = true;
        if(stage!=null) {
            stage.close();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean getResult(){
        return isOverwritten;
    }
}
