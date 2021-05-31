package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

import static ch.epfl.tchu.game.Constants.*;

/**
 * The purpose of this class is to represent the visible and the hidden informations on wagon/locomotive cards
 * that are not in the player's hands
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> drawDeck;
    private final SortedBag<Card> discardDeck;

    private CardState(List<Card> faceUpCards, Deck<Card> drawDeck, SortedBag<Card> discardDeck) {
        super(faceUpCards, drawDeck.size(), discardDeck.size());
        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
    }

    /**
     * Gives a CardState in which the 5 cards placed face up are the first 5 of the given deck,
     * the draw deck is made up of the remaining cards, and the discard deck is empty.
     * @param deck a deck containing some wagon/locomotive cards
     * @return a CardState in which the 5 cards placed face up are the first 5 of the given deck,
     *         the draw deck is made up of the remaining cards, and the discard deck is empty.
     *
     * @throws IllegalArgumentException if the given deck contains less than 5 cards
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = deck.topCards(FACE_UP_CARDS_COUNT).toList();
        Deck<Card> drawDeck = deck.withoutTopCards(FACE_UP_CARDS_COUNT);
        return new CardState(faceUpCards, drawDeck, SortedBag.of());
    }

    /**
     * Gives a new CardState identical to the original, except that the face up card at the given index slot has been
     * replaced by the card at the top of the draw deck, which is also removed from it.
     * @param slot the index representing the position of the replaced face up card
     * @return a new CardState identical to the original, except that the face up card at the given index slot has been
     *         replaced by the card at the top of the draw deck, which is also removed from it
     *
     * @throws IndexOutOfBoundsException if the given index is not between 0 (included) and 5 (excluded)
     * @throws IllegalArgumentException  if the draw deck is empty
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!isDeckEmpty());

        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        newFaceUpCards.set(slot, drawDeck.topCard());

        return new CardState(newFaceUpCards, drawDeck.withoutTopCard(), discardDeck);
    }

    /**
     * Tells the card at the top of the deck
     * @return the card at the top of the drawn deck
     *
     * @throws IllegalArgumentException if the deck is empty
     */
    public Card topDeckCard() {
        return drawDeck.topCard();
    }

    /**
     * Gives a new CardState identical to the original, but without the card at the top of the draw deck
     * @return a new CardState identical to the original, but without the card at the top of the draw deck
     *
     * @throws IllegalArgumentException if the draw deck is empty
     */
    public CardState withoutTopDeckCard() {
        return new CardState(faceUpCards(), drawDeck.withoutTopCard(), discardDeck);
    }

    /**
     * Gives a new CardState identical to the original, except that the cards in the discard deck
     * have been shuffled using the given random generator to make up the new draw deck
     * @param rng the random number generator that we want to use
     * @return a new CardState identical to the original, except that the cards in the discard deck
     *         have been shuffled using the given random generator to make up the new draw deck
     *
     * @throws IllegalArgumentException if the draw deck is not empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());
        return new CardState(faceUpCards(), Deck.of(discardDeck, rng), SortedBag.of());
    }

    /**
     * Gives a CardState identical to the original, but with the data cards added to the discard deck
     * @param additionalDiscards the additional cards to add at the discard deck
     * @return a CardState identical to the original, but with additional cards added to the discard deck
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), drawDeck, discardDeck.union(additionalDiscards));
    }
}
