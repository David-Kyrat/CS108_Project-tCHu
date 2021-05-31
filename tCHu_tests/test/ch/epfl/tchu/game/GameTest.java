package ch.epfl.tchu.game;

import ch.epfl.tchu.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static ch.epfl.tchu.game.Constants.*;

/**
 * @author Noah Munz (310779)
 */
@SuppressWarnings("SameParameterValue")
public class GameTest {

    @Test
    public void play() {
        TestPlayer.play();
    }

    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // All routes on the map
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;

        private PlayerId id;
        private SortedBag<Ticket> initialTickets;
        private List<Route> claimableRoutes;
        //   private SortedBag<Card> hand;


        private StationPartition stationPartition;

        //when nextTurn returns CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
            id = null;
        }

        /**
         * Shorthand method for <code>ownState.routes()</code>
         * @return <code>ownState.routes()</code>
         */
        public List<Route> claimedRoutes() {
            return ownState.routes();
        }

        public void updateStationPartition() {
            stationPartition = updateStationPartition(claimedRoutes());
        }

        public StationPartition updateStationPartition(List<Route> claimedRoutes) {
            int stationCount = claimedRoutes.isEmpty() ? 0 :
                               claimedRoutes.stream()
                                            .mapToInt(route -> Math.max(route.station2().id(), route.station1().id()))
                                            .max()
                                            .getAsInt() + 1;
            StationPartition.Builder spb = new StationPartition.Builder(stationCount);
            for (Route route : claimedRoutes) spb.connect(route.station1(), route.station2());
            return spb.build();
        }

        /**
         * Communicates to the player his own identity, as well as the names of the different players (including his own)
         * @param ownId the identity of the player
         * @param playerNames a Map associating the ID of a player to his name
         */
        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            id = ownId;
            String s1 = " you are ";
            String s2 = " is ";
            playerNames.forEach((playerId, name) -> receiveInfo(name + (playerId == id ? s1 : s2) + idToStr(playerId)));
        }

        public static void play() {
            Map<PlayerId, String> playerNames = new HashMap<>();
            Map<PlayerId, Player> players = new HashMap<>();
            List<String> names = List.of("Alice", "Bob");
            Random rng = new Random();
            PlayerId.ALL.forEach(id -> {
                playerNames.put(id, names.get(id.ordinal()));
                players.put(id, new TestPlayer(rng.nextInt(), ChMap.routes()));
            }); //TestPlayer plyr = new TestPlayer(1, ChMap.routes());

            Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rng);
        }

        /**
         * (call <code>updateStationPartition</code>)
         * @param ticket Ticket
         * @return whether or not the ticket will return negative points i.e. no trips on it has currently been completed
         *         <=> all "quest" currently failed. return True if at least 1 trip has been completed
         */
        public boolean isNoTripCompleted(Ticket ticket) {
            updateStationPartition();
            return ticket.points(stationPartition) > 0;
        }

        /**
         * @param routes given routes to build stationPartition from
         * @return whether or not there exist a ticket on which there exist a trip so that this trip is completable by the player
         *         in other words takes a stationPartition with all claimableRoute (i.e. as if the player has claimed every Route that he could)
         *         and checks if for that stationPartition there is ticket such that <code>ticket.points</code> is positive
         */
        public boolean isATripCompletable(List<Route> routes) {
            StationPartition potentialStationPartition = updateStationPartition(routes);
            return ownState.tickets().stream().anyMatch(ticket -> ticket.points(potentialStationPartition) > 0);
        }

        /**
         * Method called each time a piece of information must be communicated to the player during the game
         * @param info String containing the information to communicate to the player
         */
        @Override
        public void receiveInfo(String info) {
            System.out.print("To " + idToStr(id) + ": ");
            System.out.println(info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
            // this.hand = ownState.cards();
        }

        /**
         * Gives the player the five tickets that were distributed to him at the beginning of the game
         * @param tickets the five tickets that were distributed to the player at the beginning of the game
         */
        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            //ownState = ownState.withAddedTickets(tickets);
            initialTickets = tickets;
        }

        /**
         * Asks the player which ticket, among those that were initially distributed, he wants to keep
         * @return a SortedBag of the tickets the player wants to keep
         */
        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            int nbCardsKept = nextInt(IN_GAME_TICKETS_COUNT, INITIAL_TICKETS_COUNT + 1);
            List<Ticket> tickets = initialTickets.toList();
            tickets = genDistinctRandomElemList(nbCardsKept, 0, 5, (tickets::get));
            SortedBag<Ticket> ticketsSb = SortedBag.of(tickets);

            ownState = ownState.withAddedTickets(ticketsSb);
            //if remove that here the initial tickets are never added
            return ticketsSb;
        }

        private void printStates() {
            String player = idToStr(gameState.currentPlayerId()) + ": ";
            System.out.println("\n======= States of the game: ======");
            System.out.println("turn nb: " + turnCounter);
            System.out.println("Claimed Routes : " + routesToString(gameState.claimedRoutes()));
            System.out.println("Number of tickets in TicketDeck " + gameState.ticketsCount());
            System.out.println("Number of cards in cardDeck " + gameState.cardState().deckSize());
            System.out.println("Number of cards in Discard " + gameState.cardState().discardsSize());
            System.out.println(
                    "Number of cards Discard + cardDeck " + (gameState.cardState().discardsSize() + gameState.cardState().deckSize()));
            System.out.println("Last player ? " + gameState.lastPlayer());
            System.out.println("_____");
            System.out.println(idToStr(gameState.currentPlayerId()) + " can draw cards ? " + gameState.canDrawCards());
            System.out.println("nb of cards " + player + ownState.cardCount());
            System.out.println("cards " + player + ownState.cards().toList());
            System.out.println("nb of tickets " + player + ownState.ticketCount());
            System.out.println("tickets " + player + ownState.tickets().toList());
            System.out.println("ticketPoints " + player + ownState.ticketPoints());
            System.out.println("nb of routes: " + player + ownState.routes().size());
            System.out.println("owned Routes " + player + routesToString(ownState.routes()));
            System.out.println("nb cars used " + player + ownState.routes().stream().mapToInt(Route::length).sum());
            System.out.println("claimable Routes " + player + routesToString(computeClaimableRoutes()));
            System.out.println("nb of cars: " + player + ownState.carCount());
            System.out.println("length of routes : " + player + ownState.routes().stream().map(Route::length).collect(Collectors.toUnmodifiableList()));
            System.out.println("========");
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT) throw new Error("Trop de tours joués !");

            //determines the routes that this player can claim
            List<Route> claimableRoutes = computeClaimableRoutes();
            updateStationPartition();
            //printStates();
            System.out.println("Is a trip completable ? : " + isATripCompletable(claimableRoutes));

            if (!isATripCompletable(claimableRoutes) && gameState.canDrawTickets()) {
                return TurnKind.DRAW_TICKETS;
            }
            else if (claimableRoutes.isEmpty() && canDrawCard()) {
                System.out.println("\n about to Draw Card\n");
                return TurnKind.DRAW_CARDS;
            }

            else if (!claimableRoutes.isEmpty()) { //each 10 turn or 5 double turns i.e. each player played 5*k times (for a non zero integer k)
              /*  if (turnCounter % 10 == 0 && gameState.canDrawTickets() && !isATripCompletable(claimableRoutes)) {
                    return TurnKind.DRAW_TICKETS;
                }*/
                System.out.println("\n about to claim Route\n");
                Route route = chooseInList(claimableRoutes);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
            }
            throw new IllegalStateException(
                    idToStr(gameState.currentPlayerId()) + " cannot draw Cards, deck cannot be recreated and he can't claim any routes");
        }

        /**
         * Method called when a player has decided to draw additional tickets during the game, to let him know which tickets
         * were drawn and which ones he wants to keep
         * @param options a SortedBag of the drawn tickets
         * @return a SortedBag of the tickets the player wants to keep
         */
        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            int bound = IN_GAME_TICKETS_COUNT + 1;
            int nbTicketsKept = nextInt(1, bound);
            return SortedBag.of(genDistinctRandomElemList(nbTicketsKept, 0, bound-1, options::get));
        }

        /**
         * Method called when the player has decided to draw car/locomotive cards, to know where he wants to draw them from
         * @return one value from 0 to 4 (both included) if the player wants to draw a face-up card, or -1 if he want to draw
         *         a card from the draw deck
         */
        @Override
        public int drawSlot() {
            return gameState.canDrawCards() ? (nextInt(0, 6) - 1) : -1;
        }

        /**
         * Method called when the player tries to take a road, to know which road it is
         * @return the road the player tries to take
         */
        @Override
        public Route claimedRoute() {
            Route route = chooseInList(computeClaimableRoutes());
            //if cant claim a route nothing will happen => not intersting for test, so this only returns route claimable by the player
            //may produce infinite loops !
            routeToClaim = route;
            return route;
        }

        /**
         * Method called when the player tries to take a road, to know which cards he initially wants to use for this
         * @return a sortedBag with the cards the player is initially intending to use
         */
        @Override
        public SortedBag<Card> initialClaimCards() {
            List<SortedBag<Card>> list = ownState.possibleClaimCards(routeToClaim);
            initialClaimCards = chooseInList(list);
            return initialClaimCards;
        }

        /**
         * Method called when the player has decided to try to take over a tunnel and additional cards are needed,
         * to know which cards he wants to use for this
         * @param options a List of the different associations of additional cards he could use
         * @return a SortedBag with the card he has decided to use (if the SortedBag is empty it means the player does not want
         *         to use additional cards)
         */
        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            boolean willRefuse = rng.nextInt(101) <= 25; //1/4 chances the player declines the attempt to take over the route
            if (willRefuse) return SortedBag.of();
            return chooseInList(options);
        }

        private List<Route> computeClaimableRoutes() {
            return allRoutes.stream()
                            .filter(route -> !gameState.claimedRoutes().contains(route))
                            .filter(route -> ownState.canClaimRoute(route))
                            .collect(Collectors.toUnmodifiableList());
        }

        /**
         * @param list list to choose a random element from
         * @param <E> type of elements in <code>list</code>
         * @return random element of given list
         */
        private <E> E chooseInList(List<E> list) {
            int size = list.size();
            int x = size == 0 ? 0 : rng.nextInt(size);
            return list.get(x);
        }

        /**
         * Generate a list of random distinct elements of type R by generating random distinct ints with the method <code>nextDifferentInts(nbOfElem, lowerBound, upperBound)</code>
         * and mapping them (with stream) with the given mapper and then collecting the resulting stream as immutable list of object of type R
         * @param nbOfElem number of elements in the list
         * @param lowerBound lower bound of int to generate
         * @param upperBound upper bound of int to generate. i.e. ints will be generated between <code>lowerBound</code> and <code>upperBound</code>
         * @param mapper function that will map the ints to an other element of type R
         * @param <R> type of the newly created list
         */
        private <R> List<R> genDistinctRandomElemList(int nbOfElem, int lowerBound, int upperBound,
                                                      Function<? super Integer, ? extends R> mapper) {
            return nextDifferentInts(nbOfElem, lowerBound, upperBound).stream().map(mapper).collect(Collectors.toUnmodifiableList());
        }

        /**
         * @param nbOfInt number of different random integer to generate
         * @param lowerBound lower bound (inclusive)
         * @param upperBound upper bound (exclusive)
         * @return a list of <code>nbOfInt</code> different integer chosen at random between <code>lowerBound</code> and <code>upperBound</code>
         */
        private List<Integer> nextDifferentInts(int nbOfInt, int lowerBound, int upperBound) {
            List<Integer> used = new ArrayList<>();
            Supplier<Integer> diffSupplier = () -> {
                int rd = nextInt(lowerBound, upperBound);
                while (used.contains(rd)) rd = nextInt(lowerBound, upperBound);
                used.add(rd);
                return rd;
            };
            return IntStream.range(0, nbOfInt).mapToObj(x -> diffSupplier.get()).collect(Collectors.toUnmodifiableList());
        }

        /**
         * @param lowerBound lower bound (inclusive)
         * @param upperBound upper bound (exclusive)
         * @return a bounded random integer
         */
        private int nextInt(int lowerBound, int upperBound) {
            int outp = rng.nextInt(upperBound);
            while (outp < lowerBound) outp = rng.nextInt(upperBound);

            return outp;
        }

        private List<String> routesToString(List<Route> routes) {
            return routes.stream().map(this::routeToString).collect(Collectors.toUnmodifiableList());
        }

        private String routeToString(Route route) {
            return "(" + levelToString(route.level()) + ")" + route.station1() + " <=> " + route.station2();
        }

        private String levelToString(Route.Level level) {
            if (level == Route.Level.OVERGROUND) return "Regular";
            else return "Tunnel";
        }

        private boolean canDrawCard() {
            PublicCardState cardState = gameState.cardState();
            return (cardState.deckSize() + cardState.discardsSize()) >= 2;
        }

        private String idToStr(PlayerId id) {
            switch (id) {
                case PLAYER_1:
                    return "Player1";
                case PLAYER_2:
                    return "Player2";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

}
