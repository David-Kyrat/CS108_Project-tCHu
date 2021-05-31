package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.PlayerId.*;

/**
 * This class completely represents the state of a game
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class GameState extends PublicGameState {

    private final Deck<Ticket> ticketsDeck;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;

    private GameState(Deck<Ticket> ticketsDeck, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState,
                      PlayerId lastPlayer) {
        super(ticketsDeck.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.ticketsDeck = ticketsDeck;
        this.cardState = cardState;

        this.playerState = new HashMap<>(playerState);
    }

    /**
     * Gives the initial state of a game
     * @param tickets the tickets deck
     * @param rng random generator to help choose the first player and mix the ticket deck
     * @return a GameState with the ticket deck containing the given tickets and the draw deck is complete except for
     *         the top 8 cards, which are dealt to the players. The decks are shuffled using the given random generator,
     *         which is also used to randomly select the identity of the first player
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        int initialCardsBothPlayer = INITIAL_CARDS_COUNT * COUNT;
        Deck<Card> drawDeck = Deck.of(ALL_CARDS, rng);
        List<Card> initCards = drawDeck.topCards(initialCardsBothPlayer).toList();
        drawDeck = drawDeck.withoutTopCards(initialCardsBothPlayer);
        PlayerId initPlayer = ALL.get(rng.nextInt(COUNT));

        return new GameState(Deck.of(tickets, rng), CardState.of(drawDeck), initPlayer, initPlayerStates(initCards), null);
    }

    private static Map<PlayerId, PlayerState> initPlayerStates(List<Card> initCard) {
        Function<Integer, SortedBag<Card>> sortedBagProvider = playerNb ->
                SortedBag.of(initCard.subList(INITIAL_CARDS_COUNT * playerNb, INITIAL_CARDS_COUNT * (playerNb + 1)));

        return ALL.stream()
                           .collect(Collectors.toMap(id -> id,
                                                     id -> PlayerState.initial(sortedBagProvider.apply(id.ordinal()))));
    }

    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /*
    ===================================================
    First group of methods concerning tickets and cards
    ===================================================
    */

    /**
     * Gives the given number of tickets from the top of the tickets deck
     * @param count the number of tickets we want
     * @return a SortedBag with the given number of tickets from the top of the tickets deck
     *
     * @throws IllegalArgumentException if count is not between 0 and the tickets deck size (both included)
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return ticketsDeck.topCards(count);
    }

    /**
     * Gives an identical state to the receiver, but without the given number of tickets from the top of the tickets deck
     * @param count the number of tickets we want to remove from the top of the tickets deck
     * @return an identical state to the receiver, but without the given number of tickets from the top of the tickets deck
     *
     * @throws IllegalArgumentException if count is not between 0 and the tickets deck size (both included)
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return new GameState(ticketsDeck.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * Gives the card to the top of the draw deck
     * @return the card to the top of the draw deck
     *
     * @throws IllegalArgumentException if the deck is empty
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * Gives a state identical to the original but without the card on top of the draw deck
     * @return a state identical to the original but without the card on top of the draw deck
     *
     * @throws IllegalArgumentException if the draw deck is empty
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(ticketsDeck, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * Gives a state identical to the original but with the given cards added to the discard deck.
     * @param discardedCards the cards we want to add to the discard deck
     * @return a state identical to the original but with the given cards added to the discard deck.
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(ticketsDeck, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * Gives a state identical to the original unless the deck is empty, in which case the method gives a new one with a draw deck
     * recreated with the cards from the discard deck
     * @param rng Random generator used to shuffle (if needed) the new draw deck
     * @return a state identical to the original unless the draw deck is empty, in which case a new GameState is
     *         recreated with a new draw deck containing the cards of the discard deck and shuffled using the given
     *         random generator
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.isDeckEmpty() ? new GameState(ticketsDeck, cardState.withDeckRecreatedFromDiscards(rng),
                                                       currentPlayerId(), playerState, lastPlayer())
                                       : this;
    }

    /*
    ========================================================================================================================
    Second group of methods allowing us to obtain a state derived from the original in response to actions taken by a player
    ========================================================================================================================
    */

    /**
     * Gives a state identical to the original but in which the given tickets have been added to the given player's hand
     * @param playerId the ID of the player to whom we want to give the tickets
     * @param chosenTickets a SortedBag of the tickets we want to give
     * @return a state identical to the original but in which the given tickets have been added to the given player's hand
     *
     * @throws IllegalArgumentException if the given player already has at least one ticket
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).ticketCount() == 0);

        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        newMap.replace(playerId, playerState(playerId).withAddedTickets(chosenTickets));

        return new GameState(ticketsDeck, cardState, currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * Gives a state identical to the original, but in which the current player has chosen to keep (or not) tickets
     * from the ones he drew
     * @param drawnTickets a sortedBag of the tickets the player has drawn from the draw deck
     * @param chosenTickets a SortedBag of the tickets the player wants to keep
     * @return a state identical to the original, but in which the current player has choose to keep the tickets of
     *         chosenTicket from the one's of drawnTickets
     *
     * @throws IllegalArgumentException if the tickets kept are not included in the tickets drawn
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        newMap.replace(currentPlayerId(), currentPlayerState().withAddedTickets(chosenTickets));

        return new GameState(ticketsDeck.withoutTopCards(drawnTickets.size()), cardState, currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * Gives a state identical to the original except that the face-up card at the given slot has been placed in the
     * current player's hand, then replaced by the one at the top of the deck
     * @param slot the index of the face-up card that will be placed in the current player hand
     * @return a state identical to the original except that the face-up card at the given slot has been placed in the
     *         current player's hand, and replaced by the one at the top of the deck
     *
     * @throws IllegalArgumentException if it is not possible to draw cards
     */
    public GameState withDrawnFaceUpCard(int slot) {

        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        newMap.replace(currentPlayerId(), currentPlayerState().withAddedCard(cardState.faceUpCard(slot)));

        return new GameState(ticketsDeck, cardState.withDrawnFaceUpCard(slot), currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * Gives a state identical to the original except that the top card of the draw deck has been placed in the current player's hand
     * @return a state identical to the original except that the top card of the draw deck has been placed in the current player's hand
     *
     * @throws IllegalArgumentException if it is not possible to draw cards
     */
    public GameState withBlindlyDrawnCard() {

        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        newMap.replace(currentPlayerId(), currentPlayerState().withAddedCard(topCard()));

        return new GameState(ticketsDeck, cardState.withoutTopDeckCard(), currentPlayerId(), newMap, lastPlayer());
    }

    /**
     * Gives a state identical to the original one but in which the current player has taken over the given roads by means of the given cards
     * @param route the roads the player has taken over
     * @param cards the cards the player used
     * @return a state identical to the original one but in which the current player has taken over the given roads by means of the given cards
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> newMap = new HashMap<>(playerState);
        newMap.replace(currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));

        return new GameState(ticketsDeck, cardState.withMoreDiscardedCards(cards), currentPlayerId(), newMap, lastPlayer());
    }

    /*
    ===================================================================================================================
    Third group of methods allowing us to determine when the last turn starts, and to manage the end of a player's turn
    ===================================================================================================================
    */

    /**
     * Tells if the last turn is starting
     * @return true if and only if the last turn is starting
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }

    /**
     * Gives a state identical to the original except that the current player will be the one following the actual current player.
     * In addition to that, if <code>lastTurnBegins</code> returns true, the current player becomes the last player.
     * @return a state identical to the original except that the current player will be the one following the actual current player.
     *         In addition to that, if <code>lastTurnBegins</code> returns true, the current player becomes the last player.
     */
    public GameState forNextTurn() {
        PlayerId lastPlayer = lastTurnBegins() ? currentPlayerId() : lastPlayer();
        return new GameState(ticketsDeck, cardState, currentPlayerId().next(), playerState, lastPlayer);
    }

}
