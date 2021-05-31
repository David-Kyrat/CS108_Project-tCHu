package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents the public part of the state of a game
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId, lastPlayer;
    private final Map<PlayerId, PublicPlayerState> playerState;

    /**
     * Unique public constructor of PublicGameState
     *
     * @param ticketsCount    the size of the tickets deck
     * @param cardState       public state of the wagon/locomotive cards
     * @param currentPlayerId the identity of the player in activity
     * @param playerState     a Map containing the identity of the to players associated with their public states
     * @param lastPlayer      the identity of the last player who will play in the game
     * @throws IllegalArgumentException if the tickets deck size is strictly negative or if playerState does not contain
     *                                  exactly two pairs key/value
     * @throws NullPointerException     if cardState or currentPlayerId is null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == PlayerId.COUNT);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.lastPlayer = lastPlayer;
        this.playerState = Map.copyOf(playerState);
    }

    /**
     * Gives the size of the tickets deck
     *
     * @return the size of the tickets deck
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Returns true if a player can draw a card from the tickets deck
     *
     * @return true if and only if ticketsCount is greater than 0
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }

    /**
     * Gives the public part of the wagon/locomotive card state.
     *
     * @return the public part of the wagon/locomotive card state.
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * Returns true if it is possible to draw cards from the draw deck
     *
     * @return true if the draw deck and the discard deck contain at least 5 cards
     */
    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize()) >= 5;
    }

    /**
     * Gives the identity of the active player
     *
     * @return the identity of the active player
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Gives the state of the player whose identity is given
     *
     * @param playerId the identity of a player
     * @return the state of the given player
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Gives the state of the active player
     *
     * @return the state of the active player
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * Gives the totality of the roads which the players has taken over
     *
     * @return a list of the totality of the roads which the players has taken over
     */
    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>();
        playerState.forEach((id, state) -> claimedRoutes.addAll(state.routes()));
        return claimedRoutes;
    }

    /**
     * Gives the identity of the inactive player or null if the last round has not started
     *
     * @return the identity of the inactive player or null if the last round has not started
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
