package ch.epfl.tchu.game;

import java.util.List;
import java.util.stream.Collectors;
import static ch.epfl.tchu.gui.StringsFr.*;

/**
 * Enum representing the different type of cards used in the game
 *
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public enum Card {
    BLACK (Color.BLACK, BLACK_CARD),
    VIOLET (Color.VIOLET, VIOLET_CARD),
    BLUE (Color.BLUE, BLUE_CARD),
    GREEN (Color.GREEN, GREEN_CARD),
    YELLOW (Color.YELLOW, YELLOW_CARD),
    ORANGE (Color.ORANGE, ORANGE_CARD),
    RED (Color.RED, RED_CARD),
    WHITE (Color.WHITE, WHITE_CARD),
    LOCOMOTIVE (null, LOCOMOTIVE_CARD);

    private final Color color;
    private final String french;


    /**
     * Returns an immutable list containing the constants of this enum type, in the order they're declared
     */
    public final static List<Card> ALL = List.of(values());

    /**
     * Returns an immutable list containing the constants of this enum type representing cars
     * (i.e. ALL without LOCOMOTIVE), in the order they're declared
     */
    public final static List<Card> CARS = ALL.stream()
            .filter(card -> card.color != null)
            .collect(Collectors.toUnmodifiableList());

    /**
     * Returns the numbers of constants in this enum type
     */
    public final static int COUNT = ALL.size();

    /**
     * Basic constructor for Card indicating its color
     *
     * @param color to determine the color of the card (or none if the card is a locomotive)
     * @param french String french name in StringsFr
     */
    Card(Color color, String french) {
        this.color = color;
        this.french = french;
    }

    /**
     * Gives the type of card matching the given color
     *
     * @param color color to match to a wagon
     * @return the type of wagon Card corresponding to given color
     */
    public static Card of(Color color) {
        return ALL.stream()
                .filter(card -> card.color().equals(color))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new); //will never happen because all colors are supported
    }

    /**
     * Gives the french name of <code>this</code>
     * @return the field <code>french</code> of <code>this</code>
     */
    public String french() {
        return french;
    }


    /**
     * Gives the color of the card to which it is applied
     *
     * @return {@code Color} associated to this wagon or null if {@code this} is a locomotive
     */
    public Color color() {
        return color;
    }

}