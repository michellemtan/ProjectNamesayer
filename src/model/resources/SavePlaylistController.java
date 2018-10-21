package model.resources;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.FileWriter;
import java.io.IOException;

public class SavePlaylistController extends AbstractController {

    @FXML private TextField input;
    @FXML private BorderPane bground;

    public void setUp(String name) {
        bground.getStyleClass().add("root-clean");
    }

    @FXML
    private void extBtnPressed() {
        if(stage!=null) {
            stage.close();
        }
    }

    @FXML
    private void tickPressed() {
        String name = input.getText();
        if(name != null && name.length() > 0) {
            try {
                FileWriter writer = new FileWriter("saved_playlists/" + input.getText() + ".txt");
                for (String str : SetUp.getInstance().enterNamesController.getNamesList()) {
                    writer.write(str + "\n");
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("Error writing to file");
            }
        }
        stage.close();
    }

    //Call addButtonClicked if user presses enter from add name text field
    @FXML
    private void enterName(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            tickPressed();
        }
    }

}
