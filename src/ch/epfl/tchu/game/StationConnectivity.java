package ch.epfl.tchu.game;

/**
 * Represents the "connectivity" in a player's network
 *
 * @author Noah Munz (310779)
 */
@FunctionalInterface
public interface StationConnectivity {

    /**
     * @param s1 First given station to check connection with the 2nd one
     * @param s2 Second given station to check connection with the 1st one
     * @return true if and only if both station are connected by the player's railway
     */
    boolean connected(Station s1, Station s2);

}
