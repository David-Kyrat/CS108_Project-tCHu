package ch.epfl.tchu;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.gui.CardBagStringConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardBagStringConverterTest {
    @Test
    public void checkConversion() {
        CardBagStringConverter c = new CardBagStringConverter();
        SortedBag<Card> b = SortedBag.of(1, Card.VIOLET, 3, Card.RED);
        assertEquals("1 violette et 3 rouges", c.toString(b));
    }
}
