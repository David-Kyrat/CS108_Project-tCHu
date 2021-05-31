package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;
import java.util.Set;

import static ch.epfl.tchu.gui.StringsFr.*;
import static ch.epfl.tchu.gui.ConstantsGUI.*;

/**
 * This class generates texts describing the progress of a game
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class Info {

    private final String playerName;

    /**
     * Construct a message generator related with the player with the given name
     * @param playerName the name of the player to which we link the generator
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Gives a String representation of the name of the given card (granted with its multiplicity)
     * @param card the card that we want to represent
     * @param count the multiplicity of the card
     * @return a String représentation of the name of the given card (in singular if, and only if, the absolute value of the second argument is 1)
     */
    public static String cardName(Card card, int count) {
        return cardDesignation(card) + plural(count);
    }

    /**
     * Returns the message indicating that the game ends in a draw
     * @param playerNames a List containing the names of the two players
     * @param points the points obtained by the players
     * @return A String indicating that the game ends in a draw
     */
    public static String draw(List<String> playerNames, int points) {
        return String.format(DRAW, playerNames.get(0) + AND_SEPARATOR + playerNames.get(1), points);
    }

    /**
     * Returns a message stating that the player will play first
     * @return A String stating that the player will play first
     */
    public String willPlayFirst() {
        return String.format(WILL_PLAY_FIRST, playerName);
    }

    /**
     * Returns a message stating that the player has kept the given number of tickets
     * @param count the number of tickets kept by the player
     * @return A String stating that the player has kept the given number of tickets
     */
    public String keptTickets(int count) {
        return String.format(KEPT_N_TICKETS, playerName, count, plural(count));
    }

    /**
     * Returns a message stating that it's the turn of the player
     * @return A String stating that the player can play
     */
    public String canPlay() {
        return String.format(CAN_PLAY, playerName);
    }

    /**
     * Returns a message stating that the player has drawn the given number of tickets
     * @param count the number of tickets drawn
     * @return A String stating that the player has drawn the given number of tickets
     */
    public String drewTickets(int count) {
        return String.format(DREW_TICKETS, playerName, count, plural(count));
    }

    /**
     * Returns a message stating that the player has drawn a card from the draw deck
     * @return a String stating that the player has drawn a card from the draw deck
     */
    public String drewBlindCard() {
        return String.format(DREW_BLIND_CARD, playerName);
    }

    /**
     * Returns a message stating that the player has drawn a specific face up card
     * @param card the face up card selected by the player
     * @return a String stating that the player has drawn the given face up card
     */
    public String drewVisibleCard(Card card) {
        return String.format(DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Returns a message stating that the player has taken a route using some cards
     * @param route the route the player has taken
     * @param cards the cards used by the player to take over the route
     * @return A String stating that the player has taken the given route using the given cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(CLAIMED_ROUTE, playerName, routeName(route), cardsSetDescription(cards));
    }

    /**
     * Returns a message stating that the player wishes to take an underground route using some cards
     * @param route the underground route the player want to take
     * @param initialCards the cards the player intends to use
     * @return A String stating that the player wishes to take the given underground route using the given cards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(ATTEMPTS_TUNNEL_CLAIM, playerName, routeName(route), cardsSetDescription(initialCards));
    }

    /**
     * Returns a message stating that the player has drawn the three additional cards, and if they involve
     * the use of additional cards
     * @param drawnCards the drawn cards
     * @param additionalCost the number of additional cards to use
     * @return Returns a String stating that the player has drawn the three additional cards, and if they involve
     *         the use of additional cards
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        StringBuilder message = new StringBuilder().append(String.format(ADDITIONAL_CARDS_ARE, cardsSetDescription(drawnCards)));
        if (additionalCost == 0) message.append(NO_ADDITIONAL_COST);
        else message.append(String.format(SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost)));
        return message.toString();
    }

    /**
     * Returns a message stating that the player could not (or did not want to) take an underground route
     * @param route the underground route the player could not (or did not want to) take
     * @return A String stating that the player could not (or did not want to) take an underground route
     */
    public String didNotClaimRoute(Route route) {
        return String.format(DID_NOT_CLAIM_ROUTE, playerName, routeName(route));
    }

    /**
     * Returns a message stating that the player has only two cards (or less) left, and that the last turn is starting
     * @param carCount the number of cards of the player (two or less)
     * @return a String stating that the player has only two cards (or less) left, and that the last turn is starting
     */
    public String lastTurnBegins(int carCount) {
        return String.format(LAST_TURN_BEGINS, playerName, carCount, plural(carCount));
    }

    /**
     * Returns a message stating that the player gets the bonus given by its ownership of the longest trail
     * @param longestTrail the longest trail
     * @return A String stating that the player gets the bonus given by its ownership of the longest trail
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(GETS_BONUS, playerName, longestTrail.station1().name() +
                                                     EN_DASH_SEPARATOR +
                                                     longestTrail.station2().name());
    }

    /**
     * Returns a message stating that the player wins the game with a given number of points,
     * and the number of point of his opponent
     * @param points the number of point of the player
     * @param loserPoints the number of point of his opponent
     * @return Returns a String stating that the player wins the game with a given number of points,
     *         and the number of point of his opponent
     */
    public String won(int points, int loserPoints) {
        return String.format(WINS, playerName, points, plural(points), loserPoints, plural(loserPoints));
    }

    private static String routeName(Route route) {
        return route.station1().name() + EN_DASH_SEPARATOR + route.station2().name();
    }

    private String cardsSetDescription(SortedBag<Card> cards) {
        StringBuilder description = new StringBuilder();
        Set<Card> cardsSet = cards.toSet();

        int count, index = 0;

        for (Card c : cardsSet) {
            count = cards.countOf(c);
            description.append(count)
                       .append(SPACE_SEPARATOR)
                       .append(cardName(c, count));

            if (index == cardsSet.size() - 2 && cardsSet.size() > 1) description.append(AND_SEPARATOR);
            else if (index != cardsSet.size() - 1) description.append(COMA_SEPARATOR);

            ++index;
        }
        return description.toString();
    }
}
