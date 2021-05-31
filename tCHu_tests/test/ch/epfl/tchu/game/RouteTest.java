package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {
    static Station stationA = new Station(0, "A");
    static Station stationB = new Station(0, "B");
    static Station stationC = new Station(0, "C");
    static Route testRoute  = new Route("test", stationA, stationB, 2, Route.Level.OVERGROUND, null);
    static Route testRouteUnderground  = new Route("test", stationB, stationC, 2, Route.Level.UNDERGROUND, null);
    static Route longBlueRoute = new Route("test", stationA, stationB, 5, Route.Level.UNDERGROUND, Color.BLUE);

    @Test
    void testLengthConditionsOnRoutes() {
        assertThrows(IllegalArgumentException.class, () -> new Route("test", stationA, stationB, 0, Route.Level.UNDERGROUND, null));
        assertThrows(IllegalArgumentException.class, () -> new Route("test", stationA, stationB, 7, Route.Level.UNDERGROUND, null));
        assertDoesNotThrow(() -> new Route("test", stationA, stationB, 2, Route.Level.UNDERGROUND, null));
    }

    @Test
    void stationOppositeTest() {
        assertEquals(testRoute.stationOpposite(stationA), stationB);
        assertEquals(testRoute.stationOpposite(stationB), stationA);
        assertThrows(IllegalArgumentException.class, () -> testRoute.stationOpposite(stationC));
    }

    @Test
    void stationListTest() {
        assertEquals(List.of(stationA, stationB), testRoute.stations());
    }

    @Test
    void possibleClaimCardsCountSimpleTest() {
        assertEquals(8, testRoute.possibleClaimCards().size()); // 8 possible cards overground for any color
        assertEquals(17, testRouteUnderground.possibleClaimCards().size()); // as given in the document
    }

    @Test
    void possibleClaimCardsCountExtendedTestUnderground() {
        var claimCards = testRouteUnderground.possibleClaimCards();

        for (int i = 0; i < 8; i++) {
            int finalI = i;
            assertEquals(2, claimCards.get(i).stream().filter(d -> Color.ALL.get(finalI).equals(d.color())).count()); // we should have the right colors
            // Note for the previous line : we use Color.ALL.get(i).equals to prevent the case where d.color() is null
            assertEquals(2, claimCards.get(i).stream().count());
        }
        for (int i = 8; i < 16; i++) {
            int finalI = i-8;
            assertEquals(1, claimCards.get(i).stream().filter(d -> Color.ALL.get(finalI).equals(d.color())).count()); // we should have the right colors
            assertEquals(1, claimCards.get(i).stream().filter(d -> d.color() == null).count());
            assertEquals(2, claimCards.get(i).stream().count());
        }
        assertEquals(2, claimCards.get(16).stream().count());
        assertEquals(2, claimCards.get(16).stream().filter(d -> d.color() == null).count());
    }

    @Test
    void additionalClaimCardsCountTest() {
        SortedBag<Card> claimCards = new SortedBag.Builder<Card>().add(2, Card.BLUE).build();
        SortedBag<Card> drawnCardsA = new SortedBag.Builder<Card>().add(2, Card.BLUE).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> drawnCardsB = new SortedBag.Builder<Card>().add(Card.BLUE).add(Card.LOCOMOTIVE).add(Card.RED).build();
        SortedBag<Card> drawnCardsC = new SortedBag.Builder<Card>().add(Card.BLUE).add(Card.GREEN).add(Card.RED).build();
        SortedBag<Card> drawnCardsD = new SortedBag.Builder<Card>().add(2, Card.RED).add(Card.GREEN).build();
        SortedBag<Card> illegalDrawnCardsA = new SortedBag.Builder<Card>().build();
        SortedBag<Card> illegalDrawnCardsB = new SortedBag.Builder<Card>().add(5, Card.LOCOMOTIVE).build();

        assertEquals(testRouteUnderground.additionalClaimCardsCount(claimCards, drawnCardsA), 3);
        assertEquals(testRouteUnderground.additionalClaimCardsCount(claimCards, drawnCardsB), 2);
        assertEquals(testRouteUnderground.additionalClaimCardsCount(claimCards, drawnCardsC), 1);
        assertEquals(testRouteUnderground.additionalClaimCardsCount(claimCards, drawnCardsD), 0);
        assertThrows(IllegalArgumentException.class, () -> testRouteUnderground.additionalClaimCardsCount(claimCards, illegalDrawnCardsA));
        assertThrows(IllegalArgumentException.class, () -> testRouteUnderground.additionalClaimCardsCount(claimCards, illegalDrawnCardsB));
        assertThrows(IllegalArgumentException.class, () -> testRoute.additionalClaimCardsCount(claimCards, drawnCardsA));

        SortedBag<Card> claimCards_A = new SortedBag.Builder<Card>().add(5, Card.BLUE).build();
        assertEquals(longBlueRoute.additionalClaimCardsCount(claimCards_A, drawnCardsA), 3);
        assertEquals(longBlueRoute.additionalClaimCardsCount(claimCards_A, drawnCardsB), 2);
        assertEquals(longBlueRoute.additionalClaimCardsCount(claimCards_A, drawnCardsC), 1);
        assertEquals(longBlueRoute.additionalClaimCardsCount(claimCards_A, drawnCardsD), 0);
    }

    @Test
    void claimPoints() {
        assertEquals(1, new Route("test", stationA, stationB, 1, Route.Level.OVERGROUND, null).claimPoints());
        assertEquals(2, new Route("test", stationA, stationB, 2, Route.Level.OVERGROUND, null).claimPoints());
        assertEquals(4, new Route("test", stationA, stationB, 3, Route.Level.OVERGROUND, null).claimPoints());
        assertEquals(7, new Route("test", stationA, stationB, 4, Route.Level.OVERGROUND, null).claimPoints());
        assertEquals(10, new Route("test", stationA, stationB, 5, Route.Level.OVERGROUND, null).claimPoints());
        assertEquals(15, new Route("test", stationA, stationB, 6, Route.Level.OVERGROUND, null).claimPoints());
    }
}
