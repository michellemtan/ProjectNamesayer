package model.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnterNamesController {

    @FXML private Text backButton;
    @FXML private SplitPane splitPane;
    @FXML private ListView<String> databaseNamesListView;
    @FXML private Button addButton;
    @FXML private AnchorPane namesAnchor;
    @FXML private ListView<String> practiceNamesListView;
    @FXML private TextField filteredInput;
    @FXML private Button expandButton;
    @FXML private Button practiceButton;
    @FXML private Label dbName;
    @FXML private TextField nameInput;
    @FXML private ListView<String> transparentListView;
    private List<String> allNames;


    public void initialize() {

        //Disable button when no name has been entered
        practiceButton.setDisable(true);

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

        practiceNamesListView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);

    }

    void setUpList(List<String> listNames, String name) {
        //Assign names field
        allNames = listNames;
        //Clear then fill & sort list
        databaseNamesListView.getItems().clear();
        databaseNamesListView.getItems().addAll(listNames);
        databaseNamesListView.getItems().sort(String.CASE_INSENSITIVE_ORDER);
        //Set label to reflect current database
        dbName.setText(name);

        //Apply layout
        splitPane.requestLayout();
        splitPane.applyCss();
        //Consume mouse dragged event to disable divider
        Node divider = splitPane.lookup(".split-pane-divider");
        divider.setOnMouseDragged(Event::consume);

        //Create observable list of data
        listNames.sort(String.CASE_INSENSITIVE_ORDER);
        ObservableList<String> data = FXCollections.observableArrayList(listNames);

        FilteredList<String> filteredData = new FilteredList<>(data, s -> true);

        filteredInput.textProperty().addListener(obs->{
            String filter = filteredInput.getText();
            if(filter == null || filter.length() == 0) {
                filteredData.setPredicate(s -> true);
            }
            else {
                filteredData.setPredicate(s -> s.contains(filter));
            }
        });
        //TODO: Make search work case insensitive
        //Update list view with filtered data if there is any
        filteredData.addListener((ListChangeListener<String>) c -> {
            databaseNamesListView.getItems().clear();
            databaseNamesListView.getItems().addAll(filteredData);
            databaseNamesListView.getItems().sorted(String.CASE_INSENSITIVE_ORDER);
        });
    }

    @FXML
    void addButtonClicked() {
        String input = nameInput.getText();
        if(input != null && !input.isEmpty()) {
            //Check if added name is available
            String[] split = input.split("[-\\s]");
            for(int i=0; i<split.length; i++) {
                if(!allNames.contains(split[i])) {
                    split[i] = "*" + split[i] + "*";
                }
            }
            //Add string to list view //TODO: re-add hyphen if present
            StringBuilder builder = new StringBuilder();
            for(String s : split) {
                builder.append(s + " ");
            }
            String str = builder.toString().trim();
            practiceNamesListView.getItems().add(str);
            //Clear textfield
            nameInput.clear();

            //Enable practice button after a name has been added
            practiceButton.setDisable(false);
        }
    }

    //Call addButtonClicked if user presses enter from add name text field
    @FXML
    private void enterName(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            addButtonClicked();
        }
    }

    @FXML
    void backButtonClicked() throws IOException {
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void expandButtonClicked() {

    }

    @FXML
    void practiceButtonClicked() throws IOException {

        //Send names to practice menu
        List<String> practiceNames = practiceNamesListView.getItems();
        List<String> tempNames = new ArrayList<>();

        if (practiceNamesListView.getItems().size()>0) {

            //Don't play names that don't exist
            for (String name : practiceNames) {
                if (!name.contains("*")) {
                    tempNames.add(name);
                }
            }

            //Change to practice menu and set the path to have come from the enter names menu
            SetUp.getInstance().practiceMenuController.setUpList(tempNames);
            SetUp.getInstance().exitPracticeMenuController.setPreviousScene("enterNamesMenu");
            Scene scene = SetUp.getInstance().practiceMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        }
    }

    //Clear textfield if escape key pressed
    @FXML
    private void clearTextField(KeyEvent e) {
        if(e.getCode() == KeyCode.ESCAPE) {
            filteredInput.clear();
        }
    }
}
