package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The purpose of this class is to represent a pile of cards from the game
 * (therefore we are not talking only about the cards represented by the class Card)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class Deck<C extends Comparable<C>> {

    private final List<C> cards;
    private final int size;

    private Deck(List<C> cards) {
        this.cards = List.copyOf(cards);
        this.size = cards.size();
    }

    /**
     * Creates a deck of cards having the same cards as the given SortedBag but shuffled using a specific random number generator
     * @param cards a SortedBag of the cards that should be in the Deck
     * @param rng the random number generator that we want to use
     * @return a deck of cards having the same cards as the given SortedBag but shuffled
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> newCards = cards.toList();
        Collections.shuffle(newCards, rng);
        return new Deck<>(newCards);
    }

    /**
     * Gives the number of cards of the deck
     * @return the size of the Deck
     */
    public int size() {
        return size;
    }

    /**
     * Informs about the emptiness of the Deck
     * @return true if the Deck is empty or false if not
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Tells the card at the top of the deck
     * @return the element at the top of the Deck
     *
     * @throws IllegalArgumentException if the deck is empty
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return cards.get(0);
    }

    /**
     * Gives a new Deck, identical to the original one, except that it does not have the card that was at the top
     * @return a new Deck, identical to the original one, except that it does not have his first element
     *
     * @throws IllegalArgumentException if the deck is empty
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());
        return withoutTopCards(1);
    }

    /**
     * Gives a SortedBag containing the given number of cards of the top of the Deck
     * @param count the given number of cards of the top of the Deck that we want to extract
     * @return a SortedBag containing the given number of cards of the top of the Deck
     *
     * @throws IllegalArgumentException if count is not between 0 and the deck size (both included)
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size);

        SortedBag.Builder<C> sortedBag = new SortedBag.Builder<>();
        cards.stream()
             .limit(count)
             .forEach(sortedBag::add);

        return sortedBag.build();
    }

    /**
     * Gives a new Deck, identical to the original one, but without the given number of cards of the top
     * @param count the given number of cards of the top of the Deck that we want to remove
     * @return a new Deck, identical to the original one, but without the given number of cards of the top
     *
     * @throws IllegalArgumentException if count is not between 0 and the deck size (both included)
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size);

        List<C> newCards = cards.subList(count, size);

        return new Deck<>(newCards);
    }
}
