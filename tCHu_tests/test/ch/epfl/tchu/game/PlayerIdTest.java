package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerIdTest {
    @Test
    void playerIdValuesAreDefinedInTheRightOrder() {
        var expectedValues = new PlayerId[]{
                PlayerId.PLAYER_1,PlayerId.PLAYER_2
        };
        assertArrayEquals(expectedValues, PlayerId.values());
    }

    @Test
    void playerIdAllIsDefinedCorrectly() {
        assertEquals(List.of(PlayerId.values()), PlayerId.ALL);
    }

    @Test
    void playerIdCountIsDefinedCorrectly() {
        assertEquals(2, PlayerId.COUNT);
    }

    @Test
    void playerIdNextIsDefinedCorrectly(){
        assertEquals(PlayerId.PLAYER_1,PlayerId.PLAYER_2.next());
        assertEquals(PlayerId.PLAYER_2,PlayerId.PLAYER_1.next());
    }

}