package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Ticket of a list of trips from a commun place of departure
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final Station departure;
    private final String text;

    /**
     * Principal constructor for Ticket
     * @param trips list of available trip
     * @throws IllegalArgumentException if trips is empty or if all the stations of departure of the trips do not have the same name
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());
        this.departure = trips.get(0).from();
        //if trip.get(0).from() is = to each other trips.get(i).from() for all i then they are all mutually equal to each other
        //without having to check for other "initial value" than 0
        trips.forEach(trip -> Preconditions.checkArgument(trip.from().name().equals(departure.name())));
        this.trips = List.copyOf(trips);
        this.text = computeText();
    }

    /**
     * Secondary Ticket Constructor (uses the primary to made up a ticket of a single trip)
     * @param from Departure's Station
     * @param to Arrival's Station
     * @param points how many points is this trip worth ?
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }


    /**
     * Assuming all element of {@code trips} are correct i.e. all trips have the same departure point
     * computes in a pretty way the textual representation of a Ticket. <br/>
     * E.g. for a Town to Country ticket its representation will be like : <br/>
     * Bern - {Allemagne (6), Autriche (11), France (5), Italie (8)}
     * @return textual representation of {@code this}
     */
    private String computeText() {
        final boolean isTownOr1Trip = trips.size() < 2;
        // indicate if the trip is a Town to Town Ticket or contains only 1 trip (i.e. whether text needs brackets or not)
        final UnaryOperator<String> finalFormatter = destinationsAsString ->
                departure.name() + (isTownOr1Trip ? " - " + destinationsAsString
                                                  : " - {" + destinationsAsString + "}");

        final String result = trips.stream()
                                   .map((trip -> trip.to() + " (" + trip.points() + ")"))
                                   .distinct()
                                   .sorted()
                                   .collect(Collectors.joining(", "));
        //if the departure is the same then there is no 2 same to() (i.e. arrival) with different points,
        // hence why we could directly map each trip.to() with each value in points without it interfering with the distinct() method
        return finalFormatter.apply(result);
    }

    /**
     * Just returns the maximum in points of each trip using the trip.points method
     * (which returns a negative int if the connection has not been made)
     * because the player only lose points in case no connectivity whatsoever was established
     * hence all points are negative and since the player should loose the minimum of points it is
     * in fact the maximum of those negative points (i.e. the infimum) that we want.
     * If the player has successfully made any connectivity he/she gains the maximum of what he has connected
     * (maximum of positive ints this time, we still need in fact only the maximum.
     * The non connected are < 0 so we don't have to care about them in that case)
     * @param connectivity connectivity of the player possessing the ticket
     * @return value in points of this ticket knowing that the given connectivity
     *         is that of the player possessing the ticket
     */
    public int points(StationConnectivity connectivity) {
        return trips.stream()
                    .mapToInt(trip -> trip.points(connectivity))
                    .max()
                    .getAsInt();
    }

    /**
     * Getter of the textual representation of the ticket
     * @return the field {@code text} of this Ticket
     */
    public String text() {
        return text;
    }

    /**
     * Uses the string comparator to compare the textual representation of {@code this} to {@code that}
     * @param that ticket to be compared
     * @return comparison of both textual representation
     */
    @Override
    public int compareTo(Ticket that) {
        return text.compareTo(that.text());
    }

    @Override
    public String toString() {
        return text();
    }
}