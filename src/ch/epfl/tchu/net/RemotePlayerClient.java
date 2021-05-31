package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Represents a distant player's client
 * @author Mehdi Bouguerra Ezzina (314857)
 * @author Noah Munz (310779)
 */
public final class RemotePlayerClient {

    private final Player player;
    private final String proxyName;
    private final int PROXY_PORT;

    /**
     * RemotePlayerClient unique constructor
     * @param player    the player to whom it must provide remote access
     * @param proxyName a String of the name of the proxy
     * @param proxyPort an int representing the port of the proxy
     */
    public RemotePlayerClient(Player player, String proxyName, int proxyPort) {
        this.player = player;
        this.proxyName = proxyName;
        PROXY_PORT = proxyPort;
    }

    /**
     * Waits for a message from the proxy and, depending on the type of this message, will deserialize its arguments
     * and call the corresponding method of the player. If this method returns a result, it will serialize it and send
     * it back to the proxy in response.
     */
    public void run() {
        try {
            Socket socket = new Socket(proxyName, PROXY_PORT);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(), US_ASCII));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream(), US_ASCII));
            String message;
            String[] infos;
            while ((message = reader.readLine()) != null) {
                infos = message.split(Pattern.quote(" "), -1);
                System.out.println("inside loop");
                System.out.println(MessageId.valueOf(infos[0]));

                switch (MessageId.valueOf(infos[0])) {
                    case INIT_PLAYERS:
                        PlayerId id = ID_SERDE.deserialize(infos[1]);
                        List<String> namesList = STRINGS_SERDE.deserialize(infos[2]);

                        Map<PlayerId, String> playerNames = PlayerId.ALL.stream()
                                                                        .collect(Collectors.toUnmodifiableMap(
                                                                                playerId -> playerId,
                                                                                playerId -> namesList.get(playerId.ordinal())));

                        player.initPlayers(id, playerNames);
                        break;

                    case RECEIVE_INFO:
                        player.receiveInfo(STRING_SERDE.deserialize(infos[1]));
                        break;

                    case UPDATE_STATE:
                        PublicGameState publicGameState = PUBLIC_GAME_STATE_SERDE.deserialize(infos[1]);
                        PlayerState playerState = PLAYER_STATE_SERDE.deserialize(infos[2]);
                        player.updateState(publicGameState, playerState);
                        break;

                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(TICKET_SB_SERDE.deserialize(infos[1]));
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        sendMessage(writer, TICKET_SB_SERDE.serialize(player.chooseInitialTickets()));
                        break;

                    case NEXT_TURN:
                        sendMessage(writer, TURNKIND_SERDE.serialize(player.nextTurn()));
                        break;

                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> options = TICKET_SB_SERDE.deserialize(infos[1]);
                        sendMessage(writer, TICKET_SB_SERDE.serialize(player.chooseTickets(options)));
                        break;

                    case DRAW_SLOT:
                        sendMessage(writer, INT_SERDE.serialize(player.drawSlot()));
                        break;

                    case ROUTE:
                        sendMessage(writer, ROUTE_SERDE.serialize(player.claimedRoute()));
                        break;

                    case CARDS:
                        sendMessage(writer, CARDS_SB_SERDE.serialize(player.initialClaimCards()));
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> sortedBags = CARD_LISTOF_SB_SERDE.deserialize(infos[1]);
                        sendMessage(writer, CARDS_SB_SERDE.serialize(player.chooseAdditionalCards(sortedBags)));
                        break;
                }
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Method used by the client to send a message
     * @param args the serialize elements we want to transmit
     */
    private void sendMessage(BufferedWriter writer, String... args) {
        try {
            writer.write(String.join(" ", args) + '\n');
            writer.flush();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}