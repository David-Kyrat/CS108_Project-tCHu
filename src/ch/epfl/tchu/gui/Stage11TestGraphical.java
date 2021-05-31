package ch.epfl.tchu.gui;

import ch.epfl.tchu.*;
import ch.epfl.tchu.game.*;
import javafx.application.*;
import javafx.stage.*;

import java.util.*;

import static ch.epfl.tchu.game.PlayerId.*;

public final class Stage11TestGraphical extends Application {
    public static void main(String[] args) { launch(args); }

    private String[] args;
    public void startWithArgs(Stage primaryStage, String[] givenArgs) {
        args = givenArgs;
        start(primaryStage);
    }
    @Override
    public void start(Stage primaryStage) {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        Map<PlayerId, String> names = Map.of(PLAYER_1, args[0], PLAYER_2, args[1]);
        GraphicalPlayerAdapter gpa = new GraphicalPlayerAdapter();
        Map<PlayerId, Player> players = Map.of(PLAYER_1, gpa,
                                               PLAYER_2, gpa);
        Random rng = new Random();
        new Thread(() -> Game.play(players, names, tickets, rng)).start();
    }
}
