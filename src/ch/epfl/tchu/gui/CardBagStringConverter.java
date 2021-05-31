package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

import java.util.Set;

import static ch.epfl.tchu.gui.Info.cardName;
import static ch.epfl.tchu.gui.StringsFr.*;
import static ch.epfl.tchu.gui.ConstantsGUI.*;

/**
 * This class allows to obtain an elegant textual re-presentation of a SortedBag
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    @Override
    public String toString(SortedBag<Card> object) {
        StringBuilder builder = new StringBuilder();
        Set<Card> cardsSet = object.toSet();

        int count, index = 0;

        for (Card c : cardsSet) {
            int size = cardsSet.size();
            count = object.countOf(c);

            builder.append(count)
                   .append(SPACE_SEPARATOR)
                   .append(cardName(c, count));

            if (index == size - 2 && size > 1) builder.append(AND_SEPARATOR);
            else if (index != size - 1) builder.append(COMA_SEPARATOR);

            ++index;
        }
        return builder.toString();
    }

    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
