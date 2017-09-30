package game;

import player.Player;
import player.PlayerList;
import tracker.TrackerInterface;
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
    private Player player;
    private PlayerList playerList;
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

    /**
     * Local method
     * This method is used to contact tracker before joining the game. Each player / server export a
     * their stub and pass it to the tracker. The tracker add the player / server to the player list,
     * and return n, k and player list which includes each player / server's stub.
     *
     * @param trackerStub     Tracker stub.
     */
    public void contactTracker(TrackerInterface trackerStub) {
        try {

            // Add the new player to tracker player list, and get player list and paramters
            // from tracker

            //Map<String, Object> parametersPlayers = trackerStub.addPlayer(this.player);
            Map<String, Object> parametersPlayers = trackerStub.returnParametersPlayers();

            // Update local n, k and player list
            this.n = (int) parametersPlayers.get("N");
            this.k = (int) parametersPlayers.get("K");
            this.playerList = (PlayerList) parametersPlayers.get("PlayerList");

        } catch (Exception e) {
            System.err.println("Get registry exception: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Local method
     * This method is used by player to join the game. The player first asks a random player
     * for the server IDs. The it finds the primary server stub from its own player list and
     * remote calls primary server's method to actually join the game.
     *
     * @return boolean. True indicates success, false indicates failure.
     */
    public boolean joinGame(TrackerInterface trackerStub) {

        // If it is the primary server, add itself to its own player list and
        // asks the tracker to add it the tracker player list
        if (this.isPrimary()) {
            this.gameState.addPlayer(this.player);
            try {
                trackerStub.addPlayer(this.player);
            } catch (Exception e) {
                System.err.println("Updating tracker player list exception: " + e.toString());
                e.printStackTrace();
            }
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

                // Add the new player to the tracker player list only after the player
                // is added to the primary server
                trackerStub.addPlayer(this.player);

                return true;
            }

        } catch (Exception e) {
            System.err.println("Couldn't join the game: " + e.toString());
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Local method.
     * This method is used to start the primary server.
     */
    private void startPrimary() {
        if (this.isPrimary()) {
            this.startGame();
            System.out.println(this.gameState.toString());
            System.err.println("Primary server is ready");
        } else {
            System.err.println("This instance is not the primary server.");
        }
    }

    public void ping() {}

    public void pingServer() {



        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GameInterface stub = null;
                if (Game.this.isPrimary()) {
                    stub = Game.this.getGameState().getStateByPlayerID(Game.this.secondary).getStub();
                } else if (Game.this.isSecondary()) {
                    stub = Game.this.getGameState().getStateByPlayerID(Game.this.primary).getStub();
                }
                try {
                    stub.ping();
                    System.out.println("ping...");
                } catch (Exception e) {
                    System.err.println("Ping exception: " + e.toString());
                    e.printStackTrace();
                }
            }
        }, 500, 500);
    }

    /**
     * Remote method
     * This method is used by the primary server to join a player to the game. The primary
     * server adds the new player to its player list and game state. A random position
     * (i, j coordinates of the grid) is given to the new player.
     *
     * @param player Player. The new player object.
     * @return GameState object. This GameState object is returned to the new player to update its local game state.
     */
    public GameState joinGameServer(Player player) {
        if (this.isPrimary()) {
            try {
//                // Add a player to the player list
//                this.playerList.addPlayer(player);
//                System.out.println(this.playerList.toString());

                // Add the player to the game state
                this.gameState.addPlayer(player);

//                // If not primary server, update secondary server's game state
//                if (!this.isPrimary()) {
//                    GameInterface secondaryStub = this.playerList.getPlayer(this.secondary).getStub();
//                    secondaryStub.setGameState(this.gameState);
//                }
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

    /**
     * Local method
     * This method is used by the primary server to start a new game. The primary server
     * creates a n by n grid and creates k treasures on the grid.
     */
    private void startGame() {
        if (this.isPrimary()) {
            this.gameState = new GameState(this.n, this.k);
            this.gameState.createKTreasures();
        } else {
            System.err.println("Only primary server can start a new game");
        }
    }

    /**
     * Local Method.
     * THis method is used by players to play game. It waits for command and send it to the primary
     * server.
     */
    private void play() {
        boolean playing = true;
        Scanner reader = new Scanner(System.in);

        while (playing) {

            // Ask user for input
            System.out.println("Please enter a command: ");
            char command = reader.nextLine().charAt(0);
            if (COMMANDS.contains(command)) {

                // Get primary stub
                GameInterface primaryStub = this.gameState.getStates().get(this.primary).getStub();

                // Send move request to the primary server
                GameState gameState = null;
                try {
                    gameState = primaryStub.makeMove(this.player.getplayerID(), command);
                } catch (Exception e) {
                    System.err.println("Make move exception: " + e.toString());
                    e.printStackTrace();
                }

                // If primary returns game state, update the player game state.
                // Else the move is not allowed
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

    /**
     * Remote method.
     * Players remote call this primary server method to make moves during the game
     *
     * @param playerID String.
     * @param command  char. Game instruction.
     * @return GameState. THe updated game state.
     */
    public GameState makeMove(String playerID, char command) {
        if (this.isPrimary()) {
            this.gameState.move(playerID, command);
        }
        return this.gameState;
    }

    /**
     * Helper method.
     * This method is used to get a random player from a player list.
     *
     * @return String. player ID.
     */
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
            if (!selectedID.equals(this.player.getplayerID())) {
                done = true;
            }
        }
        return selectedID;
    }

    /**
     * Helper method.
     * This method is used to get the primary and secondary server ID from a random player.
     *
     * @return String[]. An array of size 2 containing the primary server ID and secondary server ID.
     */
    private String[] getServers() {
        String[] servers = new String[2];
        String randomPlayerID;
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

    /**
     * Remote method.
     * Get primary server player ID.
     *
     * @return String. Primary server player ID.
     */
    public String getPrimary() {
        return this.primary;
    }

    /**
     * Remote method.
     * Get secondary server player ID
     *
     * @return String. Secondary server player ID.
     */
    public String getSecondary() {
        return this.secondary;
    }

    /**
     * Local method.
     * Check if a player is the primary server
     *
     * @return boolean. True indicates is primary server.
     */
    public boolean isPrimary() {
        return this.isPrimary;
    }

    /**
     * Local method.
     * Set primary server player ID.
     *
     * @param primary
     */
    public void setPrimary(String primary) {
        this.primary = primary;
    }

    /**
     * Local method.
     * Set a player to be the primary server.
     *
     * @param isPrimary boolean.
     */
    public void setIsPrimary(boolean isPrimary) {
        if (isPrimary) {
            this.isPrimary = true;
        } else {
            this.isSecondary = false;
        }
    }

    /**
     * Local method.
     * Check if a player is the secondary server.
     *
     * @return boolean. True indicates is secondary server.
     */
    public boolean isSecondary() {
        return this.isSecondary;
    }

    /**
     * Local method.
     * Set secondary server player ID.
     *
     * @param secondaryID String. Secondary server player ID.
     */
    public void setSecondary(String secondaryID) {
        this.secondary = secondaryID;
    }

    /**
     * Local method.
     * Set a player to be the secondary server.
     *
     * @param isSecondary boolean.
     */
    public void setIsSecondary(boolean isSecondary) {
        if (isSecondary) {
            this.isSecondary = true;
        } else {
            this.isSecondary = false;
        }
    }

    /**
     * Local method.
     * Get player's details.
     *
     * @return Player.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Local method.
     * Set player's details.
     *
     * @param playerID String. Player ID.
     * @param stub     GameInterface. Player's stub.
     */
    public void setPlayer(String playerID, GameInterface stub) {
        Player player = new Player(playerID, stub);
        this.player = player;
    }

    /**
     * Local method.
     * Get local player list.
     *
     * @return PlayerList.
     */
    public PlayerList getPlayerList() {
        return this.playerList;
    }

    /**
     * Local method.
     * Set local player list.
     *
     * @param playerList PlayerList.
     */
    public void setPlayerList(PlayerList playerList) {
        this.playerList = playerList;
    }

    /**
     * Remote method.
     * Get game state.
     *
     * @return GameState.
     */
    public GameState getGameState() {
        return this.gameState;
    }

    /**
     * Local method.
     * Set local game state.
     *
     * @param gameState GameState
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
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

        // Get tracker stub
        TrackerInterface trackerStub = null;
        try {
            Registry registry = LocateRegistry.getRegistry(ip, portNo);
            trackerStub = (TrackerInterface) registry.lookup("Tracker");
        } catch (Exception e) {
            System.err.println("Getting tracker exception: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

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
        game.setPlayer(playerID, playerStub);

        // Add the player to tracker, and update local n, k and player list
        game.contactTracker(trackerStub);

        // If there is only one player, set it as primary server
        if (game.playerList.getSize() == 0) {
            game.setIsPrimary(true);
            game.setPrimary(game.getPlayer().getplayerID());
            System.out.println(game.getPrimary());
            game.startPrimary();
            System.out.println(game.getGameState().getGrid().toString());

            // If there are two players, set the second one as secondary server
        } else if (game.playerList.getSize() == 1) {
            game.setIsSecondary(true);
            game.setSecondary(game.getPlayer().getplayerID());
        }

        System.out.println(game.getSecondary());

        // Join game
        boolean hasJoined = game.joinGame(trackerStub);

        if (hasJoined) {
            if (game.isSecondary()) {
                game.pingServer();
            }
            System.out.println("Player has joined the game successfully");
            System.out.println(game.getGameState().toString());
            System.out.println(game.getGameState().getGrid().toString());
        }

        // Play
        game.play();

    }

}
