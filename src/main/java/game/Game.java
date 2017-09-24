package game;

import player.Player;
import player.PlayerList;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by chuanyu on 19/9/17.
 */
public class Game implements GameInterface {

    private String primary;
    private String secondary;
    private boolean isPrimary;
    private boolean isSecondary;
    private PlayerList playerList;
    private Player player;
    private int k;
    private int n;
    private GameState gameState;

    public Game() {
        this.k = 30;
        this.n = 20;
        this.primary = null;
        this.secondary = null;
        this.isPrimary = false;
        this.isSecondary = false;
        this.player = new Player("", "", "");
        this.playerList = new PlayerList();
        this.gameState = new GameState(this.n, this.k);
    }

    public String getPrimary() throws RemoteException { return this.primary; }

    public void setPrimary(String primary) {
        this.primary = primary;
    }


    public String getSecondary() throws RemoteException {return this.secondary; }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public boolean isPrimary() {
        return this.isPrimary;
    }

    public boolean isSecondary() {
        return this.isSecondary;
    }

    public void setIsPrimary(boolean isPrimary) {
        if (isPrimary) {
            this.isPrimary = true;
        } else {
            this.isSecondary = false;
        }
    }

    public void setIsSecondary(boolean isSecondary) {
        if (isSecondary) {
            this.isSecondary = true;
        } else {
            this.isSecondary = false;
        }
    }

    public void setPlayer(String ip, String portNo, String playerID) {
        Player player = new Player(ip, portNo, playerID);
        this.player = player;
    }

    public void startGame() {
        if (this.isPrimary()) {
            this.gameState.createKTreuarues();
        } else {
            System.err.println("Only primary server can start a new game");
        }
    }

    public boolean joinGameServer(Player player) {
        if (this.isPrimary()) {
            try {

                // Add a player to the player list
                this.playerList.addPlayer(player);
                System.out.println(this.playerList.toString());

                // Add the player to the game state
                this.gameState.updateScore(player.getUID(), 0);

                Registry registry = LocateRegistry.getRegistry();
                GameInterface secondaryStub = (GameInterface) registry.lookup("Secondary");
                secondaryStub.
                return true;
            } catch (Exception e) {
                System.err.println("Couldn't join the game: " + e.toString());
                e.printStackTrace();
                return false;
            }
        } else {
            System.err.println("Can only join game via the primary server");
        }
    }


    public boolean joinGame() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            GameInterface primaryStub = (GameInterface) registry.lookup("Primary");
            return primaryStub.joinGameServer(this.player);
        } catch(Exception e) {
            System.err.println("Couldn't join the game: " + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }


    public void startPrimary() {
        if (this.isPrimary()) {
            Registry registry = null;
            GameInterface stub = null;

            try {
                stub = (GameInterface) UnicastRemoteObject.exportObject(this, 0);
                registry = LocateRegistry.getRegistry();
                registry.bind("Primary", stub);
                System.err.println("Primary server is ready");
            } catch(Exception e) {
                try {
                    registry.unbind("Primary");
                    registry.bind("Primary", stub);
                    System.err.println("Primary server is ready");
                } catch(Exception ee) {
                    System.err.println("Primary server exception: " + ee.toString());
                    ee.printStackTrace();
                }
            }
            this.startGame();
        } else {
            System.err.println("This instance is not the primary server.");
        }
    }

    public void startSecondary() {
        if (this.isSecondary()) {
            Registry registry = null;
            GameInterface stub = null;

            // Bind the secondary server to RMI registry
            try {
                stub = (GameInterface) UnicastRemoteObject.exportObject(this, 0);
                registry = LocateRegistry.getRegistry();
                registry.bind("Secondary", stub);
                System.err.println("Secondary server is ready");
            } catch(Exception e) {
                try {
                    registry.unbind("Secondary");
                    registry.bind("Secondary", stub);
                    System.err.println("Secondary server is ready");
                } catch(Exception ee) {
                    System.err.println("Secondary server exception: " + ee.toString());
                    ee.printStackTrace();
                }
            }

            // Initiate the secondary server game state by syncing with the primary server
            try {
                registry = LocateRegistry.getRegistry(this.primary);
                stub = (GameInterface) registry.lookup("Primary");
                GameState primaryGameState = stub.getGameState();
                this.setGameState(primaryGameState);
            } catch (Exception e) {
                System.err.println("Secondary server exception: " + e.toString());
                e.printStackTrace();
            }
        } else {
            System.err.println("This instance is not the secondary server");
        }
    }


    public static void main(String[] args) {

        // check number of input args
        if (args.length < 4) {
            System.out.println("There should be 6 args. " +
                    "The proper usage is: " +
                    "java Game.java [IP-address][port-number][player-id][player-type]");
            System.exit(0);
        }

        // Get arguments
        String ip = args[0];
        String portNo = args[1];
        String playerID = args[2];
        String playerType = args[3];

        // Set players info based on input arguments
        Game game = new Game();
        game.setPlayer(ip, portNo, playerID);

        // Start servers
        if (playerType.equals("1")) {
            game.setIsPrimary(true);
            game.startPrimary();
            System.out.println("Primary grid:");
            System.out.println(game.getGameState().getGrid().toString());
        } else if (playerType.equals("2")) {
            game.setIsSecondary(true);
            game.startSecondary();
            System.out.println("Secondary grid:");
            System.out.println(game.getGameState().getGrid().toString());
        }

        // Join game
        boolean hasJoined = game.joinGame();
        if (hasJoined) {
            System.out.println("Player has joined the game successfully");
        }

    }

}
