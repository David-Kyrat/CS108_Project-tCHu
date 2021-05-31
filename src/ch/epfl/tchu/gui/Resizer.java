package ch.epfl.tchu.gui;

import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

/**
 * @author Noah Munz (310779)
 */
class Resizer {
    private final static double PERFECT_SCREEN_SIZE = toMeter(16);
    private final double screenSize;
    private final double ratioX, ratioY;

    Resizer(double screenSizeInch) {
        this.screenSize = toMeter(screenSizeInch);
        this.ratioY = (screenSize/ PERFECT_SCREEN_SIZE);
        this.ratioX = ratioY + ((1 - ratioY) / 4);
    }

    public double ratioX() {
        return ratioX;
    }

    public double ratioY() {
        return ratioY;
    }

    /**
     * Set ScaleX and ScaleY to ratio which is determined by screenSize / PERFECT_SCREEN_SIZE
     * @param node Node to set Scale
     */
    public void resize(Node node) {
        node.setScaleX(ratioX);
        node.setScaleY(ratioY);
    }

    public void resizeBorderPaneRoot(BorderPane root) {
        try {
            Pane mapView = (Pane) root.getCenter();
            ImageView mapImg = (ImageView) mapView.getChildren().get(0);
            mapView.setScaleX(ratioX);
            mapView.setScaleY(ratioY);


        }
        catch (ClassCastException cce) {
            System.out.println("Wrong Argument, it is not the root of the main Scene of the Graphical representation of the game");
        }


    }

    private static double toMeter(double inch) {return (inch / 39.37);}
}
