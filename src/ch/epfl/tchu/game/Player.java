package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Represents a player
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public interface Player {

    /**
     * This enum represents the three types of actions that a tCHu player can perform during a turn
     */
    enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         * Returns an immutable list containing the constants of this enum type, in the order they're declared
         */
        public final static List<TurnKind> ALL = List.of(values());
    }

    /**
     * Communicates to the player his own identity, as well as the names of the different players (including his own)
     *
     * @param ownId the identity of the player
     * @param playerNames a Map associating the ID of a player to his name
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames, Boolean rematch);

    /**
     * Method called each time a piece of information must be communicated to the player during the game
     *
     * @param info String containing the information to communicate to the player
     */
    void receiveInfo(String info);

    /**
     * Informs the player of the public component of a new state of the game, as well as of his own state
     *
     * @param newState the new PublicGameState of the game
     * @param ownState the complete state of the player
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Gives the player the five tickets that were distributed to him at the beginning of the game
     *
     * @param tickets the five tickets that were distributed to the player at the beginning of the game
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Asks the player which ticket, among those that were initially distributed, he wants to keep
     *
     * @return a SortedBag of the tickets the player wants to keep
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Method called, at the beginning of a player's turn, to find out what type of action he wants to perform
     *
     * @return the TurnKind representing the action the player wants to execute
     */
    TurnKind nextTurn();

    /**
     * Method called when a player has decided to draw additional tickets during the game, to let him know which tickets
     * were drawn and which ones he wants to keep
     *
     * @param options a SortedBag of the drawn tickets
     * @return a SortedBag of the tickets the player wants to keep
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Method called when the player has decided to draw car/locomotive cards, to know where he wants to draw them from
     *
     * @return one value from 0 to 4 (both included) if the player wants to draw a face-up card, or -1 if he want to draw
     *         a card from the draw deck
     */
    int drawSlot();

    /**
     * Method called when the player tries to take a road, to know which road it is
     *
     * @return the road the player tries to take
     */
    Route claimedRoute();

    /**
     * Method called when the player tries to take a road, to know which cards he initially wants to use for this
     *
     * @return a sortedBag with the cards the player is initially intending to use
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Method called when the player has decided to try to take over a tunnel and additional cards are needed,
     * to know which cards he wants to use for this
     *
     * @param options a List of the different associations of additional cards he could use
     * @return a SortedBag with the card he has decided to use (if the SortedBag is empty it means the player does not want
     *         to use additional cards)
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * Method called at the end of the game to know if the player want a rematch
     */
    void askForRematch();

    /**
     * Method called to know the player answer for a rematch
     * @return the answer
     */
    Boolean rematchResponse();
}
