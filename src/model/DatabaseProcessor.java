package model;

import java.io.*;

public class DatabaseProcessor {

    private String pathToDB;

    public DatabaseProcessor(String path) {
        pathToDB = path;
    }

    public void processDB() {
        File dir = new File(pathToDB);
        File[] directoryListing = dir.listFiles();
        //Create folder for untrimmed audio files
        new File(pathToDB + "/uncut_files").mkdir();

        if(directoryListing != null) {
            for(File file : directoryListing) {
                if(!file.isDirectory()) {
                    //Get name from file name
                    String name = file.getName().substring(file.getName().lastIndexOf("_")+1, file.getName().length()-4);
                    //Create folder of name
                    new File(pathToDB + "/" + name).mkdir();
                    //Trim audio into newly created folder
                    String command = "ffmpeg -y -i " + file.getPath() + " -af silenceremove=1:0:-35dB " + pathToDB + "/" + name + "/" + file.getName();
                    System.out.println(command);
                    trimAudio(command);
                    file.renameTo(new File(pathToDB + "/uncut_files/" + file.getName()));
                }
            }
        }
    }

    /**
     * Code to trim audio's silence via bash ffmpeg command
     * @param command bash command to be run
     */
    private void trimAudio(String command) {
        try {
            ProcessBuilder builderRecord = new ProcessBuilder("/bin/bash", "-c", command);
            Process process = builderRecord.start();

            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();
            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
            String line;
            while ((line = stdoutBuffered.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ignored) {
        }
    }
    //ffmpeg -y -i path/to/in.wav -af silenceremove=1:0:-35dB path/to/out.wav for removing silence
}
