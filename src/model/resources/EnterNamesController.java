package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class EnterNamesController {

    @FXML private Text backButton;
    @FXML private SplitPane splitPane;
    @FXML private ListView<String> databaseNamesListView;
    @FXML private Button addButton;
    @FXML private AnchorPane namesAnchor;
    @FXML private ListView<String> practiceNamesListView;
    @FXML private Button expandButton;
    @FXML private Button practiceButton;
    @FXML private AnchorPane mainAnchor;
    private boolean mouseDragOnDivider = false;


    //Got code from https://stackoverflow.com/questions/44358394/animate-splitpane-divider
    public void initialize() {
        splitPane.setDividerPositions(0);
        BooleanProperty collapsed = new SimpleBooleanProperty();
        collapsed.bind(splitPane.getDividers().get(0).positionProperty().isEqualTo(0.0, 0.01));

        expandButton.textProperty().bind(Bindings.when(collapsed).then("Expand ▶").otherwise("Collapse ◀"));
        expandButton.setOnAction(e -> {
            double target = collapsed.get() ? 0.35 : 0.0 ;
            KeyValue keyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), target);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), keyValue));
            timeline.play();
        });
        practiceNamesListView.prefWidthProperty().bind(namesAnchor.widthProperty());
    }

    void setUpList(List<String> listNames) {
        databaseNamesListView.getItems().clear();
        databaseNamesListView.getItems().addAll(listNames);
        databaseNamesListView.getItems().sort(String.CASE_INSENSITIVE_ORDER);

        //Apply layout
        splitPane.requestLayout();
        splitPane.applyCss();
        //Consume mouse dragged event to disable divider
        Node divider = splitPane.lookup(".split-pane-divider");
        divider.setOnMouseDragged(Event::consume);
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
        SetUp.getInstance().exitPracticeMenuController.setPreviousScene("enterNamesMenu");
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void searchButtonClicked() {

    }

}
