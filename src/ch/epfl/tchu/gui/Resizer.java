package ch.epfl.tchu.gui;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;

/**
 * @author Noah Munz (310779)
 */
class Resizer {
    private final static double PERFECT_SCREEN_SIZE = toMeter(16);
    private final static double PERFECT_WIDTH = 1500, PERFECT_HEIGHT = 920;

    private final double maxWidth, maxHeight;
    private final DoubleProperty mapRatioX, mapRatioY;
    private Stage primaryStage;
    private double screenSize;
    //  private final double mapScaleFactor;

    Resizer(double screenSizeInch, Rectangle2D visualBounds) {
        this.screenSize = toMeter(screenSizeInch);
        this.maxWidth = visualBounds.getWidth();
        this.maxHeight = visualBounds.getHeight();
        this.mapRatioY = new SimpleDoubleProperty(maxHeight / PERFECT_HEIGHT); //new SimpleDoubleProperty(screenSize / PERFECT_SCREEN_SIZE);
        this.mapRatioX = new SimpleDoubleProperty(maxWidth / PERFECT_WIDTH);  //ratioY.get() + ((1 - ratioY.get()) / 4));
    }

    Resizer(Stage primaryStage, Rectangle2D visualBounds) {
        this.maxWidth = visualBounds.getWidth();
        this.maxHeight = visualBounds.getHeight();
        this.mapRatioX = new SimpleDoubleProperty();
        this.mapRatioY = new SimpleDoubleProperty();
        this.primaryStage = primaryStage;
        mapRatioX.bind(primaryStage.widthProperty().divide(PERFECT_WIDTH));
        mapRatioY.bind(primaryStage.heightProperty().divide(PERFECT_HEIGHT));
    }

    public DoubleProperty ratioX() {
        return mapRatioX;
    }

    public DoubleProperty ratioY() {
        return mapRatioY;
    }

    /**
     * Set ScaleX and ScaleY of mapView to ratio which is determined by maxWidth / PERFECT_WIDTH, same for height
     * @param pane Pane to set Scale
     */
    public void resizeMap(Pane pane) {
        bindScaleProperty(pane, mapRatioX, mapRatioY);
    }

    void resize(Node node) {
        bindScaleProperty(node, primaryStage.widthProperty().divide(maxWidth), primaryStage.heightProperty().divide(maxHeight));
    }

    static void bindScaleProperty(Node observedNode, Node observingNode) {
        bindScaleProperty(observingNode, observedNode.scaleXProperty(), observedNode.scaleYProperty());
    }


    static void bindScaleProperty(Node node, DoubleBinding scaleX, DoubleBinding scaleY) {
        node.scaleXProperty().bind(scaleX);
        node.scaleYProperty().bind(scaleY);
    }


    static void bindScaleProperty(Node node, DoubleProperty scaleX, DoubleProperty scaleY) {
        node.scaleXProperty().bind(scaleX);
        node.scaleYProperty().bind(scaleY);
    }

    private static double toMeter(double inches) {return (inches / 39.37);}
}
