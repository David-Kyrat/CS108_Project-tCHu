package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represent the link between two station and his amount of point
 *
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class Trip {

    private final Station from, to;
    private final int points;

    /**
     * Primary Trip Constructor
     *
     * @param from   Departure's Station
     * @param to     Arrival's Station
     * @param points how many points is this trip worth ?
     * @throws IllegalArgumentException if {@param points} is lower or equals 0
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Gives all possible trips from one of the departure stations to one of the arrival stations, each worth the given number of points
     *
     * @param from   Departure's Station
     * @param to     Arrival's Station
     * @param points how many points are the trip worth ?
     * @return List of all possible trip from one of the station in {@code from} to one in {@code to}
     * where each is worth the given value {@code points}
     *
     * @throws IllegalArgumentException if one of the lists is empty, or if the number of points is not strictly positive
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(points > 0 &&
                !from.isEmpty() &&
                !to.isEmpty());

        final List<Trip> possibleTrips = new ArrayList<>();

        for (Station currentFrom : from) {
            for (Station currentTo : to) {
                possibleTrips.add(new Trip(currentFrom, currentTo, points));
            }
        }
        return List.copyOf(possibleTrips);
    }

    /**
     * Getter for the departure station
     *
     * @return the field {@code from} of this Trip
     */
    public Station from() {
        return from;
    }

    /**
     * Getter for the arrival station
     *
     * @return the field {@code to} of this Trip
     */
    public Station to() {
        return to;
    }

    /**
     * Getter of the number of points corresponding to the trip.
     *
     * @return the field {@code points} of this Trip
     */
    public int points() {
        return points;
    }

    /**
     * Gives the number of points of the trip for the given connectivity
     *
     * @param connectivity StationConnectivity
     * @return points if from and to are connected -points otherwise
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to) ? points : -points;
    }
}
