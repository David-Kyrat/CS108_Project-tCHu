package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;

/**
 * Class containing the constants used in the GUI package
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 */
final class ConstantsGUI {

    /**
     * Private constructor to remove the default one and make ConstantsGUI not instantiable
     */
    private ConstantsGUI() {
        throw new UnsupportedOperationException();
    }

    /**
     * Height of the rectangle representing the external part of a card representation
     */
    public static final int EXT_RECT_HEIGHT = 90;

    /**
     * Width of the rectangle representing the external part of a card representation
     */
    public static final int EXT_RECT_WIDTH = 60;

    /**
     * Height of the rectangle representing the interior part of a card representation
     */
    public static final int INT_RECT_HEIGHT = 70;

    /**
     * Width of the rectangle representing the interior part of a card representation
     */
    public static final int INT_RECT_WIDTH = 40;

    /**
     * Height of the rectangle representing the gauge of a deck button
     */
    public static final int GAUGE_HEIGHT = 5;

    /**
     * Width of the rectangle representing the gauge of a deck button
     */
    public static final int GAUGE_WIDTH = 50;

    /**
     * Height of the rectangle representing a track of a route (or a wagon)
     */
    public static final int ROUTE_RECT_HEIGHT = 12;

    /**
     * Width of the rectangle representing a track of a route (or a wagon)
     */
    public static final int ROUTE_RECT_WIDTH = 36;

    /**
     * Height alignment for a wagon circle in the rectangle
     */
    public static final int WAGON_CIRCLE_HEIGHT = 6;

    /**
     * Width alignment for the first wagon circle in the rectangle
     */
    public static final int FIRST_WAGON_CIRCLE_WIDTH = 12;

    /**
     * Width alignment for the second wagon circle in the rectangle
     */
    public static final int SECOND_WAGON_CIRCLE_WIDTH = FIRST_WAGON_CIRCLE_WIDTH * 2;

    /**
     * Radius of the circle of a wagon
     */
    public static final int WAGON_CIRCLE_RADIUS = 3;

    /**
     * Radius of the circle with the color of a player
     */
    public static final int COLORED_CIRCLE_RADIUS = 5;

    /**
     * Maximum count of information that should be displayed
     */
    public static final int DISPLAYED_INFO_COUNT = 5;

    /**
     * A String representing a long dash between to spaces : " — "
     */
    public static final String LONG_DASH_SEPARATOR = " — ";

    /**
     * A String representing a space : " "
     */
    public static final String SPACE_SEPARATOR = " ";

    /**
     * A String representing a coma and a space : ", "
     */
    public static final String COMA_SEPARATOR = ", ";

    /**
     * A String representing an underscore : "_"
     */
    public static final String UNDERSCORE_SEPARATOR = "_";

    // Default names for the players
    /**
     * Default name of the first player
     */
    public static final String ADA = "Ada";

    /**
     * Default name of the second player
     */
    public static final String CHARLES = "Charles";

    // Stylesheet names used
    /**
     * Stylesheet name for the map
     */
    public static final String MAP_CSS = "map.css";

    /**
     * Stylesheet name for the colors
     */
    public static final String COLORS_CSS = "colors.css";

    /**
     * Stylesheet name for the decks
     */
    public static final String DECKS_CSS = "decks.css";

    /**
     * Stylesheet name for the information
     */
    public static final String INFO_CSS = "info.css";

    /**
     * Stylesheet name for the chooser
     */
    public static final String CHOOSER_CSS = "chooser.css";

    // Class names used
    /**
     * Class name : route
     */
    public static final String ROUTE_CLASS = "route";

    /**
     * Class name : track
     */
    public static final String TRACK_CLASS = "track";

    /**
     * Class name : filled
     */
    public static final String FILLED_CLASS = "filled";

    /**
     * Class name : car
     */
    public static final String CAR_CLASS = "car";

    /**
     * Class name : card
     */
    public static final String CARD_CLASS = "card";

    /**
     * Class name : count
     */
    public static final String COUNT_CLASS = "count";

    /**
     * Class name : outside
     */
    public static final String OUTSIDE_CLASS = "outside";

    /**
     * Class name : inside
     */
    public static final String INSIDE_CLASS = "inside";

    /**
     * Class name : train-image
     */
    public static final String TRAIN_IMAGE_CLASS = "train-image";

    /**
     * Class name : gauged
     */
    public static final String GAUGED_CLASS = "gauged";

    /**
     * Class name : foreground
     */
    public static final String FOREGROUND_CLASS = "foreground";

    /**
     * Class name : background
     */
    public static final String BACKGROUND_CLASS = "background";

    /**
     * String representing the case of a null Color
     */
    public static final String NEUTRAL = "NEUTRAL";

    /**
     * Same as <code>this.color().name()</code> except that when color is null, it returns "NEUTRAL" instead.
     *
     * @return <code>name</code> of <code>color</code> as a String
     */
    public static String colorClass(Color color) {
        return color == null ? NEUTRAL : color.name();
    }
}
