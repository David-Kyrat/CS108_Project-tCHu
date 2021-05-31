package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameTest2 {

    private long randSeed = 4;
    private long randSeedTwo = 2;

    private TestPlayer player = new TestPlayer(randSeed, ChMap.routes());
    private TestPlayer playerTwo = new TestPlayer(randSeedTwo, ChMap.routes());

    @Test
    void play() {
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        players.put(PlayerId.PLAYER_1, player);
        players.put(PlayerId.PLAYER_2, playerTwo);

        Map<PlayerId, String> names = new EnumMap<>(PlayerId.class);
        names.put(PlayerId.PLAYER_1, "Félix");
        names.put(PlayerId.PLAYER_2, "Kaan");

        Game.play(players, names, SortedBag.of(ChMap.tickets()), player.rng);
    }

    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private PlayerId ownId;
        private String ownName;

        private Info ownInfo;
        private Map<PlayerId, String> playerNames;
        private SortedBag<Ticket> initialTickets;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            this.ownName = playerNames.get(ownId);
            this.playerNames = playerNames;

            System.out.println("Joueurs initialisés");
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println(info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;


            System.out.println("State updated");
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.initialTickets = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {

            return initialTickets;
        }

        @Override
        public TurnKind nextTurn() {
            List<Route> claimableRoutes = new ArrayList<>();

            turnCounter += 1;
            System.out.println("Tour n°"+turnCounter+ " de "+playerNames.get(ownId));
            System.out.println(ownState.cards()+ " cartes");
            System.out.println(ownState.tickets().size() +" billets");
            System.out.println(ownState.carCount() + " wagons restants");
            System.out.println(ownState.finalPoints() +" points actuels");
            System.out.println("\n");

            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> copyRoutes = new ArrayList<>(allRoutes);
            copyRoutes.removeAll(gameState.claimedRoutes());
            if(turnCounter<2){
                return TurnKind.DRAW_TICKETS;
            }
            if(turnCounter<4){
                return TurnKind.DRAW_CARDS;
            }
            copyRoutes.forEach(
                    (route) -> {
                        boolean addable = false;
                        for(SortedBag<Card> option: route.possibleClaimCards()){
                            if(ownState.cards().contains(option)){
                                addable = true;
                            }
                        }
                        if(addable){
                            claimableRoutes.add(route);
                        }
                    });

            if (claimableRoutes.isEmpty()) {
                System.out.println("Cards drawn");
                return TurnKind.DRAW_CARDS;
            } else if(ownState.carCount() != 0){
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                System.out.println("Claim route");
                return TurnKind.CLAIM_ROUTE;
            }else{
                return TurnKind.DRAW_CARDS;
            }
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return options;
        }

        @Override
        public int drawSlot() {
            Random rand = new Random();
            return rand.nextInt(6) - 1;
        }

        @Override
        public Route claimedRoute() {
            return this.routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return this.initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(rng.nextInt(options.size()));
        }
    }
}

