package model.resources;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class MissingDBController extends AbstractController {

    @FXML
    public void okButtonClicked(){
        stage.close();
    }

}
