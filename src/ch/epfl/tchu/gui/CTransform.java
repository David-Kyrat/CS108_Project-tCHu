package ch.epfl.tchu.gui;

import javafx.animation.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.util.*;

import java.util.function.*;

import static ch.epfl.tchu.gui.CTransform.MouseState.*;
import static ch.epfl.tchu.gui.GraphicalCard.*;

public final class CTransform {


    private final int duration;
    private final int translateAmount;
    private final Direction direction;

    private final TranslateTransition tt;
    private final BiConsumer<GraphicalCard, MouseState> translation;
    private final Function<GraphicalCard, Double> coordToParentComputer;

    enum MouseState {ENTER, EXIT}

    /** Direction of translation when card are hovered */

    enum Direction {X_AXIS, Y_AXIS}

    /**
     * @param direction       Direction of translation when card are hovered
     * @param duration        int, (duration for hoverAnimation)
     * @param translateAmount int, (translation over vertical or horizontal axis depending on given direction when Card is hovered)
     */
    public CTransform(Direction direction, int duration, int translateAmount) {
        this.direction = direction;
        this.duration = duration;
        this.translateAmount = translateAmount;
        this.tt = new TranslateTransition(Duration.millis(duration));

        switch (direction) {
            case X_AXIS:
                this.translation = (graphicalCard, mouseState) ->
                        setTTNode(graphicalCard).setByX(mouseState == ENTER ? translateAmount
                                                                            : -graphicalCard.getTranslateX());

                this.coordToParentComputer = graphicalCard -> (graphicalCard.boundsInParentProperty().get().getMaxX())
                        - (graphicalCard.boundsInLocalProperty().get().getMaxX());
                break;
            case Y_AXIS:
                this.translation = (graphicalCard, mouseState) ->
                        setTTNode(graphicalCard).setByY(mouseState == ENTER ? translateAmount
                                                                            : -graphicalCard.getTranslateY());

                this.coordToParentComputer = graphicalCard -> (graphicalCard.boundsInParentProperty().get().getMaxY())
                        - (graphicalCard.boundsInLocalProperty().get().getMaxY());
                break;
            default: throw new IllegalArgumentException(direction + " is not an instance of enum Direction");
        }

    }

    private TranslateTransition setTTNode(Node node) {
        tt.setNode(node);
        return tt;
    }

    /**
     * @param direction Direction of translation when card are hovered
     *                  default duration = 200, (duration for hoverAnimation)
     *                  default translateY = -26, (translation over vertical axis when Card hovered)
     */
    public CTransform(Direction direction) {
        this(direction, 200, -26);
    }


    public void handleHover(MouseEvent mouseEvent, GraphicalCard card, MouseState mouseState, Consumer<GraphicalCard> action,
                            Consumer<GraphicalCard> reverseAction) {
        /*
        if (tt.getNode() != null) {
            GraphicalCard oldGraphicalCard = (GraphicalCard) tt.getNode();
            System.out.println(oldGraphicalCard);
            translation.accept(oldGraphicalCard, EXIT);
        }
*/
        if (mouseState == ENTER && (coordToParentComputer.apply(card)) >= 0) {

            tt.setNode(card);
            translation.accept(card, mouseState);
            card.setHovered(true); //also sets anyHovered to true

            action.accept(card);
            tt.play();
            tt.setOnFinished(ActionEvent::consume);

        } else if (mouseState == EXIT && card.hovered()) {

            translation.accept(card, mouseState);
            //System.out.println("On exit : " + getTranslate(card));
            if (getTranslate(card) == 0) {
                card.setHovered(false);
                setAnyHovered(false);
            }
            reverseAction.accept(card);
            tt.play();
            tt.setOnFinished(finishedEvent -> {
                tt.setNode(null);
                //oldNode set null to ensure only card is hovered at a time :
                // downside is that since cards are close => transition from one card to another without completely exiting the frame between 2 cards
                // it will be considered as not exiting
                finishedEvent.consume();
            });
        }
        mouseEvent.consume();

    }

    /**
     * Handle the mouseEvent when users hovers card,
     * makes it "step" forward a bit.
     * Warning ! : Not isEmpty check => will produce NPE or index out of bounds if that's the case
     * @param mouseEvent MouseEvent
     * @param card       Card
     * @param mouseState MouseState (mouse enters or exit)
     */
    private void handleHover(MouseEvent mouseEvent, GraphicalCard card, MouseState mouseState) {

        if (mouseState == ENTER && card.getTranslateY() != 0 || card.getTranslateX() != 0) {
            translation.accept(card, EXIT);
        }

        if (mouseState == ENTER && (coordToParentComputer.apply(card)) >= 0) {

            tt.setNode(card);
            translation.accept(card, mouseState);
            card.setHovered(true); //also sets anyHovered to true
            tt.play();
            tt.setOnFinished(ActionEvent::consume);

        } else if (mouseState == EXIT && card.hovered()) {

            translation.accept(card, mouseState);
            //System.out.println("On exit : " + getTranslate(card));
            if (getTranslate(card) == 0) {
                card.setHovered(false);
                setAnyHovered(false);
            }

            tt.play();
            tt.setOnFinished(finishedEvent -> {
                tt.setNode(null);
                //oldNode set null to ensure only card is hovered at a time :
                // downside is that since cards are close => transition from one card to another without completely exiting the frame between 2 cards
                // it will be considered as not exiting
                finishedEvent.consume();
            });
        }
        mouseEvent.consume();
    }

    private double getTranslate(GraphicalCard graphicalCard) {
        return direction == Direction.X_AXIS ? graphicalCard.getTranslateX() : graphicalCard.getTranslateY();
    }

    /**
     * Adds all the required Event listeners to the card given in parameter
     * @param card Card
     */
    public void setUpTransformHandling(GraphicalCard card) {
        card.setOnMouseEntered(e -> handleHover(e, card, ENTER));
        card.setOnMouseExited(e -> handleHover(e, card, EXIT));
    }
}
