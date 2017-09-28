package game;

import player.Player;
import player.PlayerList;
import tracker.TrackerInterface;
import tracker.TrackerServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by chuanyu on 19/9/17.
 */
public class Game implements GameInterface {

    static private List<Character> COMMANDS = new ArrayList<Character>(Arrays.asList('0', '1', '2', '3', '4', '9'));

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
        this.k = 3;
        this.n = 5;
        this.primary = null;
        this.secondary = null;
        this.isPrimary = false;
        this.isSecondary = false;
        this.player = new Player("", "", "");
        this.playerList = new PlayerList();
        this.gameState = new GameState(this.n, this.k);
    }

    public String getPrimary() throws RemoteException {
        return this.primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }


    public String getSecondary() throws RemoteException {
        return this.secondary;
    }

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

    public GameState joinGameServer(Player player) {
        if (this.isPrimary()) {
            try {
                // Add a player to the player list
                this.playerList.addPlayer(player);
                System.out.println(this.playerList.toString());

                // Add the player to the game state
                this.gameState.addPlayer(player.getUID());

                // If not primary server, update secondary server's game state
                if (!this.isPrimary()) {
                    Registry registry = LocateRegistry.getRegistry(this.getSecondary());
                    GameInterface secondaryStub = (GameInterface) registry.lookup("Secondary");
                    secondaryStub.setGameState(this.gameState);
                }

                return this.gameState;

            } catch (Exception e) {
                System.err.println("Couldn't join the game: " + e.toString());
                e.printStackTrace();
                return null;
            }


        } else {
            System.err.println("Can only join game via the primary server");
            return null;
        }
    }

    public GameState makeMove(String uid, char command) {
        if (this.isPrimary()) {
            this.gameState.move(uid, command);
        }
        return this.gameState;
    }


    public boolean joinGame() {
        try {

            // Contact primary to join the game. Primary returns game state
            Registry registry = LocateRegistry.getRegistry(this.getPrimary());
            GameInterface primaryStub = (GameInterface) registry.lookup("Primary");
            GameState gameState = primaryStub.joinGameServer(this.player);

            // If successful, update player's game state. Otherwise return false
            if (gameState == null) {
                return false;
            } else {
                this.setGameState(gameState);
                System.out.println(this.gameState.getGrid().toString());
                return true;
            }


        } catch(Exception e) {
            System.err.println("Couldn't join the game: " + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public void play() {
        boolean playing = true;
        Scanner reader = new Scanner(System.in);

        while (playing) {

            // Ask user for input
            System.out.println("Please enter a command: ");
            char command = reader.nextLine().charAt(0);
            if (COMMANDS.contains(command)) {

                // Send move request to the primary server
                try {
                    Registry registry = LocateRegistry.getRegistry(this.primary);
                    GameInterface primaryStub = (GameInterface) registry.lookup("Primary");
                    GameState gameState = primaryStub.makeMove(this.player.getUID(), command);
                    this.setGameState(gameState);

                } catch(Exception e) {
                    System.err.println("Make move exception: " + e.toString());
                    e.printStackTrace();
                }

                // If primary returns game state, update the player game state.
                // Else the move is now allowed
                if (gameState != null) {
                    this.setGameState(gameState);
                } else {
                    System.out.println("The move request was declined");
                }

                // Quit the game
                if (command == '9') {
                    playing = false;
                }

                System.out.println(this.gameState.getGrid().toString());

            } else {
                System.out.println("Invalid command");
            }


        }
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }


    private void startPrimary() {
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

            System.out.println(this.gameState.toString());

        } else {
            System.err.println("This instance is not the primary server.");
        }
    }

    private void startSecondary() {
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
            System.out.println("There should be 4 args. " +
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
        game.setPrimary(null);
        game.setSecondary(null);

        // Get player list and parameters from the tracker
        try {
            Registry registry = LocateRegistry.getRegistry(ip, Integer.parseInt(portNo));
            TrackerInterface trackerStub = (TrackerInterface) registry.lookup("Tracker");
            Map<String, Object> parametersPlayers = trackerStub.returnParametersPlayers();
            PlayerList playerList = (PlayerList) parametersPlayers.get("PlayerList");
            int n = (int) parametersPlayers.get("N");
            int k = (int) parametersPlayers.get("K");
            game.playerList = playerList;
            game.n = n;
            game.k = k;
        } catch (Exception e) {
            System.err.println("Not able to contact the tracker" + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

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
            System.out.println(game.getGameState().toString());
        }

        // Play
        game.play();

    }

}
