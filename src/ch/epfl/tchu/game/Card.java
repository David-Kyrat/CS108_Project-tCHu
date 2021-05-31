package ch.epfl.tchu.game;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum representing the different type of cards used in the game
 *
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public enum Card {
    BLACK (Color.BLACK),
    VIOLET (Color.VIOLET),
    BLUE (Color.BLUE),
    GREEN (Color.GREEN),
    YELLOW (Color.YELLOW),
    ORANGE (Color.ORANGE),
    RED (Color.RED),
    WHITE (Color.WHITE),
    LOCOMOTIVE (null);

    private final Color color;

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
     */
    Card(Color color) {
        this.color = color;
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
     * Gives the color of the card to which it is applied
     *
     * @return {@code Color} associated to this wagon or null if {@code this} is a locomotive
     */
    public Color color() {
        return color;
    }

}