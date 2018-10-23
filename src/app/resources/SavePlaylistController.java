package app.resources;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.FileWriter;
import java.io.IOException;

public class SavePlaylistController extends AbstractController {

    @FXML private TextField input;
    @FXML private BorderPane bground;
    @FXML private Button tickButton;

    //Helper method to set custom CSS background
    public void setUp(String name) {
        bground.getStyleClass().add("root-clean");
    }

    //Run on exit button being pressed
    @FXML
    private void extBtnPressed() {
        if(stage!=null) {
            stage.close();
        }
    }

    //Run on tick button being pressed
    @FXML
    private void tickPressed() {
        //Get input from textfield & try to write to file
        String name = input.getText();
        if(name != null && name.length() > 0) {
            try {
                FileWriter writer = new FileWriter("saved_playlists/" + input.getText() + ".txt");
                for (String str : SetUp.getInstance().enterNamesController.getNamesList()) {
                    writer.write(str + "\n");
                }
                writer.close();
                //Show tool tip when a name has been rated
                Tooltip customTooltip = new CustomTooltip(stage, tickButton, "Created playlist!", null);
            } catch (IOException e) {
                System.out.println("Error writing to file");
            }
        }
        stage.close();
    }

    //Call addButtonClicked if user presses enter from add name text field (shortcut)
    @FXML
    private void enterName(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            tickPressed();
        }
    }
}
