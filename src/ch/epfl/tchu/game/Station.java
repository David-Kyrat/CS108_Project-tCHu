package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Represent a station of the map
 * @author Noah Munz (310779)
 */
public final class Station {

    private final int id;
    private final String name;

    /**
     * Station constructor
     * @param id identification number (Strictly superior than 0)
     * @param name name of the station
     * @throws IllegalArgumentException if {@param id} is strictly lower than 0
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0 && name != null);
        this.id = id;
        this.name = name;
    }

    /**
     * Getter of the identification number of the station
     * @return the field {@code id} of this Station
     */
    public int id() {
        return id;
    }

    /**
     * Tells the name of the station
     * @return a String of the station's name
     */
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name();
    }
}
