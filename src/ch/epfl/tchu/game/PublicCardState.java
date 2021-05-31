package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 * The purpose of this class is to represent the informations that players have access to on wagon/locomotive cards that are not in their hands
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public class PublicCardState {

    private final int deckSize, discardsSize;
    private final List<Card> faceUpCards;

    /**
     * Unique constructor of the class
     *
     * @param faceUpCards  list of the five visible wagon/locomotive cards
     * @param deckSize     size of the draw deck
     * @param discardsSize size of the discard deck
     * @throws IllegalArgumentException if faceUpCards do not contain exactly five elements, or if the size of the draw or discard is negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == FACE_UP_CARDS_COUNT &&
                deckSize >= 0 &&
                discardsSize >= 0);

        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
        this.faceUpCards = List.copyOf(faceUpCards);
    }

    /**
     * Gives the five wagon/locomotive face up cards
     *
     * @return an immutable list containing the five face up cards
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * Gives the face up card placed at a specified slot of the list
     *
     * @param slot the index of the list containing the face up cards that we want to examine
     * @return the face up card at the given index
     *
     * @throws IndexOutOfBoundsException if this index is not between 0 (included) and 5 (excluded)
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * Gives the size of the the draw deck
     *
     * @return the size of the the draw deck
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Informs about the emptiness of the draw deck
     *
     * @return true if de draw deck is empty or false if not
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * Gives the size of the the discard deck
     *
     * @return the size of the the discard deck
     */
    public int discardsSize() {
        return discardsSize;
    }

}
