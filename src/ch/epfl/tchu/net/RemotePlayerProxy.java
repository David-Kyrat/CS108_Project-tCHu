package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ch.epfl.tchu.net.MessageId.*;
import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Represents a proxy of distant player i.e."Server" part
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 * author Noah Munz (30779)
 */
public final class RemotePlayerProxy implements Player {

    private final BufferedReader reader;
    private final BufferedWriter writer;

    /**
     * Primary constructor of RemotePlayerProxy
     *
     * @param socket Socket used by the distant player's proxy to communicate with the server
     */
    public RemotePlayerProxy(Socket socket) {
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(), US_ASCII));

            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream(), US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames, Boolean rematch) {
        List<String> values = PlayerId.ALL.stream()
                                          .map(playerNames::get)
                                          .collect(Collectors.toUnmodifiableList());
        /* does this before, because call playerNames.values() does not always returns the values in the right order
        i.e. we want value for key PLAYER_I to be at the I-th position */
        sendMessage(INIT_PLAYERS.name(),
                    ID_SERDE.serialize(ownId),
                    STRINGS_SERDE.serialize(values),
                    BOOLEAN_SERDE.serialize(rematch));
    }

    @Override
    public void receiveInfo(String info) {
        sendMessage(RECEIVE_INFO.name(), STRING_SERDE.serialize(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        sendMessage(UPDATE_STATE.name(),
                    PUBLIC_GAME_STATE_SERDE.serialize(newState),
                    PLAYER_STATE_SERDE.serialize(ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(SET_INITIAL_TICKETS.name(), TICKET_SB_SERDE.serialize(tickets));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(CHOOSE_INITIAL_TICKETS.name());
        return TICKET_SB_SERDE.deserialize(response(reader));
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(NEXT_TURN.name());
        return TURNKIND_SERDE.deserialize(response(reader));
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(CHOOSE_TICKETS.name(), TICKET_SB_SERDE.serialize(options));
        return TICKET_SB_SERDE.deserialize(response(reader));
    }

    @Override
    public int drawSlot() {
        sendMessage(DRAW_SLOT.name());
        return INT_SERDE.deserialize(response(reader));
    }

    @Override
    public Route claimedRoute() {
        sendMessage(ROUTE.name());
        return ROUTE_SERDE.deserialize(response(reader));
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(CARDS.name());
        return CARDS_SB_SERDE.deserialize(response(reader));
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(CHOOSE_ADDITIONAL_CARDS.name(), CARD_LISTOF_SB_SERDE.serialize(options));
        return CARDS_SB_SERDE.deserialize(response(reader));
    }

    @Override
    public void askForRematch() {
        sendMessage(ASK_REMATCH.name());
    }

    @Override
    public Boolean rematchResponse() {
        sendMessage(REMATCH_ANSWER.name());
        return BOOLEAN_SERDE.deserialize(response(reader));
    }

    /**
     * Method used by the proxy to send a message
     *
     * @param args the serialize elements we want to transmit
     */
    private void sendMessage(String... args) {
        try {
            writer.write(String.join(" ", args) + '\n');
            writer.flush();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Method used by the proxy to receive the response of the client
     *
     * @param reader BufferedReader assigned to <code>this</code>
     * @return the response of the client
     */
    private String response(BufferedReader reader) {
        try {
            return reader.readLine().split(Pattern.quote(" "))[0];
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}