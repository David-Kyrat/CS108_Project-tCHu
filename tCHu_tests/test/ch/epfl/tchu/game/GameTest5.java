package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.*;

public class GameTest5 {
    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;
        private PlayerId ownId;
        private String name;
        private Map<PlayerId, String> playerNames;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;
        private String info;
        private SortedBag<Ticket> initialTicketsChoice;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            this.name = playerNames.get(ownId);
            this.playerNames = playerNames;
            this.turnCounter = 0;
        }

        @Override
        public void receiveInfo(String info) {
            this.info = info;
            // if(this!=null) System.out.println(this);

        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.initialTicketsChoice = tickets;
        }

        /**
         * pick first three tickets
         *
         * @return
         */
        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            int randomInt = rng.nextInt(2);
            return SortedBag.of(initialTicketsChoice.toList().subList(randomInt, 2 + randomInt));
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = new ArrayList<>();
            for (Route route : allRoutes) {
                if (this.ownState.canClaimRoute(route)) {
                    claimableRoutes.add(route);
                }
            }
            if (claimableRoutes.isEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
            }
        }

        /**
         * always pick one ticket, the first one
         *
         * @param options
         * @return
         */
        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return SortedBag.of(options.toList().get(rng.nextInt(options.size())));
        }

        /**
         * always pick the first card
         *
         * @return
         */
        @Override
        public int drawSlot() {
            return rng.nextInt(4);
        }


        @Override
        public Route claimedRoute() {
            List<Route> possibleRoutes = new ArrayList<>();
            for (Route route : allRoutes) {
                if (ownState.canClaimRoute(route)) {
                    possibleRoutes.add(route);
                } else continue;
            }
            if (!possibleRoutes.isEmpty()) {
                return possibleRoutes.get(rng.nextInt(possibleRoutes.size()));
            } else return null;
        }


        @Override
        public SortedBag<Card> initialClaimCards() {
            List<SortedBag<Card>> possibleClaimCard = ownState.possibleClaimCards(claimedRoute());
            return possibleClaimCard.get(rng.nextInt(possibleClaimCard.size()));
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            for (SortedBag<Card> option : options) {
                if (ownState.cards().contains(option)) {
                    return option;
                } else {
                    continue;
                }
            }
            return SortedBag.of();
        }


        @Override
        public String toString() {
            // if (turnCounter >= 995) {
            if (ownState != null)
                return name + " a un \nnombre de wagons = " + ownState.carCount() + "\n" + "nombre de tours = " + turnCounter + "\n" + "nombre de routes = " + ownState.routes().size() + "\n" + info;
            /*}
            else{
                return "";
            }*/
                //return "cards remaining in deck = " +gameState.cardState().deckSize();
            else return "";
        }
    }

    @Test
    void startRandomGame() {
        var rng = new Random();
        for (int i = 0; i < 100; ++i) {
            TestPlayer player1 = new TestPlayer(rng.nextInt(i + 1), ChMap.routes());
            TestPlayer player2 = new TestPlayer(rng.nextInt(i + 1), ChMap.routes());
            Map<PlayerId, Player> players = new HashMap<>();
            players.put(PlayerId.PLAYER_1, player1);
            players.put(PlayerId.PLAYER_2, player2);
            Map<PlayerId, String> playerNames = new HashMap<>();
            playerNames.put(PlayerId.PLAYER_1, "Aberer");
            playerNames.put(PlayerId.PLAYER_2, "Lenstra");
            Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());
            System.out.println("Game number = " + (i + 1));
        }
    }

}