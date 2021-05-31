package ch.epfl.tchu.gui;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

import java.util.*;
import java.util.function.*;

import static ch.epfl.tchu.gui.GameMenu.*;
import static ch.epfl.tchu.gui.Nodes.*;
import static javafx.geometry.Pos.*;

public class Menu extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private final static String BACKGROUND_PATH = imgPath("menu2.jpg");
    private final static List<String> FONTS_STYLESHEETS =
            List.of("GameMenu.css", "debug.css",
                    "https://fonts.googleapis.com/css2?family=Fondamento&family=Niconne&display=swap");

    private final static GameMenu gameMenu = new GameMenu();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(TITLE);
        primaryStage.getIcons().add(new Image(ICON_PATH));
        primaryStage.setFullScreenExitHint("Press ' f ' to exit/enter fullScreen");

        Rectangle background = new Rectangle(SCENE_W, SCENE_H, new ImagePattern(new Image(BACKGROUND_PATH)));
        StackPane fakeBtn = setUpBtnImage(primaryStage, "Play", stage -> setShowCenter(stage, gameMenu.scene(stage)));
        StackPane quitBtn = setUpBtnImage(primaryStage, "Quit", stage -> Platform.exit());
        setScale(1.5, 1.5, fakeBtn, quitBtn);

        VBox btnsBox = withChildren(setUpNewVBox(100, CENTER, true), fakeBtn, quitBtn);
        btnsBox.setPadding(new Insets(SCENE_H * 0.2, 0, 0, 0));
        StackPane root = new StackPane(background, btnsBox);

        Scene scene = new Scene(root, SCENE_W, SCENE_H);
        scene.getStylesheets().addAll(FONTS_STYLESHEETS);

        scene.getStylesheets().add("debug.css");
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F) primaryStage.setFullScreen(!primaryStage.isFullScreen());
        });
        setShowCenter(primaryStage, scene, true);
        primaryStage.setFullScreen(true);
    }

    private StackPane setUpBtnImage(Stage stage, String buttonText, Consumer<Stage> btnFunction) {
     //   final double fakeButtonWidth = 210;
      //  final Text buttonText = new Text(rawButtonText + "   ");

        StackPane stackPane = styleFantasyBtnImage(buttonText);

        //Reset image to normal
        stackPane.setOnMouseReleased(e -> {
            setUpBtnClickEffect(stackPane);
            btnFunction.accept(stage);
            e.consume();
        });
        return stackPane;
    }

    /**
     * Set Scale for X axis and Y axis
     * @param nodes  nodes to apply setScale on
     * @param scaleX argument to give to <code>node.setScaleX()</code>
     * @param scaleY argument to give to <code>node.setScaleY()</code>
     */
    public static void setScale(double scaleX, double scaleY, Node... nodes) {
        for (Node node : nodes) {
            node.setScaleX(scaleX);
            node.setScaleY(scaleY);
        }
    }

}
