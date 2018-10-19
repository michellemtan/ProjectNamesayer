package model.resources;

import javafx.scene.media.Media;

import java.io.File;
import java.io.IOException;

public class Creation {

    private String name;
    private Media media;

    public Creation(String n, Media m) {
        name = n;
        media = m;
    }

    public String getFullName() {
        return this.name;
    }

    public String getFirstName(){
        String firstName = name.split("[- ]")[0];
        return firstName;
    }

    public Media getMedia(){
        return this.media;
    }

    public Media getFirstNameMedia() throws IOException {
        String pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();
        String folderName = pathToDB + "/" + this.getFirstName() + "/";
        File[] listFiles = new File(folderName).listFiles();
        Media firstNameMedia = new Media(listFiles[0].toURI().toString());
        return firstNameMedia;
    }

    public void setMedia(Media m){
        media = m;
    }

}