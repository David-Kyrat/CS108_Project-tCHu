package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.Route.Level.*;

/**
 * Path linking two stations
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class Route {

    private final String id;

    private final Station station1, station2;

    private final int length;

    private final Level level;

    private final Color color;

    /**
     * Enum representing the two levels at which a road can be situated
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND;
    }

    /**
     * Unique road constructor
     * @param id arbitrary string which uniquely identifies the route
     * @param station1 Departure's station of the road
     * @param station2 Arrival's station of the road
     * @param length Length of the road
     * @param level Determine if the road is over or underground
     * @param color Determine the color of the road
     * @throws IllegalArgumentException if the two stations are the same or if the length is not within acceptable limits of a road of the game
     * @throws NullPointerException     if the {@param id}, {@param station1} or {@param station2}, or the level is null
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

        Preconditions.checkArgument(!(station1.equals(station2)) &&
                                    length >= MIN_ROUTE_LENGTH &&
                                    length <= MAX_ROUTE_LENGTH);

        this.id = Objects.requireNonNull(id);

        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);

        this.length = length;

        this.level = Objects.requireNonNull(level);

        this.color = color;
    }

    /**
     * Gives the other station of the road that the one specified
     * @param station one of the two station of the road
     * @return the other station that the one specified
     *
     * @throws IllegalArgumentException if the {@param station} is neither the {@code station1} nor the {@code station2} of the route
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));
        return station.equals(station1) ? station2 : station1;
    }

    /**
     * Gives a list containing all the sets of cards that could be played to (try to) take over the road
     * @return the list (immutable) of all the combinations of cards that could be played to attempt to take over a Route,
     *         sorted in ascending order of colors and then by the number of locomotive
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        return level == OVERGROUND ? overgroundPossibilities() : undergroundPossibilities();
    }

    private List<SortedBag<Card>> overgroundPossibilities() {
        return color == null ? CARS.stream()
                                        .map(car -> SortedBag.of(length, car))
                                        .collect(Collectors.toUnmodifiableList())

                             : List.of(SortedBag.of(length, Card.of(color)));
    }

    private List<SortedBag<Card>> undergroundPossibilities() {
        if (color == null) {
            List<SortedBag<Card>> list = new ArrayList<>();
            for (int i = 0; i < length; ++i) {
                for (Card car : CARS) list.add(SortedBag.of(length - i, car, i, LOCOMOTIVE));
            }
            list.add(SortedBag.of(length, LOCOMOTIVE));
            return List.copyOf(list);
        }

        else return IntStream.range(0, length + 1)
                             .mapToObj(i -> SortedBag.of(length - i, Card.of(color), i, LOCOMOTIVE))
                             .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the number of additional cards to play to take over a wanted underground road
     * @param claimCards initially cards played by the player
     * @param drawnCards the three cards drawn from the deck
     * @return the number of additional cards to play to win the road
     *
     * @throws IllegalArgumentException if the road to which it is applied is not underground, or if drawnCards does not contain exactly 3 cards
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(level.equals(UNDERGROUND) &&
                                    drawnCards.size() == ADDITIONAL_TUNNEL_CARDS);

        int count = 0;
        for (Card c : drawnCards) if (claimCards.contains(c) || c == LOCOMOTIVE) ++count;
        return count;
    }

    /**
     * Getter of the identification number of the road
     * @return the field {@code id} of this road
     */
    public String id() {
        return id;
    }

    /**
     * Getter of the departure station of the road
     * @return the field {@code station1} of this road
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter of the arrival station of the road
     * @return the field {@code station2} of this road
     */
    public Station station2() {
        return station2;
    }

    /**
     * Getter of the road length
     * @return the field {@code length} of this road
     */
    public int length() {
        return length;
    }

    /**
     * Getter of the road level
     * @return the field {@code level} of this road
     */
    public Level level() {
        return level;
    }

    /**
     * Getter of the road color
     * @return the field {@code color} of this road
     */
    public Color color() {
        return color;
    }



    /**
     * Gives a list with the two stations of the road (with the departure station placed before the arrival station in it)
     * @return A list of the two stations of road, in the order in which they were handed over to the constructor
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Gives the point value of a road according to its length
     * @return the number of points obtained in respect to the length of the road
     */
    public int claimPoints() {
        return ROUTE_CLAIM_POINTS.get(length);
    }
}

