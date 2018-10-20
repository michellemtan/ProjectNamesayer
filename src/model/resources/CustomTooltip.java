package model.resources;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CustomTooltip extends Tooltip {

    Tooltip customTooltip;

    //Source: https://stackoverflow.com/questions/17405688/javafx-activate-a-tooltip-with-a-button
    public CustomTooltip(Stage owner, Button control, String tooltipText, ImageView tooltipGraphic) {
        Point2D p = control.localToScene(0.0, 0.0);

        customTooltip = new Tooltip();
        customTooltip.setText(tooltipText);

        control.setTooltip(customTooltip);
        customTooltip.setAutoHide(true);

        customTooltip.show(owner, p.getX()
                + control.getScene().getX() + control.getScene().getWindow().getX(), p.getY()
                + control.getScene().getY() + control.getScene().getWindow().getY());

        customTooltip.setAutoFix(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> customTooltip.hide());
        pause.play();
    }
}
