package model.resources;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.sound.sampled.*;
import java.io.IOException;

public class MicrophoneController {

    @FXML
    private Button backButton;

    @FXML
    private ProgressBar progressBar;

    private String previousScene = "";

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {

        if (previousScene.equals("compareMenu")) {
            Scene scene = SetUp.getInstance().compareMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        } else if (previousScene.equals("startMenu")) {
            Scene scene = SetUp.getInstance().startMenu;
            Stage window = (Stage) backButton.getScene().getWindow();
            window.setScene(scene);
        }
    }

    public void setPreviousScene(String name) throws IOException {
        previousScene = name;
        startMic();
    }

    public void startMic(){

        new Thread () {
            @Override
                    public void run () {

                //Based on:
                TargetDataLine line = null;
                AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); //     format is an AudioFormat object
                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("The line is not supported.");
                }
                // Obtain and open the line.
                try {
                    line = (TargetDataLine) AudioSystem.getLine(info);
                    line.open(format);
                    line.start();
                } catch (
                        LineUnavailableException ex) {
                    System.out.println("The TargetDataLine is unavailable.");
                }

                while (true) {
                    byte[] bytes = new byte[line.getBufferSize() / 5];
                    line.read(bytes, 0, bytes.length);
                    double progress = (double) calculateRMSLevel(bytes)/65;
                    progressBar.setProgress((progress-0.8)*12.5);

                    Stage stage= (Stage) progressBar.getScene().getWindow();

                    if (backButton.isPressed() || !stage.isShowing()){
                        line.close();
                        return;
                    }


                }
            }
        }.start();

    }

    protected static int calculateRMSLevel(byte[] audioData)
    { // audioData might be buffered data read from a data line
        long lSum = 0;
        for(int i=0; i<audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;
        for(int j=0; j<audioData.length; j++)
            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }

}
