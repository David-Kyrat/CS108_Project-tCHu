package ch.epfl.tchu.game;

import ch.epfl.tchu.*;

import java.util.*;
import java.util.stream.*;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.Constants.*;

/**
 * Immutable representation of the complete state of a Player
 * @author Noah Munz (310779)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final Map<Ticket, Integer> ticketsValue;

    /**
     * Unique constructor of instances of the Class PlayerState
     * i.e. immutable representation of the complete state of a Player
     * @param tickets SortedBag of Tickets owned by the player at current state
     * @param cards   SortedBag of cards owned by the player at current state
     * @param routes  list of routes owned by the player at current state
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
        this.ticketsValue = new HashMap<>();
    }

    /**
     * Return the initial State of a player to whom the initial cards have been distributed.
     * NB: the player doesn't own any ticket nor any routes
     * @param initialCards initial sorted bag of cards
     * @return the initial state of a player
     *
     * @throws IllegalArgumentException if player doesn't have enough cars
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * return a state almost identical to {@code this} (instance on which this method is called, i.e. receptor).
     * Only difference : the player now possess the given Card
     * @param card Card to add to the playerState
     * @return new state of {@code this} with the given card added to it
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }

    /**
     * Return true if and only if the player can take possession of the given route.
     * i.e. if the player has enough cars left and if he owns the necessary cards.
     * @param route Route to claim
     * @return true whether the player can take possession of the given route
     */
    public boolean canClaimRoute(Route route) {
        return hasEnoughCarsToClaim(route) && possibleClaimCards(route).stream()
                                                                       .anyMatch(cards::contains);
    }

    /**
     * Return the list of all combinations (SortedBag) of Cards that the player can use to take possession of the given route
     * i.e. intersection of all that are possible and the ones that match the condition imposed by the player state
     * @param route route to claim
     * @return list of SortedBag of Card that the player can use to take possession of the given route
     *
     * @throws IllegalArgumentException if player doesn't have enough cars
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(hasEnoughCarsToClaim(route));

        final List<SortedBag<Card>> allCombinations = new ArrayList<>(route.possibleClaimCards());
        allCombinations.removeIf(sortedBag -> !cards.contains(sortedBag));
        //removes all the sortedBag that have cards that the player does not possess. i.e. not included in cards

        return List.copyOf(allCombinations);
    }

    /**
     * Returns the list of all combinations (SortedBag) of Cards that the player can use to take possession of the tunnel
     * sorted in ascending order of amount of locomotive. All while knowing that the player has initially played the initialCards
     * and that he's force to play {@code additionalCardsCount} extra cards
     * @param additionalCardsCount number of extra cards the player needs to play in order to claim the tunnel
     * @param initialCards         cards that were initially played to claim the tunnel
     * @return list of all SortedBag of cards that the player can use to take possession of the tunnel
     *
     * @throws IllegalArgumentException if additionalCardsCount is not in [1,3] or if initialCards is empty or
     *                                  has more than 2 types of card different
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards) {
        //Preconditions' check
        Preconditions.checkArgument(1 <= additionalCardsCount &&
                                    additionalCardsCount <= 3);
        Preconditions.checkArgument(!initialCards.isEmpty() &&
                                    initialCards.stream().map(Card::color).distinct().count() <= 2);

        //Actual computation of what we want
        List<Card> typesOfCardToAdd = cards.difference(initialCards).stream()
                                           .filter(card -> initialCards.contains(card) || card == LOCOMOTIVE)
                                           .collect(Collectors.toUnmodifiableList());

        SortedBag<Card> cardSb = SortedBag.of(typesOfCardToAdd);

        //we must have cardSb.size >= additionalCardsCount
        if (cardSb.size() < additionalCardsCount) return List.of();

        Set<SortedBag<Card>> setOfOptions = cardSb.subsetsOfSize(additionalCardsCount);
        return setOfOptions.stream()
                           .sorted(Comparator.comparingInt(cs -> cs.countOf(LOCOMOTIVE)))
                           .collect(Collectors.toUnmodifiableList());
    }

    /**
     * return a state almost identical to {@code this} (instance on which this method is called, i.e. receptor).
     * Only difference : the player took possession of the given Route with Cards
     * i.e. removes the claimCards to the previous state and add the route
     * @param route      Route that the player claimed
     * @param claimCards cards the the player used to claim the given route
     * @return the new updated state
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> newList = new ArrayList<>(routes());
        newList.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), newList);
    }

    /**
     * return a state almost identical to {@code this} (instance on which this method is called, i.e. receptor).
     * Only difference : the player now has the given tickets in addition to what it already had
     * @param newTickets tickets to add
     * @return the new updated state
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    /**
     * Getter for the SortedBag of tickets
     * @return SortedBag of ticket
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * Getter for the SortedBag of cards i.e. the hand of the player
     * @return SortedBag of cars/locomotives of the player
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * returns the number of points (may be negative) obtained by the player thanks to its ticket
     * @return the number of points (may be negative) obtained by the player thanks to its ticket
     */
    public int ticketPoints() {
        int stationCount = routes().isEmpty() ? 1 : routes().stream()
                                                            .mapToInt(r -> Math.max(r.station1().id(), r.station2().id()))
                                                            .max()
                                                            .getAsInt() + 1;

        StationPartition.Builder builder = new StationPartition.Builder(stationCount);
        for (Route r : routes()) builder.connect(r.station1(), r.station2());

        StationPartition partition = builder.build();

        return tickets.stream().mapToInt(ticket -> {
            int value = ticket.points(partition);
            ticketsValue.put(ticket, value);
            return value;
        }).sum();
    }

    /**
     * Construct StationPartition etc... calling ticketPoints() and returns
     * the map containing each ticket associated with its value in point
     * @return the map containing each ticket associated with its value in point
     */
    public Map<Ticket, Integer> ticketsValue() {
        ticketPoints();
        return Map.copyOf(ticketsValue);
    }

    /**
     * returns the number of points obtained by the player at the end of the game
     * @return sum of claimPoints and ticketPoints stored in the field {@code finalPoints}
     */
    public int finalPoints() {
        return ticketPoints() + claimPoints();
    }

    /**
     * @param route Route to claim
     * @return true whether the player has at least enough cars to claim the given route
     */
    private boolean hasEnoughCarsToClaim(Route route) {
        return carCount() >= route.length();
    }

}
