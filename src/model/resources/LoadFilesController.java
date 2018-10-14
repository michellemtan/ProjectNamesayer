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
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadFilesController {

    @FXML private ListView<String> databaseNamesListView;
    @FXML private ListView<String> practiceNamesListView;
    @FXML private Button practiceButton;
    @FXML private Button expandButton;
    @FXML private SplitPane splitPane;
    @FXML private Label dbName;
    @FXML private TextField filteredInput;
    @FXML private TextField textField;
    private List<String> allNames;
    private List<String> tempNames = new ArrayList<>();

    //TODO: THE USER SHOULD BE ABLE TO DELETE NAMES FROM THE LIST?
    //TODO: CLEAR NAMES BUTTON
    //TODO: COMBINE THIS MENU WITH ADDING NAMES INDIVIDUALLY
    //TODO: SHOULD THE USER BE ABLE TO DOUBLE CLICK ON A DATABASE NAME TO ADD IT TO THE LIST

    public void initialize() {

        practiceNamesListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            cell.getStyleClass().add("list-cell-red");
            cell.textProperty().bind(cell.itemProperty());
            return cell;
        });

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
    }

    void setUpList(List<String> listNames, String name) {
        //Clear then fill & sort list
        databaseNamesListView.getItems().clear();
        databaseNamesListView.getItems().addAll(listNames);
        databaseNamesListView.getItems().sort(String.CASE_INSENSITIVE_ORDER);
        //Make practice names list not selectable by user, and allow all invalid names to be selected (highlighted)
        practiceNamesListView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
        practiceNamesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //Set label to reflect current database
        dbName.setText(name);

        //Create a list of all the names in the database to check loaded names against
        allNames = listNames;

        //Disable the practice button until a list has been loaded with items inside
        practiceButton.setDisable(true);

        //Apply layout
        splitPane.requestLayout();
        splitPane.applyCss();
        //Consume mouse dragged event to disable divider
        Node divider = splitPane.lookup(".split-pane-divider");
        divider.setOnMouseDragged(Event::consume);

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
        //TODO: If you search a name and clear the text field, the order of names is messed up
        //Update list view with filtered data if there is any
        filteredData.addListener((ListChangeListener<String>) c -> {
            databaseNamesListView.getItems().clear();
            databaseNamesListView.getItems().addAll(filteredData);
            databaseNamesListView.getItems().sorted(String.CASE_INSENSITIVE_ORDER);
        });
    }

    @FXML
    void addButtonClicked() {

        //Clear previous settings
       clearListView();

        FileChooser fc = new FileChooser();
        fc.setTitle("Select text file");
        Stage fcStage = new Stage();
        File selectedFile = (File) fc.showOpenDialog(fcStage);
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        if (selectedFile != null) {
            textField.setText(selectedFile.toString());

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
                        builder.append(s + " ");
                    }
                    String str = builder.toString();
                    //Add if not duplicate
                    if(!practiceNamesListView.getItems().contains(str)) {
                        practiceNamesListView.getItems().add(str);
                    }

                //Disable the practice button when names are read
                practiceButton.setDisable(false);
                }

            } catch (IOException ignored) {
            }

        }
        //Select invalid names
        for(int i=0; i<practiceNamesListView.getItems().size(); i++) {
            if(practiceNamesListView.getItems().get(i).contains("*")) {
                practiceNamesListView.getSelectionModel().select(i);
            }
        }
    }

    @FXML
    void backButtonClicked() throws IOException {
        clearListView();
        //Change back to database menu
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) expandButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void practiceButtonClicked() throws IOException, InterruptedException {

        ProcessBuilder removeBuilder = new ProcessBuilder("/bin/bash", "-c", "rm -r ./created_names");
        Process r = removeBuilder.start();
        r.waitFor();


        //Send names to practice menu
        List<String> practiceNames = practiceNamesListView.getItems();


        if (practiceNamesListView.getItems().size()>0) {

            //Don't play names that don't exist
            for (String name : practiceNames) {
                if (!name.contains("*")) {
                    tempNames.add(name);
                }
            }

            //TODO: ADD ALERT CONTAINING LIST OF NAMES THAT DON'T EXIST?

            //Concat names before loading menu
            new File("./created_names").mkdir();

            ConcatService service = new ConcatService();
            service.setOnSucceeded(event -> {
                //Change to practice menu and set the path to have come from the load files menu
                try {
                    SetUp.getInstance().practiceMenuController.setUpList(tempNames);
                    SetUp.getInstance().exitPracticeMenuController.setPreviousScene("loadFilesMenu");
                    Scene scene = SetUp.getInstance().practiceMenu;
                    Stage window = (Stage) practiceButton.getScene().getWindow();
                    window.setScene(scene);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

            if(tempNames.size() >= 1) {
                service.start();
            }
        }
    }


    private void addToTextFile(String name) throws IOException {
        File f = new File("ConcatNames.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        bw.append("file '"+name+"'\n");
        bw.flush();
        bw.close();
    }


    //Clear textfield if escape key pressed
    @FXML
    private void clearTextField(KeyEvent e) {
        if(e.getCode() == KeyCode.ESCAPE) {
            filteredInput.clear();
        }
    }

    @FXML
    private void clearListView(){
        //Clear previous settings
        practiceNamesListView.getItems().removeAll(practiceNamesListView.getItems());
        practiceNamesListView.refresh();
        practiceButton.setDisable(true);
    }

    private class ConcatService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws IOException, InterruptedException {
                    String pathToDB = SetUp.getInstance().databaseSelectMenuController.getPathToDB();


                    int audioNumber = 0;
                    for (String creation : tempNames) {
                        audioNumber++;
                        //Split name up and concat audio files
                        String[] split = creation.replaceAll("-", "").split("[-\\s]");

                        String concatString;

                        for (int i = 0; i < split.length; i++) {
                            String folderName = pathToDB + "/" + split[i] + "/";
                            File[] listFiles = new File(folderName).listFiles();

                            concatString = listFiles[0].getPath();
                            addToTextFile(concatString);
                        }

                        String newName = creation.replaceAll(" ","");

                        ProcessBuilder audioBuilder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -safe 0 -f concat -i ConcatNames.txt -c copy ./created_names/" + audioNumber + "_" + newName +".wav");
                        Process p = audioBuilder.start();
                        p.waitFor();
                        PrintWriter writer = new PrintWriter("ConcatNames.txt", "UTF-8");

                    }
                    return null;
                }
            };
        }
    }
}
