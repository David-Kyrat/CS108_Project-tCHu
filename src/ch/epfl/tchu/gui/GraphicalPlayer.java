package ch.epfl.tchu.gui;

import ch.epfl.tchu.*;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.application.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import static ch.epfl.tchu.game.Card.LOCOMOTIVE;
import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.gui.ConstantsGUI.*;
import static ch.epfl.tchu.gui.DecksViewCreator.*;
import static ch.epfl.tchu.gui.InfoViewCreator.*;
import static ch.epfl.tchu.gui.MapViewCreator.*;
import static ch.epfl.tchu.gui.Nodes.*;
import static ch.epfl.tchu.gui.StringsFr.*;
import static javafx.application.Platform.*;
import static javafx.scene.paint.Color.*;
import static javafx.stage.Modality.*;
import static javafx.stage.StageStyle.*;

/**
 * Represents the graphic interface of a player
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class GraphicalPlayer {

    private final ObservableGameState gameState;

    private final ObjectProperty<DrawTicketsHandler> drawTicketsHandler = new SimpleObjectProperty<>(null);
    private final ObjectProperty<DrawCardHandler> drawCardHandler = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ClaimRouteHandler> claimRouteHandler = new SimpleObjectProperty<>(null);

    private final ObservableList<Text> gameInfos = FXCollections.observableArrayList();

    private final Stage primaryStage;
    private final Stage modalStage;
    private final Scene modalScene = new Scene(new Region());
    private final BorderPane root;

    /**
     * GraphicalPlayer unique constructor
     * @param id          the identity of the player to whom the interface corresponds
     * @param playerNames a Map containing the names of the players (as values) linked to their identities (as keys)
     */
    public GraphicalPlayer(PlayerId id, Map<PlayerId, String> playerNames) {
        this(id, playerNames, new Stage());
    }

    /**
     * GraphicalPlayer primary constructor
     * @param id           the identity of the player to whom the interface corresponds
     * @param playerNames  a Map containing the names of the players (as values) linked to their identities (as keys)
     * @param primaryStage Stage to use for displaying the actual (configured) game
     */
    public GraphicalPlayer(PlayerId id, Map<PlayerId, String> playerNames, Stage primaryStage) {
        assert isFxApplicationThread();
        this.primaryStage = primaryStage;

        gameState = new ObservableGameState(id);

        Pane mapView = createMapView(gameState, claimRouteHandler, this::chooseClaimCards);
        HBox handView = createHandView(gameState);
        VBox cardsView = createCardsView(gameState, drawTicketsHandler, drawCardHandler);
        VBox infoView = createInfoView(id, playerNames, gameState, gameInfos, primaryStage);

        root = new BorderPane(mapView, null, cardsView, handView, infoView);
        Scene scene = new Scene(root);


        this.primaryStage.setTitle("tCHu" + LONG_DASH_SEPARATOR + playerNames.get(id));
        this.modalStage = initModalStage(primaryStage);
        this.primaryStage.setFullScreenExitHint("");
        primaryStage.setOnCloseRequest(event -> {
            primaryStage.close();
            if (modalStage.isShowing()) modalStage.close();
            Platform.exit();
        });

        resize(scene, root, mapView, cardsView);
        Nodes.setShowCenter(primaryStage, scene, true);
    }

    public void resetForRematch() {
        gameInfos.clear();
        resetHandlerProperties();
        gameState.reset();
        root.setCenter(createMapView(gameState, claimRouteHandler, this::chooseClaimCards));
    }

    /**
     * Tests purposes
     * @param scene scene
     * @param root  BorderPane
     */
    private void resize(Scene scene, BorderPane root, Pane map, VBox cardView) {
        double maxHeight = Screen.getPrimary().getVisualBounds().getHeight();

        Resizer resizerWithStage = new Resizer(primaryStage, Screen.getPrimary().getVisualBounds());
        primaryStage.setMaxHeight(maxHeight);
        map.setManaged(false);
        double offsetX = root.getLeft().boundsInParentProperty().get().getWidth() * 3;
        map.setLayoutX(offsetX);
        map.setLayoutY(5);
        resizerWithStage.resizeMap(map);
        resizerWithStage.resize(cardView);

        root.setPadding(new Insets(0, 0, 5, 0));
        addDebug(scene);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F) primaryStage.setFullScreen(!primaryStage.isFullScreen());
        });
    }

    /**
     * Updates all the properties representing the game
     * (this method only call the one of the ObservableGameState linked to this graphical interface).
     * @param newGameState   the public part of the game
     * @param newPlayerState the complete state of the player to whom this is corresponding to
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, newPlayerState);
    }

    /**
     * Add the message passed in argument in the List of the information concerning the game progress
     * @param info String describing an information about the game progress
     */
    public void receiveInfo(String info) {
        assert isFxApplicationThread();
        styleInfo(new Text(info), info);
    }

    private void styleInfo(Text text, String info) {
        List<Card> foundCards = findCardsInInfo(info);
        if (foundCards.isEmpty()) gameInfos.add(text);
        else {
            List<Text> texts = new ArrayList<>();
            for (Card foundCard : foundCards) {
                Color textColor = foundCard == LOCOMOTIVE ? CYAN :
                                  foundCard == Card.BLACK ? rgb(77, 77, 77)
                                                          : Color.valueOf(foundCard.name());

                List<String> infoWords = Arrays.asList(info.split(Pattern.quote(" ")));
                int colorStart = getIndexOf(infoWords.stream()
                                                     .map(String::strip)
                                                     .collect(Collectors.toUnmodifiableList()), foundCard.french());
                try {
                    Text text1 = new Text(String.join(" ", infoWords.subList(0, colorStart - 1)) + " ");
                    Text text2 = new Text(String.join(" ", infoWords.subList(colorStart - 1, colorStart + 1)) + " ");
                    Text text3 = null;
                    if (colorStart + 1 <= infoWords.size())
                        text3 = new Text(String.join(" ", infoWords.subList(colorStart + 1, infoWords.size())));

                    text2.setStrokeWidth(0.3);
                    if (textColor == WHITE) {
                        text2.setStroke(BLACK);
                        text2.setFill(WHITE);
                    }
                    else text2.setStroke(textColor);

                    if (texts.size() == 0) texts.add(text1);
                    texts.add(text2);

                    if (texts.size() >= foundCards.size() + 1) {
                        texts.add(text3);
                    }
                }
                catch (Exception e) {
                    gameInfos.add(text);
                }
            }
            gameInfos.addAll(texts);
        }
    }

    private List<Card> findCardsInInfo(String info) {
        List<Card> foundCards = new ArrayList<>();
        for (Card card : Card.ALL) {
            if (info.contains(card.french())) foundCards.add(card);
        }
        return foundCards;
    }


    private int getIndexOf(List<String> infoWords, String cardFrench) {
        int colorStart;
        List<String> casesToCheck = List.of(cardFrench,
                                            cardFrench + ".", cardFrench + ",", cardFrench + "s", cardFrench + "s.", cardFrench + "s,");
        Iterator<String> it = casesToCheck.listIterator();
        do {
            String s = it.next();
            colorStart = infoWords.indexOf(s);
        } while (colorStart == -1 && it.hasNext());
        return colorStart;
    }

    /**
     * Makes sure that when the player decides to perform an action the corresponding handler will be called
     * @param ticketsHandler handler used if the player wants to draw tickets
     * @param cardHandler    handler used if the player wants to draw cards
     * @param claimHandler   handler used if the player wants to claim a road
     */
    public void startTurn(DrawTicketsHandler ticketsHandler, DrawCardHandler cardHandler, ClaimRouteHandler claimHandler) {
        assert isFxApplicationThread();

        if (gameState.canDrawTickets()) {
            DrawTicketsHandler ticketDrawer = () -> {
                ticketsHandler.onDrawTickets();
                resetHandlerProperties();
            };
            drawTicketsHandler.set(ticketDrawer);
        }

        if (gameState.canDrawCards()) {
            DrawCardHandler cardDrawer = slot -> {
                cardHandler.onDrawCard(slot);
                resetHandlerProperties();
            };
            drawCardHandler.set(cardDrawer);
        }

        ClaimRouteHandler routeClaimer = (wantedRoute, claimCards) -> {
            claimHandler.onClaimRoute(wantedRoute, claimCards);
            resetHandlerProperties();
        };
        claimRouteHandler.set(routeClaimer);
    }

    private void resetHandlerProperties() {
        drawTicketsHandler.set(null);
        drawCardHandler.set(null);
        claimRouteHandler.set(null);
    }


    /**
     * Opens a window allowing the player to make his choose from the given possibilities
     * and calls the given handler with this choice
     * @param tickets A SortedBag containing the tickets that the player can choose
     * @param handler A ticket selection handler
     * @throws IllegalArgumentException if tickets don't contains 5 or 3 elements
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler handler) {
        assert isFxApplicationThread();

        final int SIZE = tickets.size();

        Preconditions.checkArgument(SIZE == INITIAL_TICKETS_COUNT || SIZE == IN_GAME_TICKETS_COUNT);

        ListView<Ticket> selectionList = new ListView<>(FXCollections.observableArrayList(tickets.toList()));
        selectionList.getSelectionModel()
                     .setSelectionMode(SelectionMode.MULTIPLE);

        String text = String.format(CHOOSE_TICKETS,
                                    SIZE - DISCARDABLE_TICKETS_COUNT,
                                    plural(SIZE - DISCARDABLE_TICKETS_COUNT));

        Button button = new Button(CHOOSE);
        button.disableProperty().bind(
                Bindings.size(selectionList.getSelectionModel().getSelectedItems())
                        .greaterThanOrEqualTo(SIZE - DISCARDABLE_TICKETS_COUNT).not());

        button.setOnAction(action -> {
            modalStage.hide();
            handler.onChooseTickets(
                    SortedBag.of(selectionList.getSelectionModel().getSelectedItems()));
        });

        setModalWindow(TICKETS_CHOICE, text, selectionList, button);
    }

    /**
     * This method is intended to be called when the player needs to draw a second card !
     *
     * It allows the player to choose a car/locomotive card and once the player has clicked on one of these cards,
     * the given handler is called with the player's choice
     * @param handler A card drawer handler
     */
    public void drawCard(DrawCardHandler handler) {
        assert isFxApplicationThread();

        drawTicketsHandler.set(null);
        claimRouteHandler.set(null);

        DrawCardHandler cardDrawer = slot -> {
            handler.onDrawCard(slot);
            drawCardHandler.set(null);
        };
        drawCardHandler.set(cardDrawer);
    }

    /**
     * Opens a window allowing the player to make his choose from the given possibilities
     * and calls the given handler with this choice.
     *
     * N.B : This method is only intended to be passed as an argument to createMapView as a value of type CardChooser
     * @param possibleClaimCards A list of SortedBags of the initial cards the player can use to seize a route
     * @param handler            A card selection handler
     */
    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ChooseCardsHandler handler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> selectionList = createListViewOfCards(possibleClaimCards);

        Button button = new Button(CHOOSE);
        button.disableProperty().bind(
                Bindings.size(selectionList.getSelectionModel().getSelectedItems())
                        .greaterThanOrEqualTo(1).not());

        button.setOnAction(action -> {
            modalStage.hide();
            handler.onChooseCards(SortedBag.of(selectionList.getSelectionModel().getSelectedItem()));
        });

        setModalWindow(CARDS_CHOICE, CHOOSE_CARDS, selectionList, button);
    }

    /**
     * Opens a window allowing the player to make his choose from the given possibilities
     * and calls the given handler with this choice.
     * @param possibleAdditionalCards a list of SortedBags of the additional cards the player can use to take over a tunnel
     * @param handler                 A card selection handler
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ChooseCardsHandler handler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> selectionList = createListViewOfCards(possibleAdditionalCards);

        Button button = new Button(CHOOSE);
        button.setOnAction(action -> {
            modalStage.hide();
            if (selectionList.getSelectionModel().getSelectedItem() != null) {
                handler.onChooseCards(
                        SortedBag.of(selectionList.getSelectionModel().getSelectedItem()));
            }
            else handler.onChooseCards(SortedBag.of());
        });

        setModalWindow(CARDS_CHOICE, CHOOSE_ADDITIONAL_CARDS, selectionList, button);
    }

    private ListView<SortedBag<Card>> createListViewOfCards(List<SortedBag<Card>> possibleClaimCards) {
        ListView<SortedBag<Card>> selectionList = new ListView<>(FXCollections.observableArrayList(possibleClaimCards));
        selectionList.setCellFactory(option ->
                                             new TextFieldListCell<>(new CardBagStringConverter()));
        return selectionList;
    }

    private <E> void setModalWindow(String title, String text, ListView<E> selectionList, Button button) {
        modalStage.setTitle(title);

        VBox root = withChildren(new VBox(), withChildren(new TextFlow(), new Text(text)), selectionList, button);

        modalScene.setRoot(root);
        modalScene.getStylesheets().add(CHOOSER_CSS);
        modalScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F) {
                primaryStage.setFullScreen(primaryStage.isFullScreen());
            }
            if (!button.disableProperty().get() && keyEvent.getCode() == KeyCode.ENTER) {
                //TODO : fix condition
                button.getOnAction().handle(new ActionEvent());
            }
        });

        setShowCenter(modalStage, modalScene);
    }

    static Stage initModalStage(Stage primaryStage) {
        Stage modalStage = new Stage(UTILITY);
        modalStage.initOwner(primaryStage);
        modalStage.initModality(WINDOW_MODAL);
        modalStage.setOnCloseRequest(Event::consume);
        return modalStage;
    }

    public void askForRematch(AskHandler handler) {
        assert isFxApplicationThread();

        modalStage.setTitle("Rematch");

        Text text = new Text("Voulez-vous une revanche ?");

        Button yesButton = new Button("Oui");
        yesButton.setOnAction(action -> {
            modalStage.hide();
            handler.ask(true);
        });

        Button noButton = new Button("Non");
        noButton.setOnAction(action -> {
            modalStage.hide();
            handler.ask(false);
        });

        VBox root = withChildren(new VBox(), text, yesButton, noButton);

        modalScene.setRoot(root);

        setShowCenter(modalStage, modalScene);
    }
}
