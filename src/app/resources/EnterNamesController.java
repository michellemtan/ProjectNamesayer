package app.resources;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EnterNamesController {

    @FXML private SplitPane splitPane;
    @FXML private ListView<String> databaseNamesListView;
    @FXML private ListView<String> practiceNamesListView;
    @FXML private Button loadFilesBtn;
    @FXML private TextField filteredInput;
    @FXML private Button expandButton;
    @FXML private Button practiceButton;
    @FXML private Label dbName;
    @FXML private TextField nameInput;
    @FXML private Button saveBtn;
    @FXML private Tooltip listTip;
    private List<String> allNames;

    //Method invoked automatically once FXML is loaded
    public void initialize() {
        saveBtn.getStyleClass().add("button-save");
        //Cell factory to assign red CSS to selected cells in list view, and add context menu with removal options
        practiceNamesListView.setCellFactory(lv -> {
            //Set red if selected
            ListCell<String> cell = new ListCell<>();
            cell.getStyleClass().add("list-cell-red");
            ContextMenu contextMenu = new ContextMenu();
            //Create menu items for removing names & set action events
            MenuItem editItem = new MenuItem();
            editItem.textProperty().bind((Bindings.format("Remove name")));
            MenuItem editItems = new MenuItem();
            editItems.textProperty().bind((Bindings.format("Clear all names")));
            MenuItem removeInvalids = new MenuItem();
            removeInvalids.textProperty().bind((Bindings.format("Clear invalid names")));
            editItem.setOnAction(event -> practiceNamesListView.getItems().remove(cell.getText()));
            editItems.setOnAction(event -> practiceNamesListView.getItems().removeAll(practiceNamesListView.getItems()));
            removeInvalids.setOnAction(event -> practiceNamesListView.getItems().removeAll(practiceNamesListView.getSelectionModel().getSelectedItems()));
            //Add menu items and bind list to list view
            contextMenu.getItems().addAll(editItem, removeInvalids, editItems);
            //If empty set accordingly
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            cell.textProperty().bind(cell.itemProperty());
            return cell;
        });

        //Set character limit on name input text field
        nameInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (nameInput.getText().length() > 50) {
                    nameInput.setText(nameInput.getText().substring(0,50));
                }
            }
        });

        //Disable tooltip when list is empty
        practiceNamesListView.setTooltip(null);
        //Listener for items of list to select invalid ones when they're added
        practiceNamesListView.getItems().addListener((ListChangeListener<String>) c -> {
            //Enable tooltip if list has items
            if(practiceNamesListView.getItems().size() < 1) {
                practiceNamesListView.setTooltip(null);
            } else {
                practiceNamesListView.setTooltip(listTip);
            }
            //Clear selection
            practiceNamesListView.getSelectionModel().clearSelection();
            //Select invalid names
            for(int i=0; i<practiceNamesListView.getItems().size(); i++) {
                if(practiceNamesListView.getItems().get(i).contains("*")) {
                    practiceNamesListView.getSelectionModel().select(i);
                }
            }

            //Disable practice button if no names in list view
            if(practiceNamesListView.getItems().size() >= 1) {
                practiceButton.setDisable(false);
            } else {
                practiceButton.setDisable(true);
            }
        });

        //Disable button when no name has been entered
        practiceButton.setDisable(true);
        //Start expanded
        splitPane.setDividerPositions(0.35);
        BooleanProperty collapsed = new SimpleBooleanProperty();
        collapsed.bind(splitPane.getDividers().get(0).positionProperty().isEqualTo(0.0, 0.01));

        //Animate expanding/collapsing by binding to timeline
        expandButton.textProperty().bind(Bindings.when(collapsed).then("Expand ▶").otherwise("Collapse ◀"));
        expandButton.setOnAction(e -> {
            double target = collapsed.get() ? 0.35 : 0.0 ;
            KeyValue keyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), target);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), keyValue));
            timeline.play();
        });

        //Disable selecting for list view (as selected names are the red ones, can't have user selecting valid names!)
        practiceNamesListView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
        practiceNamesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    //Set up method invoked upon switching to this scene, simply loads lists & sets labels with correct names
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

        //Use filtered list to act as a search function in list view
        FilteredList<String> filteredData = new FilteredList<>(data, s -> true);
        filteredInput.textProperty().addListener(obs->{
            String filter = filteredInput.getText();
            //Code to make search work case insensitive
            String upcased;
            try {
                upcased = filter.substring(0, 1).toUpperCase() + filter.substring(1).toLowerCase();
            } catch (IndexOutOfBoundsException e) {
                upcased = filter.toUpperCase();
            }
            if(upcased.length() == 0) {
                filteredData.setPredicate(s -> true);
            }
            else {
                String finalUpcased = upcased;
                filteredData.setPredicate(s -> s.contains(finalUpcased));
            }
        });
        //Update list view with filtered data if there is any
        filteredData.addListener((ListChangeListener<String>) c -> {
            databaseNamesListView.getItems().clear();
            databaseNamesListView.getItems().addAll(filteredData);
            databaseNamesListView.getItems().sorted(String.CASE_INSENSITIVE_ORDER);
        });
    }

    //Run on add button being pressed
    @FXML
    void addButtonClicked() {
        String input = nameInput.getText().trim().replaceAll(" +", " ");
        if(input.length() > 51) {
            input = input.substring(0, 50);
        }
        //If input ends with many hashes
        while(input.endsWith("-")) {
            input = input.substring(0, input.length() - 1);

        }
        if(!input.isEmpty()) {
            //Check if added name is available
            String[] split = input.replaceAll("-", "- ").split("[\\s]");
            List<String> listNames = new ArrayList<>();
            for(String string : split) {
                if(!string.equals("")) {
                    listNames.add(string);
                }
            }
            split = listNames.toArray(new String[0]);

            for(int i=0; i<split.length; i++) {
                split[i] = split[i].substring(0, 1).toUpperCase() + split[i].substring(1).toLowerCase();
                if(!allNames.contains(split[i].replaceAll("-", ""))) {
                    split[i] = "*" + split[i] + "*";
                }
            }
            //Add string to list view
            StringBuilder builder = new StringBuilder();
            for(String s : split) {
                builder.append(s).append(" ");
            }
            String str = builder.toString().trim();
            //Don't add if there's already one there
            if(!practiceNamesListView.getItems().contains(str)) {
                practiceNamesListView.getItems().add(str);
            }

            //Enable practice button after a name has been added
            practiceButton.setDisable(false);
        }
        //Clear textfield
        nameInput.clear();
    }

    //Run on load files button being pressed
    @FXML
    void loadFilesPressed() {
        //Use file chooser to select text file
        FileChooser fc = new FileChooser();
        fc.setTitle("Select text file");
        Stage fcStage = new Stage();
        File selectedFile = fc.showOpenDialog(fcStage);
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        //Ensure select file ends with .txt
        if (selectedFile != null && selectedFile.getName().endsWith(".txt")) {
            //Read in the file containing the list of bad quality recordings
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    //Check if added name is available
                    String[] split = line.replaceAll("-", "- ").split("[\\s]");
                    for(int i=0; i<split.length; i++) {
                        split[i] = split[i].substring(0, 1).toUpperCase() + split[i].substring(1).toLowerCase();
                        if(!allNames.contains(split[i].replaceAll("-", ""))) {
                            split[i] = "*" + split[i] + "*";
                        }
                    }
                    //Add string to list view
                    StringBuilder builder = new StringBuilder();
                    for(String s : split) {
                        builder.append(s).append(" ");
                    }
                    String str = builder.toString();
                    //Add if not duplicate
                    if(!practiceNamesListView.getItems().contains(str)) {
                        practiceNamesListView.getItems().add(str);
                    }

                    //Disable the practice button when names are read
                    practiceButton.setDisable(false);
                }
            } catch (IOException ignored) {} //Ain't nobody got time for errors
        } else {
            Tooltip customTooltip = new CustomTooltip((Stage) practiceButton.getScene().getWindow(), loadFilesBtn, "Invalid file!", null);
        }
    }

    //Returns list of valid names from practice names list view
    List<String> getNamesList() {
        List<String> namesList = new ArrayList<>();
        for(String string : practiceNamesListView.getItems()) {
            if(!string.contains("*")) {
                namesList.add(string.replaceAll("- ", "-"));
            }
        }
        return namesList;
    }

    //Code run when save is pressed
    @FXML
    private void savePressed() throws IOException {
        File saveDir = new File(System.getProperty("user.dir") + "/saved_playlists");
        saveDir.mkdir();
        if(getNamesList().size() >= 1) {
            PopupWindow p = new PopupWindow("app/views/SavePlaylist.fxml", true, "");
        } else {
            Tooltip customTooltip = new CustomTooltip((Stage) practiceButton.getScene().getWindow(), saveBtn, "No valid names to save!", null);

        }
    }

    //Method called when item in database list view is double clicked
    @FXML
    private void doubleClicked() {
        nameInput.setText(nameInput.getText() + databaseNamesListView.getSelectionModel().getSelectedItem() + " ");
        filteredInput.clear();
    }

    //Call addButtonClicked if user presses enter from add name text field
    @FXML
    private void enterName(KeyEvent e) {
        if(e.getCode() == KeyCode.ENTER) {
            addButtonClicked();
        }
    }

    //Run on back button being pressed
    @FXML
    void backButtonClicked() throws IOException {
        filteredInput.clear(); //Clear input
        practiceNamesListView.getItems().removeAll(practiceNamesListView.getItems());
        Scene scene = SetUp.getInstance().startMenu;
        Stage window = (Stage) expandButton.getScene().getWindow();
        window.setScene(scene);

    }

    //Run on practise files button being pressed
    @FXML
    void practiceButtonClicked() throws IOException, UnsupportedAudioFileException {
        filteredInput.clear();

        //Send names to practice menu
        List<String> tempNames = new ArrayList<>();

        if (practiceNamesListView.getItems().size() > 0) {
            //Don't play names that don't exist
            for (String name : practiceNamesListView.getItems()) {
                if (!name.contains("*")) {
                    tempNames.add(name);
                }
            }

            if(tempNames.size() < 1) {
                practiceButton.setDisable(true);

                //Show tool tip when no valid names entered
                Tooltip customTooltip = new CustomTooltip((Stage) practiceButton.getScene().getWindow(), practiceButton, "Please enter a valid name!", null);

            } else {
                //Change to practice menu and set the path to have come from the enter names menu
                SetUp.getInstance().practiceMenuController.setUpList(tempNames);
                Scene scene = SetUp.getInstance().practiceMenu;
                Stage window = (Stage) expandButton.getScene().getWindow();
                window.setScene(scene);
            }
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

