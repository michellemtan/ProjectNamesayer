package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class EnterNamesController {

    @FXML private Text backButton;
    @FXML private SplitPane splitPane;
    @FXML private Button searchButton;
    @FXML private ListView<String> databaseNamesLIstView;
    @FXML private Button hideButton;
    @FXML private Button addButton;
    @FXML private ListView<String> practiceNamesListView;
    @FXML private Button expandButton;
    @FXML private Button practiceButton;

    //Got code from https://stackoverflow.com/questions/44358394/animate-splitpane-divider
    public void initialize() {
        splitPane.setDividerPositions(0);
        BooleanProperty collapsed = new SimpleBooleanProperty();
        collapsed.bind(splitPane.getDividers().get(0).positionProperty().isEqualTo(0.0, 0.01));

        expandButton.textProperty().bind(Bindings.when(collapsed).then("Expand ▶").otherwise("Collapse ◀"));

        expandButton.setOnAction(e -> {
            //System.out.println(splitPane.getDividers().get(0).positionProperty());
            double target = collapsed.get() ? 0.5 : 0.0 ;
            KeyValue keyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), target);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), keyValue));
            timeline.play();
        });
    }

    @FXML
    void addButtonClicked() {

    }

    @FXML
    void backButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void expandButtonClicked() {
        //splitPane.setDividerPositions(1);

    }

    @FXML
    void hideButtonClicked() {
        //splitPane.setDividerPositions(0.005);
    }

    @FXML
    void practiceButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void searchButtonClicked() {

    }

}
