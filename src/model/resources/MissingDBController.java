package model.resources;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class MissingDBController extends AbstractController {

    @FXML private Button okBtn;

    public void setUpAlert() throws IOException {
        //TODO: Not sure if this is necessary?
        File f = new File(System.getProperty("user.dir") + "/names");
        if(!f.exists() || !f.isDirectory()) {
            //Create stage and scene for small alert regarding missing database
            SetUp.getInstance().startMenuController.buttonsOff(true);
            SetUp.getInstance().settingsMenuController.disableBack(true);
            Stage stage = new Stage();
            stage.setScene(SetUp.getInstance().missingDB);
            okBtn.setOnAction(e -> stage.close());
            stage.getIcons().add(new Image("/model/resources/images/icon.png"));
            stage.setTitle("Warning");
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();
        }
    }

    @FXML
    public void okButtonClicked(){
        stage.close();
    }

}
