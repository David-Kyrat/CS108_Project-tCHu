package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class StationPartitionTest {

    @Test
    void testForConnected(){
        Station[] stations = new Station[]{
                new Station(5, "Baden"),
                new Station(12, "Bâle"),
                new Station(9, "Bellinzone"),
                new Station(25, "Berne"),
                new Station(1, "Brigue")};

        StationPartition.Builder builder = new StationPartition.Builder(26);
        builder.connect(stations[0],stations[3]);
        StationPartition partition = builder.build();

        assertTrue(partition.connected(stations[0],stations[3]));//true for two connected stations
        assertTrue(partition.connected(stations[0],stations[0]));//true when the two stations are the same
        assertTrue(partition.connected(stations[4],stations[4]));//true when two stations are the same even if their id is out of bound
        assertFalse(partition.connected(stations[4],stations[0]));//false when the stations don't have the same id
        assertFalse(partition.connected(stations[0], stations[1]));//false when the stations are not yet connected

        builder.connect(stations[0],stations[1]);//connecting the stations 0 and 1 that were not connected
        partition = builder.build();

        assertTrue(partition.connected(stations[0],stations[1]));//assert that now they are connected

    }

    @Test
    void testExampleDuProf() {
        Station[] stations = new Station[]{
            new Station(0, "Paris"),
            new Station(1, "Lyon"),
            new Station(2, "Bordeaux"),
            new Station(3, "Marseille"),
            new Station(4, "Toulouse"),
            new Station(5, "Nice"),
            new Station(6, "Brest"),
            new Station(7, "Nantes")};


        StationPartition.Builder builder = new StationPartition.Builder(8);

        BiFunction<Integer, Integer, StationPartition.Builder> connect = (a, b) -> builder.connect(stations[a], stations[b]);

        connect.apply(0, 5); // Paris Nice
        connect.apply(0, 6); // Paris Brest
        connect.apply(2, 7); // Bordeaux Nantes
        connect.apply(7, 5); // Nantes Nice
        connect.apply(1, 3); // Lyon Marseille
        StationPartition partition = builder.build();

        // Paris
        assertTrue(partition.connected(stations[0], stations[5]));
        assertTrue(partition.connected(stations[0], stations[6]));
        assertTrue(partition.connected(stations[0], stations[2]));
        assertTrue(partition.connected(stations[0], stations[7]));
        assertFalse(partition.connected(stations[0], stations[4]));
        assertFalse(partition.connected(stations[0], stations[1]));
        assertFalse(partition.connected(stations[0], stations[3]));

        // Lyon
        assertFalse(partition.connected(stations[1], stations[5]));
        assertFalse(partition.connected(stations[1], stations[0]));
        assertFalse(partition.connected(stations[1], stations[4]));
        assertFalse(partition.connected(stations[1], stations[6]));
        assertFalse(partition.connected(stations[1], stations[2]));
        assertFalse(partition.connected(stations[1], stations[7]));
        assertTrue(partition.connected(stations[1], stations[3]));

        // Marseille
        assertFalse(partition.connected(stations[3], stations[5]));
        assertFalse(partition.connected(stations[3], stations[0]));
        assertFalse(partition.connected(stations[3], stations[4]));
        assertFalse(partition.connected(stations[3], stations[6]));
        assertFalse(partition.connected(stations[3], stations[2]));
        assertFalse(partition.connected(stations[3], stations[7]));
        assertTrue(partition.connected(stations[3], stations[1]));

        // Toulouse
        assertFalse(partition.connected(stations[4], stations[5]));
        assertFalse(partition.connected(stations[4], stations[0]));
        assertFalse(partition.connected(stations[4], stations[3]));
        assertFalse(partition.connected(stations[4], stations[6]));
        assertFalse(partition.connected(stations[4], stations[2]));
        assertFalse(partition.connected(stations[4], stations[7]));
        assertFalse(partition.connected(stations[4], stations[1]));

        // Nice
        assertTrue(partition.connected(stations[5], stations[0]));
        assertTrue(partition.connected(stations[5], stations[6]));
        assertTrue(partition.connected(stations[5], stations[2]));
        assertTrue(partition.connected(stations[5], stations[7]));
        assertFalse(partition.connected(stations[5], stations[4]));
        assertFalse(partition.connected(stations[5], stations[1]));
        assertFalse(partition.connected(stations[5], stations[3]));


        // Brest
        assertTrue(partition.connected(stations[6], stations[0]));
        assertTrue(partition.connected(stations[6], stations[6]));
        assertTrue(partition.connected(stations[6], stations[5]));
        assertTrue(partition.connected(stations[6], stations[2]));
        assertTrue(partition.connected(stations[6], stations[7]));
        assertFalse(partition.connected(stations[6], stations[4]));
        assertFalse(partition.connected(stations[6], stations[1]));
        assertFalse(partition.connected(stations[6], stations[3]));

        // Nantes
        assertTrue(partition.connected(stations[7], stations[0]));
        assertTrue(partition.connected(stations[7], stations[7]));
        assertTrue(partition.connected(stations[7], stations[5]));
        assertTrue(partition.connected(stations[7], stations[2]));
        assertTrue(partition.connected(stations[7], stations[6]));
        assertFalse(partition.connected(stations[7], stations[4]));
        assertFalse(partition.connected(stations[7], stations[1]));
        assertFalse(partition.connected(stations[7], stations[3]));
    }
    @Test
    void testForBuilder(){
        assertThrows(IllegalArgumentException.class, () -> new StationPartition.Builder(-2));
    }

    @Test
    void testSupp(){

        Station[] stations = new Station[]{
                new Station(0, "Berne") ,
                new Station(1, "Delémont") ,
                new Station(2, "Fribourg") ,
                new Station(3, "Interlaken") ,
                new Station(4, "La Chaux-De-Fonds") ,
                new Station(5, "Lausanne") ,
                new Station(6, "Lucerne") ,
                new Station(7, "Neuchâtel") ,
                new Station(8, "Olten") ,
                new Station(9, "Schwyz") ,
                new Station(10, "Soleure") ,
                new Station(11, "Wassen") ,
                new Station(12, "Yverdon") ,
                new Station(13, "Zoug") ,
                new Station(14, "Zürich")
        };
        StationPartition.Builder builder = new StationPartition.Builder(15);

        BiFunction<Integer, Integer, StationPartition.Builder> connect = (a, b) -> builder.connect(stations[a], stations[b]);

        connect.apply(5, 2); //Lausanne Fribourg
        connect.apply(2, 0); //Fribourg Berne
        connect.apply(3, 0); //Berne Interlaken
        connect.apply(7, 10); //Neuchâtel Soleure
        connect.apply(10, 8); //Soleure Olten
        connect.apply(6,13); //Lucerne Zoug
        connect.apply(13,9); //Zoug Schwyz
        connect.apply(9,11); //Schwyz Wassen
        StationPartition partition = builder.build();


        // Berne
        assertTrue(partition.connected(stations[0], stations[5]));
        assertTrue(partition.connected(stations[0], stations[2]));
        assertTrue(partition.connected(stations[0], stations[3]));
        assertFalse(partition.connected(stations[0], stations[1]));
        assertFalse(partition.connected(stations[0], stations[4]));
        assertFalse(partition.connected(stations[0], stations[6]));
        assertFalse(partition.connected(stations[0], stations[7]));
        assertFalse(partition.connected(stations[0], stations[8]));
        assertFalse(partition.connected(stations[0], stations[9]));
        assertFalse(partition.connected(stations[0], stations[10]));
        assertFalse(partition.connected(stations[0], stations[11]));
        assertFalse(partition.connected(stations[0], stations[12]));
        assertFalse(partition.connected(stations[0], stations[13]));
        assertFalse(partition.connected(stations[0], stations[14]));

        // Delémont
        assertFalse(partition.connected(stations[1], stations[0]));
        assertFalse(partition.connected(stations[1], stations[2]));
        assertFalse(partition.connected(stations[1], stations[3]));
        assertFalse(partition.connected(stations[1], stations[4]));
        assertFalse(partition.connected(stations[1], stations[5]));
        assertFalse(partition.connected(stations[1], stations[6]));
        assertFalse(partition.connected(stations[1], stations[7]));
        assertFalse(partition.connected(stations[1], stations[8]));
        assertFalse(partition.connected(stations[1], stations[9]));
        assertFalse(partition.connected(stations[1], stations[10]));
        assertFalse(partition.connected(stations[1], stations[11]));
        assertFalse(partition.connected(stations[1], stations[12]));
        assertFalse(partition.connected(stations[1], stations[13]));
        assertFalse(partition.connected(stations[1], stations[14]));


        // Fribourg
        assertTrue(partition.connected(stations[2], stations[5]));
        assertTrue(partition.connected(stations[2], stations[0]));
        assertTrue(partition.connected(stations[2], stations[3]));
        assertFalse(partition.connected(stations[2], stations[1]));
        assertFalse(partition.connected(stations[2], stations[4]));
        assertFalse(partition.connected(stations[2], stations[6]));
        assertFalse(partition.connected(stations[2], stations[7]));
        assertFalse(partition.connected(stations[2], stations[8]));
        assertFalse(partition.connected(stations[2], stations[9]));
        assertFalse(partition.connected(stations[2], stations[10]));
        assertFalse(partition.connected(stations[2], stations[11]));
        assertFalse(partition.connected(stations[2], stations[12]));
        assertFalse(partition.connected(stations[2], stations[13]));
        assertFalse(partition.connected(stations[2], stations[14]));

        // Interlaken
        assertTrue(partition.connected(stations[3], stations[5]));
        assertTrue(partition.connected(stations[3], stations[0]));
        assertTrue(partition.connected(stations[3], stations[2]));
        assertFalse(partition.connected(stations[3], stations[1]));
        assertFalse(partition.connected(stations[3], stations[4]));
        assertFalse(partition.connected(stations[3], stations[6]));
        assertFalse(partition.connected(stations[3], stations[7]));
        assertFalse(partition.connected(stations[3], stations[8]));
        assertFalse(partition.connected(stations[3], stations[9]));
        assertFalse(partition.connected(stations[3], stations[10]));
        assertFalse(partition.connected(stations[3], stations[11]));
        assertFalse(partition.connected(stations[3], stations[12]));
        assertFalse(partition.connected(stations[3], stations[13]));
        assertFalse(partition.connected(stations[3], stations[14]));

        // La Chaux-de-Fonds
        assertFalse(partition.connected(stations[4], stations[0]));
        assertFalse(partition.connected(stations[4], stations[1]));
        assertFalse(partition.connected(stations[4], stations[2]));
        assertFalse(partition.connected(stations[4], stations[3]));
        assertFalse(partition.connected(stations[4], stations[5]));
        assertFalse(partition.connected(stations[4], stations[6]));
        assertFalse(partition.connected(stations[4], stations[7]));
        assertFalse(partition.connected(stations[4], stations[8]));
        assertFalse(partition.connected(stations[4], stations[9]));
        assertFalse(partition.connected(stations[4], stations[10]));
        assertFalse(partition.connected(stations[4], stations[11]));
        assertFalse(partition.connected(stations[4], stations[12]));
        assertFalse(partition.connected(stations[4], stations[13]));
        assertFalse(partition.connected(stations[4], stations[14]));



        //Lausanne
         assertTrue(partition.connected(stations[5], stations[3]));
         assertTrue(partition.connected(stations[5], stations[0]));
         assertTrue(partition.connected(stations[5], stations[2]));
        assertFalse(partition.connected(stations[5], stations[1]));
        assertFalse(partition.connected(stations[5], stations[4]));
        assertFalse(partition.connected(stations[5], stations[6]));
        assertFalse(partition.connected(stations[5], stations[7]));
        assertFalse(partition.connected(stations[5], stations[8]));
        assertFalse(partition.connected(stations[5], stations[9]));
        assertFalse(partition.connected(stations[5], stations[10]));
        assertFalse(partition.connected(stations[5], stations[11]));
        assertFalse(partition.connected(stations[5], stations[12]));
        assertFalse(partition.connected(stations[5], stations[13]));
        assertFalse(partition.connected(stations[5], stations[14]));


        // Lucerne
         assertTrue(partition.connected(stations[6], stations[9]));
         assertTrue(partition.connected(stations[6], stations[11]));
         assertTrue(partition.connected(stations[6], stations[13]));
        assertFalse(partition.connected(stations[6], stations[1]));
        assertFalse(partition.connected(stations[6], stations[4]));
        assertFalse(partition.connected(stations[6], stations[5]));
        assertFalse(partition.connected(stations[6], stations[7]));
        assertFalse(partition.connected(stations[6], stations[8]));
        assertFalse(partition.connected(stations[6], stations[3]));
        assertFalse(partition.connected(stations[6], stations[10]));
        assertFalse(partition.connected(stations[6], stations[0]));
        assertFalse(partition.connected(stations[6], stations[12]));
        assertFalse(partition.connected(stations[6], stations[2]));
        assertFalse(partition.connected(stations[6], stations[14]));


        // Neuchâtel
         assertTrue(partition.connected(stations[7], stations[8]));
         assertTrue(partition.connected(stations[7], stations[10]));
        assertFalse(partition.connected(stations[7], stations[13]));
        assertFalse(partition.connected(stations[7], stations[1]));
        assertFalse(partition.connected(stations[7], stations[4]));
        assertFalse(partition.connected(stations[7], stations[5]));
        assertFalse(partition.connected(stations[7], stations[6]));
        assertFalse(partition.connected(stations[7], stations[11]));
        assertFalse(partition.connected(stations[7], stations[3]));
        assertFalse(partition.connected(stations[7], stations[9]));
        assertFalse(partition.connected(stations[7], stations[0]));
        assertFalse(partition.connected(stations[7], stations[12]));
        assertFalse(partition.connected(stations[7], stations[2]));
        assertFalse(partition.connected(stations[7], stations[14]));


        // Olten
         assertTrue(partition.connected(stations[8], stations[7]));
         assertTrue(partition.connected(stations[8], stations[10]));
        assertFalse(partition.connected(stations[8], stations[13]));
        assertFalse(partition.connected(stations[8], stations[1]));
        assertFalse(partition.connected(stations[8], stations[4]));
        assertFalse(partition.connected(stations[8], stations[5]));
        assertFalse(partition.connected(stations[8], stations[6]));
        assertFalse(partition.connected(stations[8], stations[11]));
        assertFalse(partition.connected(stations[8], stations[3]));
        assertFalse(partition.connected(stations[8], stations[9]));
        assertFalse(partition.connected(stations[8], stations[0]));
        assertFalse(partition.connected(stations[8], stations[12]));
        assertFalse(partition.connected(stations[8], stations[2]));
        assertFalse(partition.connected(stations[8], stations[14]));

        // Schwyz
         assertTrue(partition.connected(stations[9], stations[6]));
         assertTrue(partition.connected(stations[9], stations[11]));
         assertTrue(partition.connected(stations[9], stations[13]));
        assertFalse(partition.connected(stations[9], stations[1]));
        assertFalse(partition.connected(stations[9], stations[4]));
        assertFalse(partition.connected(stations[9], stations[5]));
        assertFalse(partition.connected(stations[9], stations[7]));
        assertFalse(partition.connected(stations[9], stations[8]));
        assertFalse(partition.connected(stations[9], stations[3]));
        assertFalse(partition.connected(stations[9], stations[10]));
        assertFalse(partition.connected(stations[9], stations[0]));
        assertFalse(partition.connected(stations[9], stations[12]));
        assertFalse(partition.connected(stations[9], stations[2]));
        assertFalse(partition.connected(stations[9], stations[14]));


        // Soleure
         assertTrue(partition.connected(stations[10], stations[7]));
         assertTrue(partition.connected(stations[10], stations[8]));
        assertFalse(partition.connected(stations[10], stations[13]));
        assertFalse(partition.connected(stations[10], stations[1]));
        assertFalse(partition.connected(stations[10], stations[4]));
        assertFalse(partition.connected(stations[10], stations[5]));
        assertFalse(partition.connected(stations[10], stations[6]));
        assertFalse(partition.connected(stations[10], stations[11]));
        assertFalse(partition.connected(stations[10], stations[3]));
        assertFalse(partition.connected(stations[10], stations[9]));
        assertFalse(partition.connected(stations[10], stations[0]));
        assertFalse(partition.connected(stations[10], stations[12]));
        assertFalse(partition.connected(stations[10], stations[2]));
        assertFalse(partition.connected(stations[10], stations[14]));


        // Wassen
         assertTrue(partition.connected(stations[11], stations[9]));
         assertTrue(partition.connected(stations[11], stations[6]));
         assertTrue(partition.connected(stations[11], stations[13]));
        assertFalse(partition.connected(stations[11], stations[1]));
        assertFalse(partition.connected(stations[11], stations[4]));
        assertFalse(partition.connected(stations[11], stations[5]));
        assertFalse(partition.connected(stations[11], stations[7]));
        assertFalse(partition.connected(stations[11], stations[8]));
        assertFalse(partition.connected(stations[11], stations[3]));
        assertFalse(partition.connected(stations[11], stations[10]));
        assertFalse(partition.connected(stations[11], stations[0]));
        assertFalse(partition.connected(stations[11], stations[12]));
        assertFalse(partition.connected(stations[11], stations[2]));
        assertFalse(partition.connected(stations[11], stations[14]));



        // Yverdon
        assertFalse(partition.connected(stations[12], stations[0]));
        assertFalse(partition.connected(stations[12], stations[2]));
        assertFalse(partition.connected(stations[12], stations[3]));
        assertFalse(partition.connected(stations[12], stations[4]));
        assertFalse(partition.connected(stations[12], stations[5]));
        assertFalse(partition.connected(stations[12], stations[6]));
        assertFalse(partition.connected(stations[12], stations[7]));
        assertFalse(partition.connected(stations[12], stations[8]));
        assertFalse(partition.connected(stations[12], stations[9]));
        assertFalse(partition.connected(stations[12], stations[10]));
        assertFalse(partition.connected(stations[12], stations[11]));
        assertFalse(partition.connected(stations[12], stations[1]));
        assertFalse(partition.connected(stations[12], stations[13]));
        assertFalse(partition.connected(stations[12], stations[14]));


        // Zoug
         assertTrue(partition.connected(stations[13], stations[9]));
         assertTrue(partition.connected(stations[13], stations[6]));
         assertTrue(partition.connected(stations[13], stations[11]));
        assertFalse(partition.connected(stations[13], stations[1]));
        assertFalse(partition.connected(stations[13], stations[4]));
        assertFalse(partition.connected(stations[13], stations[5]));
        assertFalse(partition.connected(stations[13], stations[7]));
        assertFalse(partition.connected(stations[13], stations[8]));
        assertFalse(partition.connected(stations[13], stations[3]));
        assertFalse(partition.connected(stations[13], stations[10]));
        assertFalse(partition.connected(stations[13], stations[0]));
        assertFalse(partition.connected(stations[13], stations[12]));
        assertFalse(partition.connected(stations[13], stations[2]));
        assertFalse(partition.connected(stations[13], stations[14]));

        // Zürich
        assertFalse(partition.connected(stations[14], stations[0]));
        assertFalse(partition.connected(stations[14], stations[2]));
        assertFalse(partition.connected(stations[14], stations[3]));
        assertFalse(partition.connected(stations[14], stations[4]));
        assertFalse(partition.connected(stations[14], stations[5]));
        assertFalse(partition.connected(stations[14], stations[6]));
        assertFalse(partition.connected(stations[14], stations[7]));
        assertFalse(partition.connected(stations[14], stations[8]));
        assertFalse(partition.connected(stations[14], stations[9]));
        assertFalse(partition.connected(stations[14], stations[10]));
        assertFalse(partition.connected(stations[14], stations[11]));
        assertFalse(partition.connected(stations[14], stations[1]));
        assertFalse(partition.connected(stations[14], stations[13]));
        assertFalse(partition.connected(stations[14], stations[12]));


    }



}