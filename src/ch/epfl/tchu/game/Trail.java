package ch.epfl.tchu.game;

import java.util.*;
import java.util.function.Predicate;

/**
 * Sequence of station and road forming a track
 *
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class Trail {

    private final int length;
    private final Station station1, station2;
    private final List<Route> routes;

    private Trail(List<Route> routes, Station station1, Station station2, int length) {
        this.length = length;
        this.routes = routes;
        this.station1 = station1;
        this.station2 = station2;
    }

    private Trail(Trail toExtend, Route extension) {
        this(add(toExtend.routes, extension), toExtend.station1, extension.stationOpposite(toExtend.station2),
                toExtend.length + extension.length());
    }

    private static <T> List<T> add(List<T> list, T addition) {
        List<T> extended = new ArrayList<>(list);
        extended.add(addition);
        return extended;
    }

    /**
     * Gives the longest path composed of the given routes
     *
     * @param routes list of routes owned by the player
     * @return the longest path composed of the given routes. If there is more than one, returns one of them
     * (or a trail of length 0 and of stations equal to null, if the list of routes is empty)
     */
    public static Trail longest(List<Route> routes) {
        if (routes == null) return null;

        if (routes.isEmpty()) return new Trail(List.of(), null, null, 0);

        Collection<Trail> initialTrails = new HashSet<>();
        for (Route r : routes) {
            initialTrails.add(new Trail(List.of(r), r.station1(), r.station2(), r.length()));
            initialTrails.add(new Trail(List.of(r), r.station2(), r.station1(), r.length()));
        }

        return longestRec(initialTrails, routes, new Trail(List.of(), null, null, 0));
    }

    /**
     * Recursive call for longest() only stops when pointsSet is empty
     *
     * @param trails Collection of trails, initialized in longest
     * @param routes extensions to check & add to the given trails
     */
    private static Trail longestRec(Collection<Trail> trails, List<Route> routes, Trail currentLongest) {

        Collection<Trail> pointsSet = new HashSet<>();
        Trail nextLongest = currentLongest;

        for (Trail currentTrail : trails) {

            Predicate<Route> isExtendable = route ->
                    !currentTrail.routes.contains(route) && route.stations().contains(currentTrail.station2);

            routes.forEach(route -> {
                if (isExtendable.test(route)) pointsSet.add(new Trail(currentTrail, route));
            });

            if (currentTrail.length > nextLongest.length) {
                nextLongest = currentTrail;
            }

        }
        return pointsSet.isEmpty() ? nextLongest
                : longestRec(pointsSet, routes, nextLongest);
    }


    /**
     * Getter of the trail length
     *
     * @return the length of the trail
     */
    public int length() {
        return length;
    }

    /**
     * Getter of the departure station of the trail
     *
     * @return the departure station of the trail or null if its length is 0
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter of the arrival station of the road
     *
     * @return the arrival station of the road or null if its length is 0
     */
    public Station station2() {
        return station2;
    }

    /**
     * Returns a textual representation of the road containing the names of the first and the last station of the trail and it's length
     *
     * @return a String of the road containing the names of <codes>station1</codes> and <codes>station2</codes> of the trail and it's <codes>length</codes>
     */
    @Override
    public String toString() {
        if (routes.isEmpty()) return "";

        List<String> output = new ArrayList<>();
        Station currentStation = station1;

        for (Route r : routes) {
            output.add(currentStation.toString());
            currentStation = r.stationOpposite(currentStation);
        }
        output.add(station2.toString());
        return String.format("%s (%s)", String.join(" - ", output), length);
    }
}
