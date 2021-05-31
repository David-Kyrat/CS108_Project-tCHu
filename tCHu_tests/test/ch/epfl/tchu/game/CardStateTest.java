package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CardStateTest {
    final CardState generateCardState(List<Card> cards) {
        return CardState.of(Deck.of(SortedBag.of(cards), new Random(1)));
    }
    final CardState cardState = generateCardState(Card.ALL);

    @Test
    void withDrawnFaceUpCard() {
        CardState testCardState = cardState;

        List<Card> shuffledCards = new ArrayList<>(Card.ALL);
        Collections.shuffle(shuffledCards, new Random(1));
        for(int i = 0; i < 4; i++) {
            testCardState = testCardState.withDrawnFaceUpCard(i);
            assertEquals(testCardState.faceUpCard(i), shuffledCards.get(i+5));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> cardState.withDrawnFaceUpCard(6));
        assertThrows(IndexOutOfBoundsException.class, () -> cardState.withDrawnFaceUpCard(-1));
    }

    @Test
    void topDeckCard() {
        List<Card> shuffledCards = new ArrayList<>(Card.ALL);
        Collections.shuffle(shuffledCards, new Random(1));

        assertEquals(cardState.topDeckCard(), shuffledCards.get(5));
        assertThrows(IllegalArgumentException.class, () -> generateCardState(Collections.emptyList()).topDeckCard());
    }

    @Test
    void withoutTopDeckCard() {
        CardState cs = cardState;
        List<Card> shuffledCards = new ArrayList<>(Card.ALL);
        Collections.shuffle(shuffledCards, new Random(1));

        for(int i = 0; i < 4; i++) {
            assertEquals(cs.topDeckCard(), shuffledCards.get(i+5));
            cs = cs.withoutTopDeckCard();
        }

        assertThrows(IllegalArgumentException.class, cs::withoutTopDeckCard);
    }

    @Test
    void withDeckRecreatedFromDiscards() {
        assertThrows(IllegalArgumentException.class, () -> cardState.withDeckRecreatedFromDiscards(new Random(1)));

        CardState newCardState = generateCardState(Card.ALL.subList(0,5)).withMoreDiscardedCards(SortedBag.of(Card.ALL));
        assertDoesNotThrow(() -> newCardState.withDeckRecreatedFromDiscards(new Random(1)));
    }

}
