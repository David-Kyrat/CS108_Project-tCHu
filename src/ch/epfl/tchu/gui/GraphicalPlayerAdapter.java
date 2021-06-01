package ch.epfl.tchu.gui;

import ch.epfl.tchu.*;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.stage.*;

import java.util.*;
import java.util.concurrent.*;

import static ch.epfl.tchu.game.Player.TurnKind.*;
import static javafx.application.Platform.*;

/**
 * This class adapts the class GraphicalPlayer into a Player value
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class GraphicalPlayerAdapter implements Player {

    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> turnKindQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> drawSlotQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> claimRouteQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> claimCardsQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Boolean> rematchQueue = new ArrayBlockingQueue<>(1);

    private GraphicalPlayer graphicalPlayer;
    private Stage stage;

    GraphicalPlayerAdapter setStage(Stage stage) {
        if (this.stage == null) this.stage = stage;
        return this;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames, Boolean rematch) {
        System.out.println("init players and creates graphical players");
        if(!rematch) {
            runLater(() -> graphicalPlayer = stage == null ? new GraphicalPlayer(ownId, playerNames)
                                                           : new GraphicalPlayer(ownId, playerNames, stage));
        } else {
            runLater(() -> graphicalPlayer.resetForRematch());
        }
    }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        ChooseTicketsHandler handler = keptTickets -> {
            try {
                ticketsQueue.put(keptTickets);
            }
            catch (InterruptedException e) {
                throw new Error();
            }
        };

        runLater(() -> graphicalPlayer.chooseTickets(tickets, handler));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return ticketsQueue.take();
        }
        catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public TurnKind nextTurn() {
        DrawTicketsHandler ticketsHandler = () -> {
            try {
                turnKindQueue.put(DRAW_TICKETS);
            }
            catch (InterruptedException e) {
                throw new Error();
            }
        };

        DrawCardHandler cardHandler = slot -> {
            try {
                drawSlotQueue.put(slot);
                turnKindQueue.put(DRAW_CARDS);
            }
            catch (InterruptedException e) {
                throw new Error();
            }
        };

        ClaimRouteHandler claimHandler = (wantedRoute, claimCards) -> {
            try {
                claimRouteQueue.put(wantedRoute);
                claimCardsQueue.put(claimCards);
                turnKindQueue.put(CLAIM_ROUTE);
            }
            catch (InterruptedException e) {
                throw new Error();
            }
        };

        runLater(() -> graphicalPlayer.startTurn(ticketsHandler, cardHandler, claimHandler));

        try {
            return turnKindQueue.take();
        }
        catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        ChooseTicketsHandler handler = keptTickets -> {
            try {
                ticketsQueue.put(keptTickets);
            }
            catch (InterruptedException e) {
                throw new Error();
            }
        };

        runLater(() -> graphicalPlayer.chooseTickets(options, handler));

        try {
            return ticketsQueue.take();
        }
        catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public int drawSlot() {
        if (!drawSlotQueue.isEmpty()) return drawSlotQueue.remove();
        else {
            DrawCardHandler cardHandler = slot -> {
                try {
                    drawSlotQueue.put(slot);
                }
                catch (InterruptedException e) {
                    throw new Error();
                }
            };

            runLater(() -> graphicalPlayer.drawCard(cardHandler));

            try {
                return drawSlotQueue.take();
            }
            catch (InterruptedException e) {
                throw new Error();
            }
        }
    }

    @Override
    public Route claimedRoute() {
        try {
            return claimRouteQueue.take();
        }
        catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return claimCardsQueue.take();
        }
        catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        ChooseCardsHandler handler = claimCards -> {
            try {
                claimCardsQueue.put(claimCards);
            }
            catch (InterruptedException e) {
                throw new Error();
            }
        };

        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, handler));

        try {
            return claimCardsQueue.take();
        }
        catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public void askForRematch() {
        AskHandler handler = response -> {
            try {
                rematchQueue.put(response);
            } catch (InterruptedException e) {
                throw new Error();
            }
        };

        runLater(() -> graphicalPlayer.askForRematch(handler));
    }

    @Override
    public Boolean rematchResponse() {
        try {
            return rematchQueue.take();
        }
        catch (InterruptedException e) {
            throw new Error();
        }
    }
}
