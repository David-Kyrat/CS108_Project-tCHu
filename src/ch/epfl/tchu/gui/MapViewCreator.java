package ch.epfl.tchu.gui;

import ch.epfl.tchu.*;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import java.util.*;
import java.util.stream.*;

import static ch.epfl.tchu.gui.ConstantsGUI.*;
import static ch.epfl.tchu.gui.Nodes.*;

/**
 * Class used to initialize & implements all the JavaFx graphical components regarding the map of the game board
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
final class MapViewCreator {

    /**
     * Private constructor to remove the default one and make MapViewCreator not instantiable
     */
    private MapViewCreator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a view of the Swiss Map on which the game unfolds
     * @param gameState         observable State of the game
     * @param claimRouteHandler property containing the action handler corresponding to all actions regarding the claim of a Route
     * @param cardChooser       instance of interface called when player has to choose which card to use to claim a Route
     */
    public static Pane createMapView(ObservableGameState gameState, ReadOnlyObjectProperty<ClaimRouteHandler> claimRouteHandler,
                                     CardChooser cardChooser) {

        Pane map = new Pane(new ImageView());
        map.getStylesheets().addAll(MAP_CSS, COLORS_CSS);

        List<Group> routeNodes = ChMap.routes()
                                      .stream()
                                      .map(route -> makeRouteNode(route, gameState, claimRouteHandler, cardChooser, map))
                                      .collect(Collectors.toUnmodifiableList());

        map.getStyleClass().add("map");
        return withChildren(map, routeNodes);
    }

    private static Group makeRouteNode(Route route, ObservableGameState gameState,
                                       ReadOnlyObjectProperty<ClaimRouteHandler> claimRouteHandler,
                                       CardChooser cardChooser, Pane mapView) {

        Group routeGroup = withClass(new Group(), ROUTE_CLASS, route.level().name(), colorClass(route.color()));
        routeGroup.setId(route.id());

        gameState.routeOwnerProperty(route)
                 .addListener((property, oldValue, newValue) -> {
                     if (newValue != null) withClass(routeGroup, newValue.name());
                     else if (oldValue != null) routeGroup.getStyleClass().remove(3);
                 });

        routeGroup.disableProperty().bind(
                claimRouteHandler.isNull().or(
                        gameState.isRouteClaimableProperty(route).not()));

        List<Group> caseNodes = IntStream.range(1, route.length() + 1)
                                         .mapToObj(i -> makeCaseNode(route, i, gameState.routeOwnerProperty(route)))
                                         .collect(Collectors.toUnmodifiableList());

        withChildren(routeGroup, caseNodes);

        routeGroup.setOnMouseClicked(mouseEvent -> {
            List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);

            if (possibleClaimCards.size() == 1) claimRouteHandler.getValue().onClaimRoute(route, possibleClaimCards.get(0));
            else {
                ChooseCardsHandler choiceHandler = chosenCard -> claimRouteHandler.getValue().onClaimRoute(route, chosenCard);
                cardChooser.chooseCards(possibleClaimCards, choiceHandler);
            }
        });
        //if we want to resize the map we have to make sure that the relative (to the imageView) position of the routesView stay the same
        Resizer.bindScaleProperty(mapView, routeGroup);
        return routeGroup;
    }

    private static Group makeCaseNode(Route route, int emplacement, ReadOnlyObjectProperty<PlayerId> routePossessor) {
        Group cell = new Group();
        cell.setId(route.id() + UNDERSCORE_SEPARATOR + emplacement);

        Rectangle track = withClass(new Rectangle(ROUTE_RECT_WIDTH, ROUTE_RECT_HEIGHT), TRACK_CLASS, FILLED_CLASS);

        Group wagon = new Group(withClass(new Rectangle(ROUTE_RECT_WIDTH, ROUTE_RECT_HEIGHT), FILLED_CLASS),
                                new Circle(FIRST_WAGON_CIRCLE_WIDTH, WAGON_CIRCLE_HEIGHT, WAGON_CIRCLE_RADIUS),
                                new Circle(SECOND_WAGON_CIRCLE_WIDTH, WAGON_CIRCLE_HEIGHT, WAGON_CIRCLE_RADIUS));

        withClass(wagon, CAR_CLASS);

        wagon.visibleProperty().bind(
                routePossessor.isNotNull());

        return withChildren(cell, track, wagon);
    }

    /**
     * Represent a cards chooser
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         * Called method when the player must choose the cards he wants to use to take a road
         * @param options List of SortedBag of its possibilities
         * @param handler ChooseCardsHandler used once the choice is made
         */
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}
