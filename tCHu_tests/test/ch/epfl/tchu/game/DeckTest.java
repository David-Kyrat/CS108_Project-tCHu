package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    private Deck<Integer> EMPTY_DECK = Deck.of(SortedBag.<Integer>of(Collections.emptyList()), new Random());

    @Test
    void size() {
        for (int i = 0; i < 25; i++) {
            SortedBag.Builder<Integer> builder = new SortedBag.Builder<>();
            builder.add(i, 14);
            Deck<Integer> deck = Deck.of(builder.build(), new Random());
            assertEquals(i, deck.size());
        }
    }

    @Test
    void isEmpty() {
        for (int i = 0; i < 25; i++) {
            SortedBag.Builder<Integer> builder = new SortedBag.Builder<>();
            builder.add(i, 14);
            Deck<Integer> deck = Deck.of(builder.build(), new Random());

            assertEquals(i == 0, deck.isEmpty());
        }
    }

    @Test
    void topCard() {
        SortedBag.Builder<Integer> builder = new SortedBag.Builder<>();
        builder.add(14);
        builder.add(15);
        Deck<Integer> deck = Deck.of(builder.build(), new Random(1));
        assertEquals(deck.topCard(), 14);
        assertThrows(IllegalArgumentException.class, () -> EMPTY_DECK.topCard());
    }

    @Test
    void topCards() {
        SortedBag.Builder<Integer> builder = new SortedBag.Builder<>();
        builder.add(14);
        builder.add(15);
        Deck<Integer> deck = Deck.of(builder.build(), new Random(1));
        SortedBag<Integer> topCards = deck.topCards(2);
        assertEquals(topCards.get(0), 14);
        assertEquals(topCards.get(1), 15);
        assertThrows(IllegalArgumentException.class, () -> EMPTY_DECK.topCards(-1));
        assertThrows(IllegalArgumentException.class, () -> EMPTY_DECK.topCards(1));
        assertThrows(IllegalArgumentException.class, () -> deck.topCards(3));
    }

    @Test
    void withoutTopCard() {
        SortedBag.Builder<Integer> builder = new SortedBag.Builder<>();
        builder.add(14);
        builder.add(15);
        Deck<Integer> deck = Deck.of(builder.build(), new Random(1));
        Deck<Integer> topCards = deck.withoutTopCard();
        assertEquals(topCards.topCard(), 15);
        assertThrows(IllegalArgumentException.class, () -> EMPTY_DECK.withoutTopCard());
    }

    @Test
    void withoutTopCards() {
        SortedBag.Builder<Integer> builder = new SortedBag.Builder<>();
        builder.add(14);
        builder.add(15);
        Deck<Integer> deck = Deck.of(builder.build(), new Random(1));
        Deck<Integer> topCards = deck.withoutTopCards(2);
        assertTrue(topCards.isEmpty());
        assertThrows(IllegalArgumentException.class, () -> EMPTY_DECK.withoutTopCards(3));
    }
}