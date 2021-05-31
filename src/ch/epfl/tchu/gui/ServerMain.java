package ch.epfl.tchu.gui;

import ch.epfl.tchu.*;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.*;
import javafx.application.*;
import javafx.stage.*;

import java.net.*;
import java.util.*;

import static ch.epfl.tchu.game.PlayerId.*;
import static ch.epfl.tchu.gui.ClientMain.DEFAULT_PORT;
import static ch.epfl.tchu.gui.ConstantsGUI.*;
import static java.lang.Integer.parseInt;

/**
 * This class contains the main program of the server
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class ServerMain extends Application {
    private String[] args;

    /**
     * Primary Constructor of ServerMain assigning playerNames values to it so that the start method can be called
     * without having to call the main method (because it can only be called once).
     * @param player1Name Name of player1
     * @param player2Name Name of player2
     */
    public ServerMain(String player1Name, String player2Name, String port) {
        System.out.println("instance of ServerMain created");
        String checkedPlayer1Name = player1Name.isBlank() ? ADA : player1Name;
        String checkPlayer2Name = player2Name.isBlank() ? CHARLES : player2Name;
        String checkPort = port.isBlank() ? String.valueOf(DEFAULT_PORT) : port;
        this.args = new String[]{checkedPlayer1Name, checkPlayer2Name, checkPort};
    }

    /**
     * Secondary constructor of ServerMain that'll use the default names
     */
    public ServerMain() {this("", "", "");}

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (this.args.length == 0 || args[0].isBlank() && args[1].isBlank() || args[2].isBlank()) {
            ServerMain serverMain = new ServerMain();
            serverMain.start(primaryStage);
            return;
        }
        System.out.println("Start method Called");

        RemotePlayerProxy remotePlayer = new RemotePlayerProxy(new ServerSocket(parseInt(args[2])).accept());
        System.out.println("Server initialized");
        //By convention PLAYER_1 is hostPlayer and PLAYER_2 is remotePlayer
        new Thread(() -> {
            System.out.println("Game Started");
            Game.play(Map.of(PLAYER_1, new GraphicalPlayerAdapter().setStage(primaryStage), PLAYER_2, remotePlayer),
                      Map.of(PLAYER_1, args[0], PLAYER_2, args[1]),
                      SortedBag.of(ChMap.tickets()), new Random());
        }).start();

    }

    /**
     * useful when Main method is called statically without defining explicitely playerNames with the construction of an instance of this
     */
   /* private void initForMain() {
        List<String> args = getParameters().getRaw().isEmpty() ? List.of(ADA, CHARLES)
                                                               : getParameters().getRaw();
        if (args.size() < 2) args.add(CHARLES);
        this.args = new String[]{args.get(0), args.get(1)};
    }*/
}
