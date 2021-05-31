package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents a flattened partition of stations in a player's road network
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class StationPartition implements StationConnectivity {

    private final List<Integer> listOfReps;

    private StationPartition(List<Integer> listOfLinks) {
        listOfReps = List.copyOf(listOfLinks);
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        return (Math.max(s1.id(), s2.id()) >= listOfReps.size()) ? s1.id() == s2.id()
                                                                 : listOfReps.get(s1.id()).equals(listOfReps.get(s2.id()));
    }

    /**
     * StationPartition Builder (constructs a deep partition of stations in a player's road network)
     * @author Mehdi Bouguerra Ezzina (314857)
     */
    public static final class Builder {

        private final List<Integer> listOfReps;

        /**
         * Constructor of a StationPartition builder
         * @param stationCount the number of station of a network
         * @throws IllegalArgumentException if stationCount if strictly lower than 0
         */
        public Builder(int stationCount) {

            Preconditions.checkArgument(stationCount >= 0);
            listOfReps = IntStream.range(0, stationCount)
                                  .boxed()
                                  .collect(Collectors.toList());
        }

        /**
         * Joins the subsets containing the two stations passed in argument, choosing the representative of the first station
         * as representative of the joined subset
         * @param s1 the station from which we keep the representative
         * @param s2 the second station we want to connect
         * @return the builder
         */
        public Builder connect(Station s1, Station s2) {
            listOfReps.set(representative(s2.id()), representative(s1.id()));
            return this;
        }

        /**
         * Returns the flattened partition of the stations corresponding to the deep partition of this builder
         * @return a StationPartition of the stations corresponding to the deep partition of this builder
         */
        public StationPartition build() {
            for (int i = 0; i < listOfReps.size(); ++i) {
                listOfReps.set(i, representative(i));
            }
            return new StationPartition(listOfReps);
        }

        private int representative(int id) {
            return listOfReps.get(id) == id ? id : representative(listOfReps.get(id));
        }
    }
}
