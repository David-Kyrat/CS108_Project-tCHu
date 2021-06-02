package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;

import java.util.*;

import static ch.epfl.tchu.gui.ConstantsGUI.*;
import static ch.epfl.tchu.gui.Nodes.*;
import static ch.epfl.tchu.gui.StringsFr.*;

/**
 * Class used to initialize & implements all the JavaFx graphical components regarding the information window of the game
 * @author Mehdi Bouguerra Ezzina (314857)
 */
final class InfoViewCreator {

    /**
     * Private constructor to remove the default one and make InfoViewCreator not instantiable
     */
    private InfoViewCreator() {
        throw new UnsupportedOperationException();
    }


    /**
     * Create the view of the informations concerning the game and wrap it in a ScrollPane
     * to be able to keep all messages and not only the last 5 ones
     * @param id          the identity of the player to whom the interface corresponds
     * @param playerNames a Map containing the names of the players (as values) linked to their identities (as keys)
     * @param gameState   the observable state of the game
     * @param gameInfos   an ObservableList containing information about the progress of the game, in the form of Text instances
     * @return an VBox representing a view of the informations concerning the game
     */
    public static ScrollPane createInfoViewAndWrap(PlayerId id, Map<PlayerId, String> playerNames, ObservableGameState gameState,
                                                   ObservableList<Text> gameInfos, Stage primaryStage) {

        VBox infoView = createInfoView(id, playerNames, gameState, gameInfos, primaryStage);
        ScrollPane infoViewRoot = new ScrollPane(infoView);
        infoViewRoot.setHbarPolicy(ScrollBarPolicy.NEVER);
        infoViewRoot.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        return infoViewRoot;
    }

    /**
     * Create the view of the informations concerning the game
     * @param id          the identity of the player to whom the interface corresponds
     * @param playerNames a Map containing the names of the players (as values) linked to their identities (as keys)
     * @param gameState   the observable state of the game
     * @param gameInfos   an ObservableList containing information about the progress of the game, in the form of Text instances
     * @return an VBox representing a view of the informations concerning the game
     */
    public static VBox createInfoView(PlayerId id, Map<PlayerId, String> playerNames, ObservableGameState gameState,
                                      ObservableList<Text> gameInfos, Stage primaryStage) {

        VBox infoView = new VBox();
        infoView.getStylesheets().addAll(INFO_CSS, COLORS_CSS);
        VBox playerStats = null;
        for (PlayerId playerId : PlayerId.ALL) {
            Text stats = new Text();
            stats.textProperty().bind(
                    Bindings.format(PLAYER_STATS,
                                    playerNames.get(playerId),
                                    gameState.ticketCountsProperty(playerId),
                                    gameState.cardCountsProperty(playerId),
                                    gameState.carCountsProperty(playerId),
                                    gameState.claimPointsProperty(playerId)));

            TextFlow statsInfo = new TextFlow(withClass(new Circle(COLORED_CIRCLE_RADIUS), FILLED_CLASS),
                                              stats);
            withClass(statsInfo, playerId.name());

            playerStats = new VBox(statsInfo);
            playerStats.setId("player-stats");


            withChildren(infoView, playerStats);
        }

        TextFlow gameInfo = new TextFlow();
        Bindings.bindContent(gameInfo.getChildren(), gameInfos);
        ScrollPane gameInfoWrapper = new ScrollPane(gameInfo);
        gameInfoWrapper.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        gameInfoWrapper.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        gameInfoWrapper.vvalueProperty().bind(gameInfo.heightProperty());

        gameInfoWrapper.setId("game-info");

        gameInfo.maxWidthProperty().bind(primaryStage.widthProperty().multiply(0.14));
        return withChildren(infoView, new Separator(), gameInfoWrapper);
    }
}
