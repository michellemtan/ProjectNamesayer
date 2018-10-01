package model.resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class SetUp {

    //Single instance of NameSayer can be running at a time
    private static SetUp setUp;

    //Initialise controllers to allow for data to be passed between scenes
    DatabaseMenuController dbMenuController;
    DeleteMenuController deleteMenuController;
    PracticeMenuController practiceMenuController;
    RecordMenuController recordMenuController;
    AudioRatingsController audioRatingsController;
    NameDetailsController nameDetailsController;
    CompareMenuController compareMenuController;
    CreateMenuController createMenuController;
    RecordCreationMenuController recordCreationMenuController;


    //Scenes to load the fxml files to
    Scene compareMenu;
    Scene createMenu;
    Scene databaseMenu;
    Scene deleteMenu;
    Scene databaseSelectMenu;
    Scene practiceMenu;
    Scene recordMenu;
    public Scene startMenu;
    Scene instructionsMenu;
    Scene badRecordingsMenu;
    Scene nameDetailsMenu;
    Scene exitPracticeMenu;
    Scene recordCreationMenu;

    private SetUp() throws IOException {

        //Load menus to be used throughout the program
        //compareMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/CompareMenu.fxml")));
        compareMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/CompareMenu.fxml")));
        //createMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/CreateMenu.fxml")));
        createMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/CreateMenu.fxml")));
        //databaseMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/DatabaseMenu.fxml")));
        databaseMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/DatabaseMenu.fxml")));
        //deleteMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/DeleteMenu.fxml")));
        deleteMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/DeleteMenu.fxml")));
        //databaseSelectMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/DatabaseSelectMenu.fxml")));
        databaseSelectMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/DatabaseSelectMenu.fxml")));
        //practiceMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/PracticeMenu.fxml")));
        practiceMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/PracticeMenu.fxml")));
        //recordMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/RecordMenu.fxml")));
        recordMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/RecordMenu.fxml")));
        //startMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/StartMenu.fxml")));
        startMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/StartMenu.fxml")));
        //badRecordingsMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/AudioRatings.fxml")));
        badRecordingsMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/AudioRatings.fxml")));
        //instructionsMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/InstructionsMenu.fxml")));
        instructionsMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/InstructionsMenu.fxml")));
        //nameDetailsMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/NameDetailsMenu.fxml")));
        nameDetailsMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/NameDetailsMenu.fxml")));
        //exitPracticeMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/ExitPracticeMenu.fxml")));
        exitPracticeMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/ExitPracticeMenu.fxml")));
        //recordCreationMenu = new Scene(FXMLLoader.load(getClass().getResource("../views/RecordCreationMenu.fxml")));
        recordCreationMenu = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("model/views/RecordCreationMenu.fxml")));

        //Load load menu
        startMenuLoader();
        databaseMenuLoader();
        deleteMenuLoader();
        mainMenuLoader();
        practiceMenuLoader();
        recordMenuLoader();
        badRecordingsMenuLoader();
        instructionsMenuLoader();
        nameDetailsMenuLoader();
        exitPracticeMenuLoader();
        compareMenuLoader();
        createMenuLoader();
        recordCreationMenuLoader();

        //Add Theme.css to all scenes
        compareMenu.getStylesheets().add("/model/resources/Theme.css");
        createMenu.getStylesheets().add("/model/resources/Theme.css");
        databaseMenu.getStylesheets().add("/model/resources/Theme.css");
        deleteMenu.getStylesheets().add("/model/resources/Theme.css");
        databaseSelectMenu.getStylesheets().add("/model/resources/Theme.css");
        practiceMenu.getStylesheets().add("/model/resources/Theme.css");
        recordMenu.getStylesheets().add("/model/resources/Theme.css");
        startMenu.getStylesheets().add("/model/resources/Theme.css");
        badRecordingsMenu.getStylesheets().add("/model/resources/Theme.css");
        instructionsMenu.getStylesheets().add("/model/resources/Theme.css");
        nameDetailsMenu.getStylesheets().add("/model/resources/Theme.css");
        exitPracticeMenu.getStylesheets().add("/model/resources/Theme.css");
        recordCreationMenu.getStylesheets().add("/model/resources/Theme.css");

    }

    //Constructor implementing Singleton pattern to create one instance of SetUp class where different scenes are created
    public static SetUp getInstance() throws IOException {
        if (setUp == null){
            setUp = new SetUp();
        }
        return setUp;
    }

    //Methods to load FXML files to scenes

    private void startMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/StartMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/StartMenu.fxml"));
        startMenu = new Scene(loader.load());
        StartMenuController startMenuController = loader.getController();
    }

    private void recordMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/RecordMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/RecordMenu.fxml"));
        recordMenu = new Scene(loader.load());
        recordMenuController = loader.getController();
    }

    private void practiceMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/PracticeMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/PracticeMenu.fxml"));
        practiceMenu = new Scene(loader.load());
        practiceMenuController = loader.getController();
    }

    private void mainMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/DatabaseSelectMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/DatabaseSelectMenu.fxml"));
        databaseSelectMenu = new Scene(loader.load());
        DatabaseSelectMenuController databaseSelectMenuController = loader.getController();
    }

    private void deleteMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/DeleteMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/DeleteMenu.fxml"));
        deleteMenu = new Scene(loader.load());
        deleteMenuController = loader.getController();
    }

    private void databaseMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/DatabaseMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/DatabaseMenu.fxml"));
        databaseMenu = new Scene(loader.load());
        dbMenuController = loader.getController();
    }

    private void badRecordingsMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/AudioRatings.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/AudioRatings.fxml"));
        badRecordingsMenu = new Scene(loader.load());
        audioRatingsController = loader.getController();
    }

    private void instructionsMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/InstructionsMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/InstructionsMenu.fxml"));
        instructionsMenu = new Scene(loader.load());
        InstructionsMenuController instructionsMenuController = loader.getController();
    }

    private void nameDetailsMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/NameDetailsMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/NameDetailsMenu.fxml"));
        nameDetailsMenu = new Scene(loader.load());
        nameDetailsController = loader.getController();
    }

    private void exitPracticeMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/ExitPracticeMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/ExitPracticeMenu.fxml"));
        exitPracticeMenu = new Scene(loader.load());
        ExitPracticeMenuController exitPracticeMenuController = loader.getController();
    }

    private void createMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/CreateMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/CreateMenu.fxml"));
        createMenu = new Scene(loader.load());
        createMenuController = loader.getController();
    }

    private void recordCreationMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/RecordCreationMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/RecordCreationMenu.fxml"));
        recordCreationMenu = new Scene(loader.load());
        recordCreationMenuController = loader.getController();
    }

    private void compareMenuLoader() throws IOException {
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/CompareMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("model/views/CompareMenu.fxml"));
        compareMenu = new Scene(loader.load());
        compareMenuController = loader.getController();
    }
}
