package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * Immutable representation of the public part of the state of a Player, i.e. routes, number of ticket and number of card
 *
 * @author Noah Munz (310779)
 */
public class PublicPlayerState {

    private final List<Route> routes;
    private final int ticketCount, cardCount, carCount, claimPoints;

    /**
     * Unique constructor of PublicPlayerState
     * @param ticketCount number of ticket a player has at current state
     * @param cardCount number of cards that a player has at current state
     * @param routes list of routes owned by the player at current state
     * @throws IllegalArgumentException if the number of tickets or the number of cards is strictly negative
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        this.routes = List.copyOf(routes);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.carCount = Constants.INITIAL_CAR_COUNT - routes.stream().mapToInt(Route::length).sum();
        this.claimPoints = routes.stream().mapToInt(Route::claimPoints).sum();
    }
    /**
     * Getter for the number of cards owned by the player at current state
     * @return number of cards
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Getter for the number of ticket owned by the player at current state
     * @return number of ticket
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * Getter for the list of routes owned by the player at current state
     * @return number of cards
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Getter for the number of cars a Player owned by the player at current state
     * @return the number of cars a Player has
     */
    public int carCount() {
        return carCount;
    }

    /**
     * Getter for the number of points of construction obtained by the player at current state,
     * i.e. sum of the points of each route
     * @return number of points of construction obtained by the player
     */
    public int claimPoints() {
        return claimPoints;
    }
}
