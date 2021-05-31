package ch.epfl.tchu.game;

import java.util.List;

/**
 * This enum represents the identity of a player
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;

    public final static List<PlayerId> ALL = List.of(values());
    public final static int COUNT = ALL.size();

    /**
     * Gives the identity of the other player
     *
     * @return PLAYER_1 if it applied to PLAYER_2 and vice-versa
     */
    public PlayerId next() {
        return equals(PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }
}
