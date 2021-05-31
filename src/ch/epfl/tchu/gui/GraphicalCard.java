package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.CTransform.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;

import static ch.epfl.tchu.gui.GraphicalCard.CardPosition.*;
import static ch.epfl.tchu.gui.Nodes.*;

/**
 * @author Noah Munz (310779)
 */
final class GraphicalCard extends StackPane {

    private final static int RECT_OUT_W = 60, RECT_OUT_H = 90, RECT_W = 40, RECT_H = 70;
    private static boolean isAnyHovered = false;

    private final CardPosition type;
    private final Color color;
    private final CTransform trans;
    private boolean isHovered;

    // private static StackPane structure = initStructure();

    /**
     * Primary constructor of GrapicalCard where all field can be specified
     * @param type type/position of the card i.e. is it a faceUpCard or card in the hand of a player
     * @param color color of this card, can be nu
     * @param multiplicity (Can be null) Amount of <code>this</code> in hand of player if <code>type == HAND</code>
     *                     null otherwise
     */
    GraphicalCard(CardPosition type, Color color, ReadOnlyIntegerProperty multiplicity) {
        this.type = type;
        this.color = color;
        isHovered = false;
        this.getStyleClass().addAll(colorAsString(color), "card");
        this.getChildren().addAll(withClass(new Rectangle(RECT_OUT_W, RECT_OUT_H), "outside"),
                                  withClass(new Rectangle(RECT_W, RECT_H), "filled", "inside"),
                                  withClass(new Rectangle(RECT_W, RECT_H), "train-image"));

        Text countText = type == HAND ? createCounter(multiplicity) : null;

        Direction direction = type == FACEUP ? Direction.X_AXIS
                                                        : Direction.Y_AXIS;
        this.trans = new CTransform(direction);
        trans.setUpTransformHandling(this);
        //if we get more types replace by a switch
    }

    /**
     * Constructor used when we want to redefine the default children nodes of a <code>Card</code>
     * @param nodes children nodes of <code>this</code>
     */
    GraphicalCard(Direction direction, Node... nodes) {
        this.type = null;
        this.color = null;
        isHovered = false;
        this.trans = new CTransform(direction);
        trans.setUpTransformHandling(this);
        getChildren().addAll(nodes);
    }

    private Text createCounter(ReadOnlyIntegerProperty multiplicity) {
        Text countText = new Text(String.valueOf(multiplicity));
        countText.textProperty().bind(multiplicity.asString());
        countText.visibleProperty().bind(Bindings.greaterThan(multiplicity, 0));
        this.getChildren().add(countText);

        return countText;
    }

    /**
     * Same as <code>this.color().name()</code> except that when color is null, it returns "NEUTRAL" instead.
     * @return <code>name</code> of <code>color</code> as a String
     */
    public static String colorAsString(Color color) {
        return color == null ? "NEUTRAL" : color.name();
    }

    public static boolean anyHovered() {
        return isAnyHovered;
    }

    public static void setAnyHovered(boolean isAnyHovered) {GraphicalCard.isAnyHovered = isAnyHovered;}

    public boolean hovered() {
        return isHovered;
    }

    public void setHovered(boolean hovered) {
        isHovered = hovered;
        if (hovered) setAnyHovered(true);
    }

    enum CardPosition {FACEUP, HAND}
}
