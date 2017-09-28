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
import java.util.concurrent.ThreadLocalRandom;

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
        this.k = 0;
        this.n = 0;
        this.primary = null;
        this.secondary = null;
        this.isPrimary = false;
        this.isSecondary = false;
        this.player = null;
        this.playerList = new PlayerList();
        this.gameState = null;
    }

    public String getPrimary() throws RemoteException {
        return this.primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }


    public String getSecondary() {
        return this.secondary;
    }

    public void setSecondary(String secondaryID) {
        this.secondary = secondaryID;
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

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(int portNo, String playerID, GameInterface stub) {
        Player player = new Player(portNo, playerID, stub);
        this.player = player;
    }

    public PlayerList getPlayerList() {
        return this.playerList;
    }

    public void setPlayerList(PlayerList playerList) {
        this.playerList = playerList;
    }


    public void startGame() {
        if (this.isPrimary()) {
            this.gameState = new GameState(this.n, this.k);
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
                this.gameState.addPlayer(player.getplayerID());

                // If not primary server, update secondary server's game state
                if (!this.isPrimary()) {
                    GameInterface secondaryStub = this.playerList.getPlayer(this.secondary).getStub();
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

        if (this.isPrimary()) {
            this.gameState.addPlayer(this.player.getplayerID());
            return true;
        }

        // Get primary and secondary server ID from another player
        String[] servers = this.getServers();
        this.setPrimary(servers[0]);
        this.setSecondary(servers[1]);

        try {
            // Remote call join game method using primary stub
            GameInterface primaryStub = this.playerList.getPlayer(this.primary).getStub();
            this.gameState = primaryStub.joinGameServer(this.player);

            // If it is secondary server, update primary server with secondary server ID
            if (this.isSecondary) {
                primaryStub.setSecondary(this.player.getplayerID());
            }

            // If received game state from primary, the player joins the game successfully
            if (this.gameState == null) {

                return false;

            } else {

                if (this.isSecondary) {
                    System.err.println("Secondary server is ready");
                }

                return true;
            }

        } catch (Exception e) {
            System.err.println("Couldn't join the game: " + e.toString());
            e.printStackTrace();
            return false;
        }

    }

//    public void play() {
//        boolean playing = true;
//        Scanner reader = new Scanner(System.in);
//
//        while (playing) {
//
//            // Ask user for input
//            System.out.println("Please enter a command: ");
//            char command = reader.nextLine().charAt(0);
//            if (COMMANDS.contains(command)) {
//
//                // Send move request to the primary server
//                try {
//                    Registry registry = LocateRegistry.getRegistry(this.primary);
//                    GameInterface primaryStub = (GameInterface) registry.lookup("Primary");
//                    GameState gameState = primaryStub.makeMove(this.player.getUID(), command);
//                    this.setGameState(gameState);
//
//                } catch(Exception e) {
//                    System.err.println("Make move exception: " + e.toString());
//                    e.printStackTrace();
//                }
//
//                // If primary returns game state, update the player game state.
//                // Else the move is now allowed
//                if (gameState != null) {
//                    this.setGameState(gameState);
//                } else {
//                    System.out.println("The move request was declined");
//                }
//
//                // Quit the game
//                if (command == '9') {
//                    playing = false;
//                }
//
//                System.out.println(this.gameState.getGrid().toString());
//
//            } else {
//                System.out.println("Invalid command");
//            }
//
//
//        }
//    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }


    private void startPrimary() {
        if (this.isPrimary()) {
            this.startGame();
            System.out.println(this.gameState.toString());
            System.err.println("Primary server is ready");
        } else {
            System.err.println("This instance is not the primary server.");
        }
    }


    private String getRandomPlayer() {
        boolean done = false;
        String selectedID = "";
        while (!done) {
            // Get all player IDs from player list
            List<String> playerIDs = new ArrayList<>(this.playerList.getPlayers().keySet());

            // Randomly select a player from the player list
            int randInt = ThreadLocalRandom.current().nextInt(0, playerIDs.size());
            selectedID = playerIDs.get(randInt);

            // If the selected player is the player itself, continue
            if (! selectedID.equals(this.player.getplayerID())) {
                done = true;
            }
        }
        return selectedID;
    }

    private String[] getServers() {
        String[] servers = new String[2];
        String randomPlayerID = "";
        String primaryID = "";
        String secondaryID = "";
        boolean done = false;
        while (!done) {

            // Randomly select a player from the player list
            randomPlayerID = this.getRandomPlayer();

            // Try to get the primary server ID from the random player
            try {
                GameInterface stub = this.playerList.getPlayer(randomPlayerID).getStub();
                primaryID = stub.getPrimary();
                secondaryID = stub.getSecondary();
                done = true;
            } catch (Exception e) {
                System.err.println("Remote invocation exception: " + e.toString());
            }
        }

        servers[0] = primaryID;
        servers[1] = secondaryID;

        return servers;
    }


    public static void main(String[] args) {

        // check number of input args
        if (args.length < 3) {
            System.out.println("There should be 3 args. " +
                    "The proper usage is: " +
                    "java Game.java [IP-address][port-number][player-id]");
            System.exit(0);
        }

        // Get arguments
        String ip = args[0];
        int portNo = Integer.parseInt(args[1]);
        String playerID = args[2];


        // Set players info based on input arguments
        Game game = new Game();

        // Export the player stub
        GameInterface playerStub = null;
        try {
            playerStub = (GameInterface) UnicastRemoteObject.exportObject(game, 0);
        } catch (Exception e) {
            System.err.println("Export player stub exception: " + e.toString());
            e.printStackTrace();
        }

        // Set player field
        game.setPlayer(portNo, playerID, playerStub);

        // Send new player info to tracker, and get n, k and player list from the tracker
        try {
            Registry registry = LocateRegistry.getRegistry(ip, portNo);
            TrackerInterface trackerStub = (TrackerInterface) registry.lookup("Tracker");
            Map<String, Object> parametersPlayers = trackerStub.addPlayer(game.player);
            if (parametersPlayers == null) {
                System.err.println("Add new player failed");
                System.exit(1);
            }
            game.n = (int) parametersPlayers.get("N");
            game.k = (int) parametersPlayers.get("K");
            game.playerList = (PlayerList) parametersPlayers.get("PlayerList");
        } catch (Exception e) {
            System.err.println("Get registry exception: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        // If there is only one player, set it as primary server
        if (game.playerList.getSize() == 1) {
            game.setIsPrimary(true);
            game.setPrimary(game.getPlayer().getplayerID());
            game.startPrimary();
            System.out.println(game.getGameState().getGrid().toString());

        // If there are two players, set the second one as secondary server
        } else if (game.playerList.getSize() == 2) {
            game.setIsSecondary(true);
        }

        // Join game
        boolean hasJoined = game.joinGame();

        if (hasJoined) {
            System.out.println("Player has joined the game successfully");
            System.out.println(game.getGameState().toString());
            System.out.println(game.getGameState().getGrid().toString());
        }

        // Play
//        game.play();

    }

}
