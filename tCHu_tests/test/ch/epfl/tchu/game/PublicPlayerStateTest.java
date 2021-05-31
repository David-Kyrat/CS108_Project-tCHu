package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicPlayerStateTest {

    @Test
    void ticketCount() {
        for (int i = 0; i < 10; i++) {
            assertEquals(new PublicPlayerState(i, 0, Collections.emptyList()).ticketCount(), i);
        }
        assertThrows(IllegalArgumentException.class, () -> new PublicPlayerState(-1, 0, Collections.emptyList()));
    }

    @Test
    void cardCount() {
        for (int i = 0; i < 10; i++) {
            assertEquals(new PublicPlayerState(0, i, Collections.emptyList()).cardCount(), i);
        }
        assertThrows(IllegalArgumentException.class, () -> new PublicPlayerState(0, -1, Collections.emptyList()));
    }

    @Test
    void carCount() {
        Route routeA = new Route("1", new Station(1, "mdr"), new Station(2, "lol"), 4, Route.Level.UNDERGROUND, Color.RED);
        Route routeB = new Route("1", new Station(1, "mdr"), new Station(2, "lol"), 6, Route.Level.UNDERGROUND, Color.RED);
        List<Route> routes = List.of(routeA, routeB);

        PublicPlayerState publicPlayerState = new PublicPlayerState(1, 1, routes);
        assertEquals(publicPlayerState.carCount(), Constants.INITIAL_CAR_COUNT - 4 - 6);
    }

    @Test
    void claimPoints() {
        Route routeA = new Route("1", new Station(1, "mdr"), new Station(2, "lol"), 4, Route.Level.UNDERGROUND, Color.RED);
        Route routeB = new Route("1", new Station(1, "mdr"), new Station(2, "lol"), 6, Route.Level.UNDERGROUND, Color.RED);
        List<Route> routes = List.of(routeA, routeB);

        PublicPlayerState publicPlayerState = new PublicPlayerState(1, 1, routes);
        assertEquals(publicPlayerState.claimPoints(), Constants.ROUTE_CLAIM_POINTS.get(4) + Constants.ROUTE_CLAIM_POINTS.get(6));
    }
}