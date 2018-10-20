package model.resources;

import javafx.scene.media.Media;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Creation {

    private HashMap<String, Media> fullNameHashMap;
    private String name;
    private List<String> nameList;

    public Creation(String n) throws IOException {
        name = n;

        String pathToDB = SetUp.getInstance().settingsMenuController.getPathToDB();
        String[] split = name.replaceAll("-", "- ").split("[\\s]");
        nameList = new ArrayList<>();
        fullNameHashMap = new HashMap<>();

        for(int i=0; i<split.length; i++) {
            nameList.add(split[i]);
            //Set up the hash map to contain the default names
            String folderName = pathToDB + "/" + split[i] + "/";
            File[] listFiles = new File(folderName).listFiles();
            Media m = new Media(listFiles[0].toURI().toString());
            fullNameHashMap.put(split[i], m);
        }
    }

    public String getFullName() {
        return this.name;
    }

    public String getFirstName(){
        String firstName = name.split("[- ]")[0];
        return firstName;
    }

    //This method returns the full name of media files
    public List<Media> getFullNameMedia(){
        List<Media> mediaList = new ArrayList<>();
        for (String name: nameList){
            mediaList.add(fullNameHashMap.get(name));
        }
        return mediaList;
    }

    public Media getFirstNameMedia() throws IOException {
        String firstName = name.split("[- ]")[0];
        return fullNameHashMap.get(firstName);
    }

    //This method sets the highest rated media file
    public void setHighestRateMedia(String name, Media m){
        fullNameHashMap.put(name, m);
    }


    //This method sets the default media file
    public void setDefaultMedia(String name, Media m){
        fullNameHashMap.put(name, m);
    }

}