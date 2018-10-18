package model.resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class SetUp {

    //Single instance of NameSayer can be running at a time
    private static SetUp setUp;

    //Initialise controllers to allow for data to be passed between scenes
    PracticeMenuController practiceMenuController;
    AudioRatingsController audioRatingsController;
    CompareMenuController compareMenuController;
    public StartMenuController startMenuController;
    DatabaseSelectMenuController databaseSelectMenuController;
    ExitPracticeMenuController exitPracticeMenuController;
    InstructionsMenuController instructionsMenuController;
    EnterNamesController enterNamesController;
    MicrophoneController microphoneController;
    NamesListController namesListController;
    public MissingDBController missingDBController;

    //Scenes to load the fxml files to
    public Scene startMenu;
    Scene compareMenu;
    Scene databaseSelectMenu;
    Scene practiceMenu;
    Scene instructionsMenu;
    Scene audioRatingsMenu;
    Scene exitPracticeMenu;
    Scene enterNamesMenu;
    Scene microphoneMenu;
    Scene namesListMenu;
    Scene missingDB;

    private SetUp() throws IOException {

        //Load scenes to be used in the program
        startMenu = startMenuLoader();
        compareMenu = compareMenuLoader();
        databaseSelectMenu = databaseSelectMenuLoader();
        practiceMenu = practiceMenuLoader();
        audioRatingsMenu = audioRatingsMenuLoader();
        instructionsMenu = instructionsMenuLoader();
        exitPracticeMenu = exitPracticeMenuLoader();
        enterNamesMenu = enterNamesLoader();
        microphoneMenu = microphoneMenuLoader();
        namesListMenu = namesListMenuLoader();
        missingDB = missingDBLoader();

        //Add Theme.css to all scenes
        compareMenu.getStylesheets().add("/model/resources/Theme.css");
        databaseSelectMenu.getStylesheets().add("/model/resources/Theme.css");
        practiceMenu.getStylesheets().add("/model/resources/Theme.css");
        startMenu.getStylesheets().add("/model/resources/Theme.css");
        audioRatingsMenu.getStylesheets().add("/model/resources/Theme.css");
        instructionsMenu.getStylesheets().add("/model/resources/Theme.css");
        exitPracticeMenu.getStylesheets().add("/model/resources/Theme.css");
        enterNamesMenu.getStylesheets().add("/model/resources/Theme.css");
        microphoneMenu.getStylesheets().add("/model/resources/Theme.css");
        namesListMenu.getStylesheets().add("/model/resources/Theme.css");
        missingDB.getStylesheets().add("/model/resources/Theme.css");
    }

    //Constructor implementing Singleton pattern to create one instance of SetUp class where different scenes are created
    public static SetUp getInstance() throws IOException {
        if (setUp == null){
            setUp = new SetUp();
        }
        return setUp;
    }

    //Methods to load FXML files to scenes

    private Scene startMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/StartMenu.fxml"));
        startMenu = new Scene(loader.load());
        startMenuController = loader.getController();
        return startMenu;
    }

    private Scene missingDBLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/MissingDB.fxml"));
        missingDB = new Scene(loader.load());
        missingDBController = loader.getController();
        return missingDB;
    }

    private Scene practiceMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/PracticeMenu.fxml"));
        practiceMenu = new Scene(loader.load());
        practiceMenuController = loader.getController();
        return practiceMenu;
    }

    private Scene databaseSelectMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/DatabaseSelectMenu.fxml"));
        databaseSelectMenu = new Scene(loader.load());
        databaseSelectMenuController = loader.getController();
        return databaseSelectMenu;
    }

    private Scene audioRatingsMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/AudioRatings.fxml"));
        audioRatingsMenu = new Scene(loader.load());
        audioRatingsController = loader.getController();
        return audioRatingsMenu;
    }

    private Scene instructionsMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/InstructionsMenu.fxml"));
        instructionsMenu = new Scene(loader.load());
        instructionsMenuController = loader.getController();
        return instructionsMenu;
    }

    private Scene exitPracticeMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/ExitPracticeMenu.fxml"));
        exitPracticeMenu = new Scene(loader.load());
        exitPracticeMenuController = loader.getController();
        return exitPracticeMenu;
    }

    private Scene compareMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/CompareMenu.fxml"));
        compareMenu = new Scene(loader.load());
        compareMenuController = loader.getController();
        return compareMenu;
    }

    private Scene enterNamesLoader() throws  IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/EnterNamesMenu.fxml"));
        enterNamesMenu = new Scene(loader.load());
        enterNamesController = loader.getController();
        return enterNamesMenu;
    }

    private Scene microphoneMenuLoader() throws  IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/MicrophoneMenu.fxml"));
        microphoneMenu = new Scene(loader.load());
        microphoneController = loader.getController();
        return microphoneMenu;
    }

    private Scene namesListMenuLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/NamesListMenu.fxml"));
        namesListMenu = new Scene(loader.load());
        namesListController = loader.getController();
        return namesListMenu;
    }
}
