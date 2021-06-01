package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Interface representing an action handler
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public interface ActionHandlers {

    /**
     * Interface representing a handler for the drawing of tickets
     */
    @FunctionalInterface
    interface DrawTicketsHandler {

        /**
         * Method called when a player want to draw tickets
         */
        void onDrawTickets();
    }

    /**
     * Interface representing a handler for the drawing of cards
     */
    @FunctionalInterface
    interface DrawCardHandler {

        /**
         * Method called when a player want to draw card
         * @param slot the location from where the player want to draw the card
         *             (-1 for the deck, 0 to 4 - both included - for the face up cards)
         */
        void onDrawCard(int slot);
    }

    /**
     * Interface representing a handler for the claiming of a route
     */
    @FunctionalInterface
    interface ClaimRouteHandler{

        /**
         * Method called when the player wants to take the given road with the given initial cards
         * @param wantedRoute the road the player want to take over
         * @param claimCards the initial cards he's using (trying) to do so
         */
        void onClaimRoute(Route wantedRoute, SortedBag<Card> claimCards);
    }

    /**
     * Interface representing a handler for the choice of tickets
     */
    @FunctionalInterface
    interface ChooseTicketsHandler{

        /**
         * Method called when the player has chosen to keep the given tickets following a ticket draw
         * @param keptTickets the tickets the player have choose to keep
         */
        void onChooseTickets(SortedBag<Ticket> keptTickets);
    }

    /**
     * Interface representing a handler for the choice of cards
     */
    @FunctionalInterface
    interface ChooseCardsHandler{

        /**
         * Method called when the player has chosen to use some cards when taking possession of a road
         * @param claimCards the initial, or additional, claim cards used by a player
         *                   (if they are additional cards, then the multi-set can be empty,
         *                   which means that the player gives up on taking the tunnel.)
         */
        void onChooseCards(SortedBag<Card> claimCards);
    }

    interface AskHandler {
        void ask(Boolean answer);
    }
}
