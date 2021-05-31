package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;

import java.util.*;
import java.util.stream.*;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.gui.ConstantsGUI.*;
import static ch.epfl.tchu.gui.Nodes.*;
import static ch.epfl.tchu.gui.StringsFr.*;

/**
 * Class used to initialize & implements all the JavaFx graphical components regarding the Decks, Hands etc...
 * (basically all the cards and tickets containers) and what they contains.
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
final class DecksViewCreator {

    /**
     * private constructor to remove the default one and make DeckViewCreator not instantiable
     */
    private DecksViewCreator() {
        throw new UnsupportedOperationException();
    }

    /*
    ===========================================================================
    Creation of the View of the Hand (i.e. Ticket Container and Card Container)
    ===========================================================================
    */

    /**
     * Initialize all the JavaFx graphical components regarding the Hand and what it contains.
     * @param gameState Observable state of the game currently unfolding
     * @return an HBox representing a view of the hand
     */
    public static HBox createHandView(ObservableGameState gameState) {
        HBox handView = new HBox();
        handView.getStylesheets().addAll(DECKS_CSS, COLORS_CSS);

        //ListView<Ticket> ticketsView = new ListView<>(gameState.ticketsProperty());
        List<String> tickets = gameState
                .ticketsProperty()
                .entrySet()
                .stream()
                .map(entry -> String.format("%s vaut : %dd  -  %s", entry.getKey(), entry.getValue(),
                                            entry.getValue() > 0 ? "✅" : "❌"))
                .peek(System.out::println)
                .collect(Collectors.toUnmodifiableList());

        ListView<String> ticketsView = new ListView<>(FXCollections.observableList(tickets));
        ticketsView.setId("tickets");

        HBox handBox = new HBox();
        handBox.setId("hand-pane");

        List<StackPane> cardsList = Card.ALL.stream()
                                            .map(card -> {
                                                StackPane stackPane = withClass(new StackPane(), colorClass(card.color()), CARD_CLASS);

                                                Text countText = withClass(new Text(), COUNT_CLASS);
                                                countText.textProperty().bind(Bindings.convert(gameState.colorCardCountProperty(card)));

                                                withChildren(stackPane, withClass(new Rectangle(EXT_RECT_WIDTH, EXT_RECT_HEIGHT), OUTSIDE_CLASS),
                                                             withClass(new Rectangle(INT_RECT_WIDTH, INT_RECT_HEIGHT), FILLED_CLASS, INSIDE_CLASS),
                                                             withClass(new Rectangle(INT_RECT_WIDTH, INT_RECT_HEIGHT), TRAIN_IMAGE_CLASS),
                                                             countText);

                                                stackPane.visibleProperty()
                                                         .bind(Bindings.greaterThan(gameState.colorCardCountProperty(card), 0));
                                                countText.visibleProperty()
                                                         .bind(Bindings.greaterThan(gameState.colorCardCountProperty(card), 1));

                                                return stackPane;
                                            })
                                            .collect(Collectors.toUnmodifiableList());

        withChildren(handBox, cardsList);

        return withChildren(handView, ticketsView, handBox);
    }

    /*
    ================================================================================
    Creation of the View of the Decks (i.e. Ticket Deck, FaceUp Cards and Card Deck)
    ================================================================================
    */

    /**
     * Initialize all the JavaFx graphical components regarding the Decks, FacUpCards and what they contains.
     * @param gameState         Observable state of the game currently unfolding
     * @param ticketDrawHandler property containing the handler of ticket draws
     * @param cardDrawHandler   property containing the handler of card draws
     * @return a VBox containing all the above mentioned element
     */
    public static VBox createCardsView(ObservableGameState gameState, ObjectProperty<DrawTicketsHandler> ticketDrawHandler,
                                       ObjectProperty<DrawCardHandler> cardDrawHandler) {

        VBox cardsView = new VBox();
        cardsView.getStylesheets().addAll(DECKS_CSS, COLORS_CSS);
        cardsView.setId("card-pane");

        Button ticketDeck = gaugeButton(TICKETS, gameState.ticketPercentageProperty());
        ticketDeck.disableProperty().bind(
                ticketDrawHandler.isNull());
        ticketDeck.onMouseClickedProperty()
                  .set(mouseEvent -> ticketDrawHandler.get().onDrawTickets());

        withChildren(cardsView, ticketDeck);
//TODO: check if remove null changes something
        List<StackPane> cardsList = FACE_UP_CARD_SLOTS
                .stream()
                .map(slot -> {
                    StackPane stackPane = withClass(new StackPane(), null, CARD_CLASS);
                    gameState.faceUpCardProperty(slot)
                             .addListener((property, oldValue, newValue) -> stackPane.getStyleClass()
                                                                                     .set(0, ConstantsGUI.colorClass(newValue.color())));

                    withChildren(stackPane, withClass(new Rectangle(EXT_RECT_WIDTH, EXT_RECT_HEIGHT), OUTSIDE_CLASS),
                                 withClass(new Rectangle(INT_RECT_WIDTH, INT_RECT_HEIGHT), FILLED_CLASS, INSIDE_CLASS),
                                 withClass(new Rectangle(INT_RECT_WIDTH, INT_RECT_HEIGHT), TRAIN_IMAGE_CLASS));

                    stackPane.disableProperty().bind(
                            cardDrawHandler.isNull());
                    stackPane.setOnMouseClicked(mouseEvent -> cardDrawHandler.get().onDrawCard(slot));

                    return stackPane;
                })
                .collect(Collectors.toUnmodifiableList());

        withChildren(cardsView, cardsList);

        Button cardDeck = gaugeButton(CARDS, gameState.cardsPercentageProperty());
        cardDeck.disableProperty().bind(cardDrawHandler.isNull());
        cardDeck.onMouseClickedProperty().set(mouseEvent -> cardDrawHandler.get().onDrawCard(DECK_SLOT));

        withChildren(cardsView, cardDeck);

        return cardsView;

    }

    private static Button gaugeButton(String label, ReadOnlyIntegerProperty property) {
        Rectangle foreground = withClass(new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT), FOREGROUND_CLASS);
        foreground.widthProperty().bind(
                property.multiply(GAUGE_WIDTH).divide(100));

        Group group = new Group(withClass(new Rectangle(GAUGE_WIDTH, GAUGE_HEIGHT), BACKGROUND_CLASS),
                                foreground);

        Button button = withClass(new Button(label), GAUGED_CLASS);
        button.setGraphic(group);
        return button;
    }
}
