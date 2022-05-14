package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.*;
import javafx.application.*;
import javafx.stage.*;

import static java.lang.Integer.*;

/**
 * This class contains the main program of the client
 * @author Noah Munz (310779)
 * @author Mehdi Bouguerra Ezzina (314857)
 */
public final class ClientMain extends Application {

        private final static String DEFAULT_PROXY_NAME = "localhost";
    final static int LOCALHOST_PORT = 5108;
    private final String proxyName;
    private final int port;

    /**
     * Primary Constructor of Client assigning ProxyName and Port values to it so that the start method can be called
     * without having to call the main method (because it can only be called once).
     * @param proxyName name of the server/proxy
     * @param port      PORT number
     */
    public ClientMain(String proxyName, String port) {
        this.proxyName = proxyName == null || proxyName.isBlank() ? DEFAULT_PROXY_NAME : proxyName;
        this.port = port == null || port.isBlank() ? LOCALHOST_PORT : parseInt(port);
    }

    /**
     * Secondary constructor of ClientNames that'll use the default values
     */
    public ClientMain() {this("", "");}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (proxyName == null || port == 0) new ClientMain().start(primaryStage);

        GraphicalPlayerAdapter gpa = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(gpa.setStage(primaryStage), proxyName, port);
        new Thread(client::run).start();
    }
}
