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
import javafx.scene.media.Media;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LoadFilesController {

    @FXML private Text backButton;
    @FXML private ListView<String> databaseNamesListView;
    @FXML private ListView<String> practiceNamesListView;
    @FXML private Button practiceButton;
    @FXML private Button expandButton;
    @FXML private SplitPane splitPane;
    @FXML private Label dbName;
    @FXML private TextField filteredInput;
    @FXML private TextField textField;
    private List<String> allNames;


    //TODO: THE USER SHOULD BE ABLE TO DELETE NAMES FROM THE LIST?

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
    }

    void setUpList(List<String> listNames, String name) {
        //Clear then fill & sort list
        databaseNamesListView.getItems().clear();
        databaseNamesListView.getItems().addAll(listNames);
        databaseNamesListView.getItems().sort(String.CASE_INSENSITIVE_ORDER);
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

            List<String> namesList = new ArrayList<>();

            //Read in the file containing the list of bad quality recordings
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                StringBuilder fieldContent = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                        //Check if added name is available
                        String[] split = line.split("[-\\s]");
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
                        String str = builder.toString();
                        //Trim off white space otherwise results in Catherine .wav rather than Catherine.wav
                        str = str.trim();
                        namesList.add(str);

                //Disable the practice button when names are read
                practiceButton.setDisable(false);
                }

                //Add text from file to list view
                practiceNamesListView.getItems().addAll(namesList);

            } catch (IOException ignored) {
            }

        }
    }

    @FXML
    void backButtonClicked() throws IOException {

        clearListView();

        //Change back to database menu
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void practiceButtonClicked(MouseEvent e) throws IOException, InterruptedException {

        ProcessBuilder removeBuilder = new ProcessBuilder("/bin/bash", "-c", "rm -r ./created_names");
        Process r = removeBuilder.start();
        r.waitFor();

        int audioNumber = 0;

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

            //TODO: ADD ALERT CONTAINING LIST OF NAMES THAT DON'T EXIST?

            //Concat names before loading menu
            new File("./created_names").mkdir();
            System.out.println("Made directory");

            String pathToDB = SetUp.getInstance().databaseSelectMenuController.getPathToDB();

            for (String creation : tempNames) {

                audioNumber++;
                //Set up the file to be played
                String selectedName = creation;

                //Split name up and concat audio files
                String[] split = selectedName.split("[-\\s]");

                for (int i = 0; i < split.length; i++) {
                    String folderName = pathToDB + "/" + split[i] + "/";
                    File[] listFiles = new File(folderName).listFiles();
                    String concatString = listFiles[0].toURI().toString();
                    concatString = concatString.replaceAll("file:", "");
                    addToTextFile(concatString);
                }

                String newName = selectedName.replaceAll(" ","");

                ProcessBuilder audioBuilder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -safe 0 -f concat -i ConcatNames.txt -c copy ./created_names/" + audioNumber + "_" + newName +".wav");
                Process p = audioBuilder.start();
                p.waitFor();
                PrintWriter writer = new PrintWriter("ConcatNames.txt", "UTF-8");

            }

            //Change to practice menu and set the path to have come from the load files menu
            SetUp.getInstance().practiceMenuController.setUpList(tempNames);
            SetUp.getInstance().exitPracticeMenuController.setPreviousScene("loadFilesMenu");
            Scene scene = SetUp.getInstance().practiceMenu;
            Stage window = (Stage) practiceButton.getScene().getWindow();
            window.setScene(scene);
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
}
