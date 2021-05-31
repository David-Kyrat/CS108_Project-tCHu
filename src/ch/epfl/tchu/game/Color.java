package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enum representing the different colors used to characterize the cards and the roads of the game
 *
 * @author Noah Munz (310779)
 */
public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * Returns an immutable list containing the constants of this enum type, in the order they're declared
     */
    public final static List<Color> ALL = List.of(values());

    /**
     * Returns the numbers of constants in this enum type
     */
    public final static int COUNT = ALL.size();
}
