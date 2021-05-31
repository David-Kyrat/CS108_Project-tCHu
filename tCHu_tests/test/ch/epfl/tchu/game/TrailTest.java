package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrailTest {
    /**
     * we found it smarter to test both methods
     * ToString() and Longest() in the same time in this method
     */
    @Test
    void longest() {
        List<Route> mapRoutes = ChMap.routes();
        List<Route> testRoutes = new ArrayList<>();

        List<Route> testRoutesNull = new ArrayList<>();
      
        int[] routesToAdd = new int[]{46 /* GEN-LAU */, 44 /* LAU-FRI */, 13 /*FRI-BER*/, 19 /*BER-SOL*/, 15 /*BER-INT*/, 23 /*SION-BRIG*/, 58 /*LCF-YVE*/};
        for (int j : routesToAdd) {
            testRoutes.add(mapRoutes.get(j));
        }
        Trail longest = Trail.longest(testRoutes);
        assertEquals("Genève - Lausanne - Fribourg - Berne - Interlaken (11)", longest.toString());
        assertEquals("", Trail.longest(testRoutesNull).toString());

        List<Route> routes = List.of(ChMap.routes().get(66), ChMap.routes().get(65), ChMap.routes().get(19), ChMap.routes().get(18), ChMap.routes().get(13), ChMap.routes().get(16));
        Trail tr = Trail.longest(routes);
        assertEquals("Fribourg - Berne - Soleure - Neuchâtel - Berne - Lucerne (13)", tr.toString());
    }

    @Test
    void length() {
        List<Route> mapRoutes = ChMap.routes();
        List<Route> testRoutes = new ArrayList<>();
        List<Route> testRoutesNull = new ArrayList<>();

        int[] routesToAdd = new int[]{46 /* GEN-LAU */, 44 /* LAU-FRI */, 13 /*FRI-BER*/, 19 /*BER-SOL*/, 15 /*BER-INT*/, 23 /*SION-BRIG*/, 58 /*LCF-YVE*/};
        for (int j : routesToAdd) {
            testRoutes.add(mapRoutes.get(j));
        }
        Trail longest = Trail.longest(testRoutes);
        assertEquals(11, longest.length()); // expected is geneva-lausanne-fribourg-bern-interlaken (11)
        assertEquals(0,Trail.longest(testRoutesNull).length());
    }



    @Test
    void station1() {
        List<Route> mapRoutes = ChMap.routes();
        List<Route> testRoutes = new ArrayList<>();
        List<Route> testRoutesNull = new ArrayList<>();

        int[] routesToAdd = new int[]{46 /* GEN-LAU */, 44 /* LAU-FRI */, 13 /*FRI-BER*/, 19 /*BER-SOL*/, 15 /*BER-INT*/, 23 /*SION-BRIG*/, 58 /*LCF-YVE*/};
        for (int j : routesToAdd) {
            testRoutes.add(mapRoutes.get(j));
        }
        Trail longest = Trail.longest(testRoutes);
        assertEquals("Genève",longest.station1().name());
        assertEquals(null, Trail.longest(testRoutesNull).station1());
    }

    @Test
    void station2() {
        List<Route> mapRoutes = ChMap.routes();
        List<Route> testRoutes = new ArrayList<>();
        List<Route> testRoutesNull = new ArrayList<>();

        int[] routesToAdd = new int[]{46 /* GEN-LAU */, 44 /* LAU-FRI */, 13 /*FRI-BER*/, 19 /*BER-SOL*/, 15 /*BER-INT*/, 23 /*SION-BRIG*/, 58 /*LCF-YVE*/};
        for (int j : routesToAdd) {
            testRoutes.add(mapRoutes.get(j));
        }
        Trail longest = Trail.longest(testRoutes);
        assertEquals("Interlaken",longest.station2().name());
        assertEquals(null, Trail.longest(testRoutesNull).station2());
    }

}