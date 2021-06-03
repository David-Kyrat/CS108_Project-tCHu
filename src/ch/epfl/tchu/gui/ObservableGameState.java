package ch.epfl.tchu.gui;

import ch.epfl.tchu.*;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.*;

import java.util.*;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.PlayerId.*;

/**
 * Represents the observable state of a tCHu game
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 * @author Noah Munz (310779)
 */
public final class ObservableGameState {

    private final PlayerId playerId;
    private PublicGameState gameState;
    private PlayerState playerState;

    /*
    ==================================================
    Properties concerning the public state of the game
    ==================================================
     */
    private final IntegerProperty ticketsPercentage = new SimpleIntegerProperty();
    private final IntegerProperty cardsPercentage = new SimpleIntegerProperty();

    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
    private final Map<Route, ObjectProperty<PlayerId>> routeOwner = new HashMap<>();

    /*
    =====================================================
    Properties concerning the public state of the players
    =====================================================
     */
    private final List<IntegerProperty> ticketCounts = new ArrayList<>();
    private final List<IntegerProperty> cardCounts = new ArrayList<>();
    private final List<IntegerProperty> carCounts = new ArrayList<>();
    private final List<IntegerProperty> claimPoints = new ArrayList<>();

    /*
    ==========================================================================================================
    Properties concerning the hidden state of the player corresponding to this instance of ObservableGameState
    ==========================================================================================================
     */
    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();
    private final ObservableList<String> ticketsValues = FXCollections.observableArrayList();

    private final Map<Card, IntegerProperty> colorCardCount = new HashMap<>();
    private final Map<Route, BooleanProperty> isRouteClaimable = new HashMap<>();

    /**
     * ObservableGameState unique constructor
     * @param id the identity of the player to whom it corresponds
     */
    public ObservableGameState(PlayerId id) {
        playerId = id;
        gameState = null;
        playerState = null;

        ticketsPercentage.set(0);
        cardsPercentage.set(0);

        for (int i : FACE_UP_CARD_SLOTS) faceUpCards.add(new SimpleObjectProperty<>(null));
        for (Card card : Card.ALL) colorCardCount.put(card, new SimpleIntegerProperty(0));

        for (Route route : ChMap.routes()) {
            routeOwner.put(route, new SimpleObjectProperty<>(null));
            isRouteClaimable.put(route, new SimpleBooleanProperty(false));
        }

        for (int i = 0 ; i < PlayerId.COUNT ; i++) {
            ticketCounts.add(new SimpleIntegerProperty(0));
            cardCounts.add(new SimpleIntegerProperty(0));
            carCounts.add(new SimpleIntegerProperty(0));
            claimPoints.add(new SimpleIntegerProperty(0));
        }
    }

    public void reset() {
        gameState = null;
        playerState = null;

        ticketsPercentage.set(0);
        cardsPercentage.set(0);

        for (Card card : Card.ALL) colorCardCount.get(card).set(0);

        for (Route route : ChMap.routes()) {
            routeOwner.get(route).set(null);
            isRouteClaimable.get(route).set(false);
        }

        for (int i = 0 ; i < PlayerId.COUNT ; i++) {
            ticketCounts.get(i).set(0);
            cardCounts.get(i).set(0);
            carCounts.get(i).set(0);
            claimPoints.get(i).set(0);
        }
    }

    /**
     * Updates all the properties representing the game.
     * @param newGameState the public part of the game
     * @param newPlayerState the complete state of the player to whom this is corresponding to
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        PublicCardState newCardState = newGameState.cardState();
        gameState = newGameState;
        playerState = newPlayerState;

        ticketsPercentage.set(newGameState.ticketsCount() * 100 / ChMap.tickets().size());
        cardsPercentage.set(newCardState.deckSize() * 100 / TOTAL_CARDS_COUNT);

        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newCardState.faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        routeOwner.forEach((route, property) -> {
            if (newGameState.playerState(PLAYER_1).routes().contains(route))
                property.set(PLAYER_1);

            else if (newGameState.playerState(PLAYER_2).routes().contains(route))
                property.set(PLAYER_2);
        });

        for (PlayerId id : PlayerId.ALL) {
            PublicPlayerState playerState = newGameState.playerState(id);

            ticketCounts.get(id.ordinal())
                    .set(playerState.ticketCount());

            cardCounts.get(id.ordinal())
                    .set(playerState.cardCount());

            carCounts.get(id.ordinal())
                    .set(playerState.carCount());

            claimPoints.get(id.ordinal())
                    .set(playerState.claimPoints());
        }

        tickets.setAll(newPlayerState.tickets().toList());
        ticketsValues.setAll(newPlayerState.ticketsValue());

        colorCardCount.forEach((card, property) -> property.set(newPlayerState.cards().countOf(card)));

        isRouteClaimable.forEach((route, property) -> {
            final boolean isRouteClaimed = newGameState.claimedRoutes()
                    .stream()
                    .anyMatch(claimedRoute -> route.station1() == claimedRoute.station1() &&
                            route.station2() == claimedRoute.station2());

            property.set(!isRouteClaimed &&
                    playerId.equals(newGameState.currentPlayerId()) &&
                    newPlayerState.canClaimRoute(route));
        });
    }

    /**
     * Getter for the property containing the tickets percentage
     * @return the property containing the tickets percentage
     */
    public ReadOnlyIntegerProperty ticketPercentageProperty() {
        return ticketsPercentage;
    }

    /**
     * Getter for the property containing the cards percentage
     * @return the property containing the cards percentage
     */
    public ReadOnlyIntegerProperty cardsPercentageProperty() {
        return cardsPercentage;
    }

    /**
     * Getter for the property containing the face up card placed at the given slot
     * @param slot the location of the face up card
     * @return the property containing the face up card placed at the given slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCardProperty(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * Getter for the property containing the identity of the player possessing the given road
     * (or null if it does not belong to anyone)
     * @param route the road we want to know the owner of
     * @return the property containing the PlayerId of the player possessing the given road
     */
    public ReadOnlyObjectProperty<PlayerId> routeOwnerProperty(Route route) {
        return routeOwner.get(route);
    }

    /**
     * Getter of the property containing the ticket count of a player
     * @param id the identity of the player we want to know the ticket count
     * @return the property containing the ticket count of the given player
     */
    public ReadOnlyIntegerProperty ticketCountsProperty(PlayerId id) {
        return ticketCounts.get(id.ordinal());
    }

    /**
     * Getter of the property containing the card count of a player
     * @param id the identity of the player we want to know the card count
     * @return the property containing the card count of the given player
     */
    public ReadOnlyIntegerProperty cardCountsProperty(PlayerId id) {
        return cardCounts.get(id.ordinal());
    }

    /**
     * Getter of the property containing the car count of a player
     * @param id the identity of the player we want to know the car count
     * @return the property containing the car count of the given player
     */
    public ReadOnlyIntegerProperty carCountsProperty(PlayerId id) {
        return carCounts.get(id.ordinal());
    }

    /**
     * Getter of the property containing the amount of construction points for a player
     * @param id the identity of the player we want to know the amount of construction points
     * @return the property containing the amount of claim points for the given player
     */
    public ReadOnlyIntegerProperty claimPointsProperty(PlayerId id) {
        return claimPoints.get(id.ordinal());
    }

    /**
     * Getter for the ObservableMap of tickets owned by the player to whom this is corresponding to and the number of points they give
     * @return an unmodifiableObservableMap of the tickets and the number of points they give
     */
    public ObservableList<String> ticketsValuesProperty() {
        return FXCollections.unmodifiableObservableList(ticketsValues);
    }

    /**
     * Getter for the ObservableList of tickets owned by the player to whom this is corresponding to
     * @return an unmodifiableObservableList of the tickets
     */
    public ObservableList<Ticket> ticketsProperty() {
        return FXCollections.unmodifiableObservableList(tickets);
    }

    /**
     * Getter for the property containing the multiplicity of the given card in the player hand
     * @param card the type of the card we are interested in
     * @return the property containing the multiplicity of the given card in the player hand
     */
    public ReadOnlyIntegerProperty colorCardCountProperty(Card card) {
        return colorCardCount.get(card);
    }

    /**
     * Getter of the property containing a boolean value stating if the player (to whom this corresponds)
     * can claim a given road
     * @param route the road we are interested in
     * @return the property containing a boolean value stating if the player can claim the road
     */
    public ReadOnlyBooleanProperty isRouteClaimableProperty(Route route) {
        return isRouteClaimable.get(route);
    }


    /**
     * Method corresponding to the one of the same name in PublicGameState
     * (it is only a call of this one using the PublicGameState specified to set the properties in the method setState).
     * Returns true if a player can draw a card from the tickets deck
     * @return true if and only if ticketsCount is greater than 0
     */
    public boolean canDrawTickets() {
        return gameState.canDrawTickets();
    }

    /**
     * Method corresponding to the one of the same name in PublicGameState
     * (it is only a call of this one using the PublicGameState specified to set the properties in the method setState).
     * Returns true if it is possible to draw cards from the draw deck
     * @return true if the draw deck and the discard deck contain at least 5 cards
     */
    public boolean canDrawCards() {
        return gameState.canDrawCards();
    }

    /**
     * Method corresponding to the one of the same name in PlayerState
     * (it is only a call of this one using the PlayerState specified to set the properties in the method setState).
     * Return the list of all combinations (SortedBag) of Cards that the player can use to take possession of the given route
     * i.e. intersection of all that are possible and the ones that match the condition imposed by the player state
     * @param route route to claim
     * @return list of SortedBag of Card that the player can use to take possession of the given route
     *
     * @throws IllegalArgumentException if player doesn't have enough cars
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }

    /**
     * Method corresponding to the one of the same name in PlayerState.
     * Getter for the SortedBag of cards i.e. the hand of the player
     * @return hand of current player
     */
    public SortedBag<Card> cards() {
        return playerState.cards();
    }

    /**
     * Method corresponding to the one of the same name in PublicCardState.
     * Gives the five wagon/locomotive face up cards
     * @return an immutable list containing the five face up cards
     */
    public List<Card> faceUpCards() {
        return gameState.cardState().faceUpCards();
    }
}

