package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;


import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;


/**
 * @author Noah Munz (310779)
 *         NB: IAE := Shorthand notation for IAEeption
 */
public class GameStateTest {

    private final Random rng = TestRandomizer.newRandom();
    private final GameState gsEmpty = GameState.initial(SortedBag.of(), rng);
    private final SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
    private final GameState gs = GameState.initial(tickets, rng);


    @Test
    void topTicketsThrowsIAE() {
        assertThrows(IllegalArgumentException.class, () -> gs.topTickets(rng.nextInt(100) * -1));
        assertThrows(IllegalArgumentException.class, () -> gs.topTickets(gs.ticketsCount() + 1));
    }

  /*  @Test
    void topTicketsReturnTheRightValue() {
        int bound = 5;
        List<Ticket> list = IntStream.range(tickets.size() - bound, tickets.size())
                                     .mapToObj(tickets::get)
                                     .collect(Collectors.toUnmodifiableList());

        SortedBag<Ticket> sb = SortedBag.of(list);
        List<Ticket> temp = new ArrayList<>();

        for (int i = tickets.size() - bound; i < tickets.size(); i++) {
            temp.add(tickets.get(i));
        }

        assertEquals(SortedBag.of(temp), gs.topTickets(5));

    }*/

    @Test
    void withoutTopTicketThrowsIAE() {
        assertThrows(IllegalArgumentException.class, () -> gs.withoutTopTickets(rng.nextInt(100) * -1));
        assertThrows(IllegalArgumentException.class, () -> gs.withoutTopTickets(gs.ticketsCount() + 1));
    }

    @Test
    void topCardThrowsIAE() {
        GameState currentGs = gsEmpty;
        while (!currentGs.cardState().isDeckEmpty()) {
            currentGs = currentGs.withoutTopCard();
        }

        assertThrows(IllegalArgumentException.class, currentGs::topCard);
        assertThrows(IllegalArgumentException.class, currentGs::withoutTopCard);

    }

    @Test
    void withMoreDiscardedCardsAddsToDiscardCorrectly() {
        SortedBag<Card> sb = SortedBag.of(Card.ALL.subList(0, 5));

        GameState currentGs = gs.withMoreDiscardedCards(sb);
        assertEquals(gs.cardState().discardsSize() + sb.size(), currentGs.cardState().discardsSize());
    }

   /*@Test
    void withMoreDiscardCardsWorksWithNull() {
        SortedBag<Card> sb = null;

        GameState currentGs = gs.withMoreDiscardedCards(sb);
        assertEquals(gs.cardState().discardsSize() + 0, currentGs.cardState().discardsSize());
    }*/

    @Test
    void withMoreDiscardCardsWorksWithEmptyBag() {
        SortedBag<Card> sb = SortedBag.of();

        GameState currentGs = gs.withMoreDiscardedCards(sb);
        assertEquals(gs.cardState().discardsSize() + sb.size(), currentGs.cardState().discardsSize());
    }

    @Test
    void withCardsDeckRecreatedIfNeededWorksOnNonEmptyDeck() {
        assertEquals(gs, gs.withCardsDeckRecreatedIfNeeded(rng));
    }

    @Test
    void withCardsDeckRecreatedIfNeededWorksOnEmptyDeck() {
        List<Card> cardDeck = getWholeDeck(gs);
        GameState stateDeckDiscarded = gs.withMoreDiscardedCards(SortedBag.of(cardDeck));

        List<Card> newCardDeck = getWholeDeck(stateDeckDiscarded.withCardsDeckRecreatedIfNeeded(rng));
        assertEquals(cardDeck, newCardDeck);
    }

    @Test
    void withInitiallyChosenTicketsAddsCorrectly() {
        PlayerId plyr = PLAYER_1;
        SortedBag<Ticket> subSBOfTicket = SortedBag.of(tickets.toList().subList(0, 5));
        GameState newGs = gs.withInitiallyChosenTickets(plyr, subSBOfTicket);

        if (gs.playerState(plyr).tickets().isEmpty()) {
            assertEquals(gs.playerState(plyr).tickets().union(subSBOfTicket), newGs.playerState(plyr).tickets());
        }
    }


    @Test
    void withInitiallyChosenTicketsDoesNotModifyDrawDeck() {
        PlayerId plyr = PLAYER_1;
        SortedBag<Ticket> subSBOfTicket = SortedBag.of(tickets.toList().subList(0, 5));
        GameState newGs = gs.withInitiallyChosenTickets(plyr, subSBOfTicket);

        assertEquals(getWholeDeck(gs), getWholeDeck(newGs));
    }

    @Test
    void withInitiallyChosenTicketsThrowsIAE() {
        PlayerId plyr = PLAYER_1;
        List<Ticket> ticketList = tickets.toList();
        SortedBag<Ticket> subSBOfTicket = SortedBag.of(ticketList.subList(0, 5));
        SortedBag<Ticket> subSBOfTicket2 = SortedBag.of(ticketList.subList(5, 10));

        GameState newGs = gs.withInitiallyChosenTickets(plyr, subSBOfTicket);

        if (!newGs.playerState(plyr).tickets().isEmpty()) {
            assertThrows(IllegalArgumentException.class, () -> newGs.withInitiallyChosenTickets(plyr, subSBOfTicket2));
        }
    }

    @Test
    void withInitiallyChosenTicketsWorksOnEmptyBag() {
        PlayerId plyr = PLAYER_1;
        GameState newGs = gs.withInitiallyChosenTickets(plyr, SortedBag.of());

        assertEquals(SortedBag.of(), newGs.playerState(plyr).tickets());
    }

    @Test
    void withChosenAdditionalTicketsAddsTicketsToPlayersHand() {
        List<Ticket> ticketList = tickets.toList().subList(0, 3);
        final SortedBag<Ticket> drawnCards = SortedBag.of(ticketList);
        final SortedBag<Ticket> chosenCards = SortedBag.of(ticketList.subList(0, 2));

        GameState newGs = gs.withChosenAdditionalTickets(drawnCards, chosenCards);

        //adds correctly the tickets
        assertEquals(cps(gs).tickets().union(chosenCards), cps(newGs).tickets());
        //method removes the tickets correctly from the deck

        //ticketsDeck of gs is just a sortedBag containing ChMap.tickets()
      /*  SortedBag<Ticket> sb1 = tickets.difference(drawnCards);
        SortedBag<Ticket> sb2 = newGs.topTickets(newGs.ticketsCount());
        boolean areEqual = IntStream.range(0, Math.max(sb1.size(), sb2.size()))
                                    .mapToObj(i -> sb2.contains(sb1.get(i)) && sb1.contains(sb2.get(i)))
                                    .noneMatch(b -> b == false);
        assertTrue(equals(sb1, sb2));
        assertTrue(areEqual);*/
    }

    @Test
    void withClaimRoutesAddRoutesCorrectly() {
        final Route route = ChMap.routes().get(1); //color is not null
        GameState oldGs = this.gs;
        SortedBag<Card> claimCards = SortedBag.of(route.length(), Card.of(route.color()));
        GameState gs = this.gs.withClaimedRoute(route, claimCards);

        BiFunction<PlayerState, Route, Integer> countInRoute = (playerState, r) ->
                Math.toIntExact(playerState.routes().stream().filter(r1 -> r1.equals(r)).count());

        int oldMult = countInRoute.apply(oldGs.currentPlayerState(), route);
        int mult = countInRoute.apply(gs.currentPlayerState(), route);

        assertEquals((oldMult + 1), mult);
        assertEquals(gs.cardState().discardsSize() - claimCards.size(), oldGs.cardState().discardsSize());
    }

    /**
     * Compares the sortedBag and indicates whether they are equal or not.
     * Where equality is defined as the mathematical set equality (i.e. A == B <=> A is a subset of B and B is a subset of A)
     * @param sb1 first set to compare
     * @param sb2 second set to compare
     * @param <E> Type of the element in the sets
     * @return true if and only if sb1 is a subset of  sb2 and vice versa
     */
    private <E extends Comparable<E>> boolean equals(SortedBag<E> sb1, SortedBag<E> sb2) {
        return sb1.contains(sb2) && sb2.contains(sb1);
    }

    @Test
    void withChosenAdditionalTicketsThrowsIAEOnChosenTicketsNotContainedInDrawnTickets() {
        List<Ticket> ticketList = tickets.toList().subList(0, 5);
        SortedBag<Ticket> drawnCards = SortedBag.of(ticketList);
        SortedBag<Ticket> chosenCards = SortedBag.of(tickets.toList().subList(5, 6));
        if (!drawnCards.contains(chosenCards)) {
            assertThrows(IllegalArgumentException.class, () -> gs.withChosenAdditionalTickets(drawnCards, chosenCards));
        }
    }

    @Test
    void withDrawnFaceUpCardThrowsIAEOnFalseCanDrawCards() {
     /*   GameState currentState = gs;
        while (currentState.canDrawCards()) currentState = currentState.withoutTopCard();*/
        final GameState finalGs = triggerCannotDrawCards(gs);
        assertThrows(IllegalArgumentException.class, () -> finalGs.withDrawnFaceUpCard(rng.nextInt(Constants.FACE_UP_CARDS_COUNT)));
    }

    @Test
    void withDrawnFaceUpCardAddsAndReplaceCardsCorrectly() {
        final int slot = rng.nextInt(Constants.FACE_UP_CARDS_COUNT);
        final Card oldCardInSlot = gs.cardState().faceUpCard(slot);
        final GameState newGs = gs.withDrawnFaceUpCard(slot);

        final PlayerState addedCardPs = cps(gs).withAddedCard(oldCardInSlot);
        assertEquals(addedCardPs.cards(), newGs.currentPlayerState().cards()); //playerState adds correctly card to the hand
        assertEquals(gs.topCard(), newGs.cardState().faceUpCard(slot)); //empty slot replaced correctly with card on top of the old deck
    }

    @Test
    void withBlindlyDrawnCardDrawsAndRemoveCardsCorrectly() {
        final GameState oldGs = this.gs;
        final GameState gs = oldGs.withBlindlyDrawnCard();
        final Card card = oldGs.topCard();
        final int oldMultiplicity = cps(oldGs).cards().countOf(card);
        final int multiplicity = cps(gs).cards().countOf(card);

        BiFunction<GameState, Card, Integer> countOfInDeck = (gameState, c) ->
                Math.toIntExact(getWholeDeck(gameState).stream().filter(c1 -> c1.equals(c)).count());

        assertEquals(oldMultiplicity + 1, multiplicity); //card on top of the oldDeck is now in player's hand
        assertEquals((countOfInDeck.apply(oldGs, card) - 1), countOfInDeck
                .apply(gs, card));//card is present 1 time less in new deck because was drawn
        //i.e. check if card was removed correctly
    }

    @Test
    void withBlindlyDrawnCardDrawsAndRemoveCardsCorrectly2() {
        final GameState oldGs = this.gs;
        final GameState gs = oldGs.withBlindlyDrawnCard();
        final Card card = oldGs.topCard();

        GameState tempCardRemoved = oldGs.withoutTopCard();
        PlayerState tempCardAddedToHand = cps(tempCardRemoved).withAddedCard(card);

        //   assertEquals(tempCardAddedToHand, cps(gs)); //check playerState of manually added card and playerState of new gameState are the same
        assertEquals((oldGs.cardState().deckSize() - 1), gs.cardState().deckSize());

    }

    @Test
    void withBlindlyDrawnCardThrowsIAEOnCannotDrawCard() {
        final GameState finalGs = triggerCannotDrawCards(gs);
        assertThrows(IllegalArgumentException.class, finalGs::withBlindlyDrawnCard);
    }

    private List<Card> getWholeDeck(GameState gameState) {
        List<Card> currentList = new ArrayList<>();
        GameState currentState = gameState;

        while (!currentState.cardState().isDeckEmpty()) {
            currentList.add(currentState.topCard());
            currentState = currentState.withoutTopCard();
        }

        return List.copyOf(currentList);
    }

  /*  private SortedBag<Ticket> allTickets(GameState gameState) {
        return gameState.topTickets(gameState.ticketsCount());
    }*/

    /**
     * @param oldGameState old GameState
     * @return new GameState which is based on the old one but in a way that canDrawCards() returns false
     */
    private GameState triggerCannotDrawCards(GameState oldGameState) {
        GameState currentState = oldGameState;
        while (currentState.canDrawCards()) currentState = currentState.withoutTopCard();
        return currentState;
    }

    /**
     * Shortand method to get faster the playerState of the current player
     * @param gameState GameState
     * @return playerState of the current player
     */
    private PlayerState cps(GameState gameState) {
        return gameState.currentPlayerState();
    }


}


