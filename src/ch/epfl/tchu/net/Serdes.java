package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Class containing all the useful serdes
 *
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class Serdes {

    private final static Base64.Encoder encoder = Base64.getEncoder();
    private final static Base64.Decoder decoder = Base64.getDecoder();
    private final static Serde<byte[]> BYTE_SERDE = Serde.of(encoder::encodeToString, decoder::decode);

    private final static String LIST_SEPARATOR = ",";
    private final static String SB_SEPARATOR = ","; //SB := SortedBag
    private final static String COMPOSITE_SEPARATOR = ";";
    private final static String PGS_SEPARATOR = ":"; //PGS := PublicGameState

      /* ===========================================
         ------------- Basic Types -----------------
         =========================================== */

    /**
     * Represents an integer serializer-deserializer
     */
    public final static Serde<Integer> INT_SERDE = Serde.of(String::valueOf, Integer::parseInt);

    /**
     * Represents a string serializer-deserializer
     */
    public final static Serde<String> STRING_SERDE = BYTE_SERDE.andThen(string -> string.getBytes(UTF_8),
                                                                        bytes -> new String(bytes, UTF_8));

    /**
     * Represents a PlayerId serializer-deserializer
     */
    public final static Serde<PlayerId> ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * Represents a TurnKind serializer-deserializer
     */
    public final static Serde<Player.TurnKind> TURNKIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Represents a Card serializer-deserializer
     */
    public final static Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * Represents a Route serializer-deserializer
     */
    public final static Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * Represents a Ticket serializer-deserializer
     */
    public final static Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /* ===========================================
       ----------- Collection Types --------------
       =========================================== */

    /**
     * Represents a List of strings serializer-deserializer
     */
    public final static Serde<List<String>> STRINGS_SERDE = Serde.listOf(STRING_SERDE, LIST_SEPARATOR);

    /**
     * Represents a List of cards serializer-deserializer
     */
    public final static Serde<List<Card>> CARDS_LIST_SERDE = Serde.listOf(CARD_SERDE, LIST_SEPARATOR);

    /**
     * Represents a List of roads serializer-deserializer
     */
    public final static Serde<List<Route>> ROUTES_SERDE = Serde.listOf(ROUTE_SERDE, LIST_SEPARATOR);

    /**
     * Represents a SortedBag of cards serializer-deserializer
     */
    public final static Serde<SortedBag<Card>> CARDS_SB_SERDE = Serde.bagOf(CARD_SERDE, SB_SEPARATOR);

    /**
     * Represents a SortedBag of Tickets serializer-deserializer
     */
    public final static Serde<SortedBag<Ticket>> TICKET_SB_SERDE = Serde.bagOf(TICKET_SERDE, SB_SEPARATOR);

    /**
     * Represents a List of sorted bags of cards serializer-deserializer
     */
    public final static Serde<List<SortedBag<Card>>> CARD_LISTOF_SB_SERDE = Serde.listOf(CARDS_SB_SERDE, COMPOSITE_SEPARATOR);

    /* =============================================
       ------------- Composite Types ---------------
       ============================================= */

    /**
     * Represents a PublicCardState serializer-deserializer
     */
    public final static Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(
            cs -> String.join(COMPOSITE_SEPARATOR,
                              CARDS_LIST_SERDE.serialize(cs.faceUpCards()),
                              INT_SERDE.serialize(cs.deckSize()),
                              INT_SERDE.serialize(cs.discardsSize())),

            message -> {
                List<String> list = toStringList(message, COMPOSITE_SEPARATOR);
                return new PublicCardState(CARDS_LIST_SERDE.deserialize(list.get(0)),
                                           INT_SERDE.deserialize(list.get(1)),
                                           INT_SERDE.deserialize(list.get(2)));
            });

    /**
     * Represents a PublicPlayerState serializer-deserializer
     */
    public final static Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            pps -> String.join(COMPOSITE_SEPARATOR,
                               INT_SERDE.serialize(pps.ticketCount()),
                               INT_SERDE.serialize(pps.cardCount()),
                               ROUTES_SERDE.serialize(pps.routes())),

            message -> {
                List<String> l = toStringList(message, COMPOSITE_SEPARATOR);
                return new PublicPlayerState(INT_SERDE.deserialize(l.get(0)),
                                             INT_SERDE.deserialize(l.get(1)),
                                             ROUTES_SERDE.deserialize(l.get(2)));
            });

    /**
     * Represents a PlayerState serializer-deserializer
     */
    public final static Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(
            ps -> String.join(COMPOSITE_SEPARATOR,
                              TICKET_SB_SERDE.serialize(ps.tickets()),
                              CARDS_SB_SERDE.serialize(ps.cards()),
                              ROUTES_SERDE.serialize(ps.routes())),

            message -> {
                List<String> l = toStringList(message, COMPOSITE_SEPARATOR);
                return new PlayerState(TICKET_SB_SERDE.deserialize(l.get(0)),
                                       CARDS_SB_SERDE.deserialize(l.get(1)),
                                       ROUTES_SERDE.deserialize(l.get(2)));
            });

    /**
     * Represents a PublicGameState serializer-deserializer
     */
    public final static Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            pgs -> String.join(PGS_SEPARATOR,
                               INT_SERDE.serialize(pgs.ticketsCount()),
                               PUBLIC_CARD_STATE_SERDE.serialize(pgs.cardState()),
                               ID_SERDE.serialize(pgs.currentPlayerId()),
                               PUBLIC_PLAYER_STATE_SERDE.serialize(pgs.playerState(PlayerId.PLAYER_1)),
                               PUBLIC_PLAYER_STATE_SERDE.serialize(pgs.playerState(PlayerId.PLAYER_2)),
                               pgs.lastPlayer() == null ? ""
                                                        : ID_SERDE.serialize(pgs.currentPlayerId())),

            message -> {
                List<String> l = toStringList(message, PGS_SEPARATOR);

                Map<PlayerId, PublicPlayerState> map = PlayerId.ALL.stream()
                                                                   .collect(Collectors.toUnmodifiableMap(
                                                                           id -> id,
                                                                           id -> PUBLIC_PLAYER_STATE_SERDE.deserialize(l.get(id.ordinal() + 3))));

                return new PublicGameState(INT_SERDE.deserialize(l.get(0)),
                                           PUBLIC_CARD_STATE_SERDE.deserialize(l.get(1)),
                                           ID_SERDE.deserialize(l.get(2)),
                                           map,
                                           l.get(5).equals("") ? null
                                                               : ID_SERDE.deserialize(l.get(5)));
            });

    /**
     * Private constructor to remove the default one and make Serdes not instantiable
     */
    private Serdes() {throw new UnsupportedOperationException();}

    private static List<String> toStringList(String message, String separator) {
        return Arrays.stream(message.split(Pattern.quote(separator), -1))
                     .collect(Collectors.toList());
    }
}