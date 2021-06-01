package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.PlayerId.*;

/**
 * Represent a game of tCHu
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class Game {

    private static Map<PlayerId, Player> players;
    private static Map<PlayerId, Info> infos;
    private static GameState gameState;

    private static Player currentPlayer;
    private static PlayerState currentPlayerState;

    /**
     * private constructor to remove the default one and make Game not instantiable
     */
    private Game() {
        throw new UnsupportedOperationException();
    }

    /**
     * makes the given players play a game of tCHu
     * @param playersMap  Map containing the two players, associated to their IDs
     * @param playerNames Map containing the name of the two players, associated to their IDs
     * @param tickets     SortedBag of the tickets for this game
     * @param rng         Random generator used for the game
     */
    public static void play(Map<PlayerId, Player> playersMap, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        boolean rematch = false;
        Random random = rng;
        do {
            //initialize Game
            initGame(playersMap, playerNames, tickets, random, rematch);

            //handle mid-Game actions :
            boolean isGameFinished = false, isLastTurnTriggered = false;

            while (!isGameFinished) {
                isGameFinished = isLastTurnTriggered;
                isLastTurnTriggered = playATurn(rng) || playATurn(rng);
                //play player1's turn and then player2's turn and register if any of it triggers the last turn
            }

            //handle endGame Actions:
            endGame(playerNames);

            //ask for a rematch
            playersMap.forEach((id, player) -> player.askForRematch());

            rematch = false;
            if(playersMap.get(PLAYER_1).rematchResponse() && playersMap.get(PLAYER_2).rematchResponse()) {
                rematch = true;
                random = new Random();
            }

        } while (rematch);
    }

    /*
    =================================================
    Method concerning the initialisation of the game
    =================================================
     */

    /**
     * "Constructs" an initial state of the Game
     * @param playersMap  map of couples [PlayerId, Player]
     * @param playerNames map of couples [PlayerId, PlayerName (as a String)]
     * @param tickets     SortedBag of tickets
     * @param rng         Random generator
     */
    private static void initGame(Map<PlayerId, Player> playersMap, Map<PlayerId, String> playerNames,
                                 SortedBag<Ticket> tickets, Random rng, Boolean rematch) {

        //check that the 2 maps rightfully have 2 pairs key/value
        Preconditions.checkArgument(playersMap.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);

        //communicate the ids to both players
        playersMap.forEach((playerId, player) -> player.initPlayers(playerId, playerNames, rematch));

        //create the initial state of the game
        gameState = GameState.initial(tickets, rng);
        players = playersMap;
        infos = playerNames.entrySet()
                           .stream()
                           .collect(Collectors.toMap((Map.Entry::getKey), (set -> new Info(set.getValue()))));

        //communicate the id of the player that will player 1st
        informAll(infos.get(gameState.currentPlayerId()).willPlayFirst());

        currentPlayer = players.get(gameState.currentPlayerId());
        currentPlayerState = gameState.currentPlayerState();

        //communicate to both players the tickets they initially drawn et takes them out of the tickets deck
        players.forEach((id, player) -> {
            player.setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT);
        });

        //make each player choose their initial tickets
        int keptTickets1 = selectInitialTickets(PLAYER_1, players.get(PLAYER_1));
        int keptTickets2 = selectInitialTickets(PLAYER_2, players.get(PLAYER_2));

        //communicate the number of tickets kept the player
        informAll(infos.get(PLAYER_1).keptTickets(keptTickets1));
        informAll(infos.get(PLAYER_2).keptTickets(keptTickets2));
    }

    private static int selectInitialTickets(PlayerId id, Player player) {
        //adds the tickets he has chosen to his hand
        informAll(infos.get(id).drewTickets(INITIAL_TICKETS_COUNT));
        updateAllStates(gameState);
        SortedBag<Ticket> keptTickets = player.chooseInitialTickets();
        gameState = gameState.withInitiallyChosenTickets(id, keptTickets);

        return keptTickets.size();
    }

    /*
    ===============================================
    Group of methods concerning the end of the game
    ===============================================
     */

    private static void endGame(Map<PlayerId, String> playerNames) {
        updateAllStates(gameState);

        int finalPoints1 = gameState.playerState(PLAYER_1).finalPoints();
        Trail longest1 = Trail.longest(gameState.playerState(PLAYER_1).routes());

        int finalPoints2 = gameState.playerState(PLAYER_2).finalPoints();
        Trail longest2 = Trail.longest(gameState.playerState(PLAYER_2).routes());

        //attribute the bonus of the longest trail if one of the two players has it
        if (longest1.length() > longest2.length()) {
            informAll(infos.get(PLAYER_1).getsLongestTrailBonus(longest1));
            finalPoints1 += LONGEST_TRAIL_BONUS_POINTS;
        }
        else if (longest2.length() > longest1.length()) {
            informAll(infos.get(PLAYER_2).getsLongestTrailBonus(longest2));
            finalPoints2 += LONGEST_TRAIL_BONUS_POINTS;
        }
        else {
            informAll(infos.get(PLAYER_1).getsLongestTrailBonus(longest1));
            finalPoints1 += LONGEST_TRAIL_BONUS_POINTS;

            informAll(infos.get(PLAYER_2).getsLongestTrailBonus(longest2));
            finalPoints2 += LONGEST_TRAIL_BONUS_POINTS;
        }

        //announce the winner of the game or a draw
        winnerOrDraw(finalPoints1, finalPoints2, playerNames);
    }

    private static void winnerOrDraw(int finalPoints1, int finalPoints2, Map<PlayerId, String> playerNames) {
        if (finalPoints1 > finalPoints2) informAll(infos.get(PLAYER_1).won(finalPoints1, finalPoints2));

        else if (finalPoints2 > finalPoints1) informAll(infos.get(PLAYER_2).won(finalPoints2, finalPoints1));
        else informAll(
                    Info.draw(List.copyOf(playerNames.values()),
                              finalPoints1));
    }

    /*
    ====================================================
    Group of methods concerning the progress of the game
    ====================================================
     */

    /**
     * Make a turn unfold by asking the current player what does he wants to do, then do it, then end the turn and
     * begin a new one.
     * @param rng Random
     * @return whether or not last Turn begins
     */
    private static boolean playATurn(Random rng) {
        informAll(info().canPlay());

        //ask the currentPlayer what does he want to do and based on it, play his turn
        chooseWhatToDo(rng);

        boolean lastTurnBegins = gameState.lastTurnBegins();
        if (lastTurnBegins) {
            currentPlayerState = gameState.currentPlayerState();
            informAll(info().lastTurnBegins(currentPlayerState.carCount()));
        }

        //triggers next turn and communicate infos
        newTurn();
        return lastTurnBegins;
    }

    /**
     * Based on the action that the current Player chose => proceed to 1 out of 3 "Actions"
     * @param rng Random
     */
    private static void chooseWhatToDo(Random rng) {
        updateAllStates(gameState);
        switch (currentPlayer.nextTurn()) {
            case DRAW_TICKETS:
                drawTicketsAction();
                break;

            case DRAW_CARDS:
                drawCardsAction(rng);
                break;

            case CLAIM_ROUTE:
                claimRouteAction(rng);
                break;
        }
    }

    /**
     * Ends turn, inform players about the current state of the game and tells the next player that he can play
     */
    private static void newTurn() {
        gameState = gameState.forNextTurn();
        updateAllStates(gameState);
        currentPlayerState = gameState.currentPlayerState();
        currentPlayer = players.get(gameState.currentPlayerId());
    }

    /**
     * Action corresponding to the TurnKind Draw Tickets
     * Handles the scenario where a player wants to draw tickets
     */
    private static void drawTicketsAction() {
        SortedBag<Ticket> ticketsDrawn = gameState.topTickets(IN_GAME_TICKETS_COUNT);
        SortedBag<Ticket> ticketsKept = currentPlayer.chooseTickets(ticketsDrawn);

        gameState = gameState.withChosenAdditionalTickets(ticketsDrawn, ticketsKept);
        informAll(info().drewTickets(IN_GAME_TICKETS_COUNT));
        informAll(info().keptTickets(ticketsKept.size()));
    }

    /**
     * Action corresponding to the TurnKind Draw Cards
     * Handles the scenario where a player wants to draw cards be it from the face up cards or from the draw deck
     * @param rng Random
     */
    private static void drawCardsAction(Random rng) {
        for (int i = 0; i < 2; i++) {
            int slot = currentPlayer.drawSlot();
            if (slot == DECK_SLOT) drawDeck(rng);
            else drawSlot(rng, slot);
        }
    }

    private static void drawDeck(Random rng) {
        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng).withBlindlyDrawnCard();
        updateAllStates(gameState);
        informAll(info().drewBlindCard());
    }

    private static void drawSlot(Random rng, int slot) {
        informAll(info().drewVisibleCard(gameState.cardState().faceUpCard(slot)));
        gameState = checkAndDrawSlot(rng, slot);
        updateAllStates(gameState);
    }

    /**
     * Same as <code>checkAndDrawCar</code> but for drawing a face up card from one of the slot
     * ==> potential recreation of deck needed when the drawn face up card is replaced with a card from the draw deck
     * @param rng  random
     * @param slot position/slot 0 to 4) to take the face up card from. <br/>
     *             !! does not check any condition on slot
     * @return new GameState with the draw deck potentially refilled, and
     */
    private static GameState checkAndDrawSlot(Random rng, int slot) {
        return gameState.withCardsDeckRecreatedIfNeeded(rng).withDrawnFaceUpCard(slot);
    }

    /**
     * Action corresponding to the TurnKind Claim Route
     * Handles the scenario where a player wants to take over a Route be it a tunnel or not
     * @param rng Random (for drawing additional cards when attempting to take over a tunnel)
     */
    private static void claimRouteAction(Random rng) {
        Route claimedRoute = currentPlayer.claimedRoute();

        SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();

        switch (claimedRoute.level()) {
            case OVERGROUND: //if card is not a tunnel => just take the route
                claimRouteAndInform(claimedRoute, initialClaimCards);
                break;
            case UNDERGROUND: // if it is => goes through the process of attempting the claim
                attemptTunnelClaim(claimedRoute, initialClaimCards, rng);
                break;
        }
                /* used a switch here for 2 reasons:
                    1. Its makes the code more readable and more clear even there's only 2 cases
                    2. In a more general way since we are using an enum we may want to add more cases
                    (even though this enum contains only 2 elements, it's definitely not the case in general i.e. for any enum)
                 */
    }

    /**
     * Attempt to claim a tunnel. See the documentation of the methods called within this one
     */
    private static void attemptTunnelClaim(Route claimedTunnel, SortedBag<Card> initialClaimCards, Random rng) {
        informAll(info().attemptsTunnelClaim(claimedTunnel, initialClaimCards));

        //draw the 3 necessary cards
        SortedBag<Card> drawnCards = drawAdditionalCards(rng);
        int additionalCount = claimedTunnel.additionalClaimCardsCount(initialClaimCards, drawnCards);
        informAll(info().drewAdditionalCards(drawnCards, additionalCount));

        //puts the cards drawn in discard
        gameState = gameState.withMoreDiscardedCards(drawnCards);
        handleAdditionalCost(additionalCount, claimedTunnel, initialClaimCards);
    }

    /**
     * Handles the part player has to add additional cards in order to take the tunnel, or back down from claiming it
     */
    private static void handleAdditionalCost(int additionalCount, Route claimedTunnel, SortedBag<Card> initialClaimCards) {
        //if they imply an additional cost of at least 1
        if (additionalCount > 0) {
            List<SortedBag<Card>> options = currentPlayerState.possibleAdditionalCards(additionalCount, initialClaimCards);

            //we call chooseAdditionalCards only when at least 1 sortedBag in opt is not empty in options
            if (!options.isEmpty()) chooseAdditionalCards(options, claimedTunnel,
                                                          initialClaimCards);
            else informAll(info().didNotClaimRoute(claimedTunnel));
            /*parallel stream over list "options", in second if, was tested and found out to be approximately 2 to 3 faster (25 -> 10 ms)
             than regular stream when no sortedBag was found to be empty, and as fast when at least one is.*/
        }
        //If no additional card is required ==> just take the route
        else claimRouteAndInform(claimedTunnel, initialClaimCards);
    }

    /**
     * Handles part where player has to choose the additional cards he wants to use in order to claim (or not) the tunnel.
     */
    private static void chooseAdditionalCards(List<SortedBag<Card>> options, Route claimedTunnel, SortedBag<Card> initialClaimCards) {
        SortedBag<Card> finalAdditionalCardsUsed = currentPlayer.chooseAdditionalCards(options);

        if (finalAdditionalCardsUsed.isEmpty()) informAll(info().didNotClaimRoute(claimedTunnel));
        else {
            SortedBag<Card> cardsUsedInTotal = initialClaimCards.union(finalAdditionalCardsUsed);
            claimRouteAndInform(claimedTunnel, cardsUsedInTotal);
        }
    }

    /**
     * Draws the 3 additional Cards when attempting to claim a tunnel and update the <code>currentGameState</code>
     * @param rng random
     * @return the sortedBag of the 3 cards
     */
    private static SortedBag<Card> drawAdditionalCards(Random rng) {
        SortedBag.Builder<Card> sbBld = new SortedBag.Builder<>();
        for (int i = 0; i < ADDITIONAL_TUNNEL_CARDS; i++) {
            sbBld.add(gameState.withCardsDeckRecreatedIfNeeded(rng).topCard());
            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng).withoutTopCard();
        }
        return sbBld.build();
    }

    private static void claimRouteAndInform(Route route, SortedBag<Card> claimCards) {
        informAll(info().claimedRoute(route, claimCards));
        gameState = gameState.withClaimedRoute(route, claimCards);
    }

    /*
    ===============================================
    Group of methods created for practical purposes
    ===============================================
     */

    /**
     * @return current Info i.e. infos.get(currentGameState.currentPlayerId())
     */
    private static Info info() {
        return infos.get(gameState.currentPlayerId());
    }

    /**
     * Transmits <code>info</code> to all Players
     * @param info info to transmit
     */
    private static void informAll(String info) {
        players.forEach((playerId, player) -> player.receiveInfo(info));
    }

    /**
     * Update the GameState and PlayerState of all Player
     * @param newGameState GameState to replace the old one with <br/>
     *                     (Note: PlayerState is computed from the given GameState)
     */
    private static void updateAllStates(GameState newGameState) {
        players.forEach((playerId, player) -> player.updateState(newGameState, newGameState.playerState(playerId)));
    }

}
