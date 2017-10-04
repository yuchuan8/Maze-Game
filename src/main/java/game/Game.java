package game;

import player.Player;
import player.PlayerList;
import tracker.TrackerInterface;
import gui.BaseDesktop;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.spec.ECField;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by chuanyu on 19/9/17.
 */
public class Game implements GameInterface {

    static private List<Character> COMMANDS = new ArrayList<Character>(Arrays.asList('0', '1', '2', '3', '4', '9', 'p'));

    private String primary;
    private String secondary;
    private boolean isPrimary;
    private boolean isSecondary;
    private Player player;
    private PlayerList playerList;
    private int k;
    private int n;
    private GameState gameState;
    private int version;
    private TrackerInterface trackerStub;


    public Game() {
        this.k = 0;
        this.n = 0;
        this.primary = null;
        this.secondary = null;
        this.isPrimary = false;
        this.isSecondary = false;
        this.player = null;
        this.playerList = new PlayerList();
        this.gameState = new GameState(15,15);
        this.version = 0;
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
            trackerStub.addPlayer(this.player);
            Map<String, Object> parametersPlayers = trackerStub.returnParametersPlayers();

            // Update local n, k and player list
            this.n = (int) parametersPlayers.get("N");
            this.k = (int) parametersPlayers.get("K");
            this.playerList = (PlayerList) parametersPlayers.get("PlayerList");
            initialGameStateFromPlayerList(this.playerList); // xyx
        } catch (Exception e) {
            System.err.println("Get registry exception: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
    //xyx
    private void initialGameStateFromPlayerList(PlayerList playerlist){
        Map<String, Player> players = playerlist.getPlayers();
        if(players != null) {
            for (String key : players.keySet()) {
                this.gameState.addPlayer(players.get(key));
            }
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
            return true;
        }

        // Get primary and secondary server ID from another player
        String[] servers = this.getServers();
        this.setPrimary(servers[0]);

        if (!this.isSecondary()) {
            this.setSecondary(servers[1]);
        }

        try {
            // Remote call join game method using primary stub
            GameInterface primaryStub = this.playerList.getPlayer(this.primary).getStub();
            this.gameState = primaryStub.joinGameServer(this.player);

            // If it is secondary server, update primary server with secondary server ID
//            if (this.isSecondary()) {
//                primaryStub.setSecondary(this.player.getplayerID());
//            }

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
            playerDealWithPrimaryDown();//xyx
            try{
                GameInterface primaryStub = this.playerList.getPlayer(this.primary).getStub();
                this.gameState = primaryStub.joinGameServer(this.player);
                trackerStub.addPlayer(this.player);
                return true;
            }catch (Exception ee){
                System.err.println("Couldn't join the game: " + e.toString());
                e.printStackTrace();
                return false;
            }

        }
    }

    /**
     * Local method.
     * This method is used to start the primary server.
     */
    public void startPrimary() {
        this.setIsPrimary(true);
        this.setIsSecondary(false);
        this.setPrimary(this.getPlayer().getplayerID());
        this.pingServer();
        //System.out.println(this.gameState.toString());
        System.err.println("Primary server is ready");
    }


    public void startSecondary() {
        this.setIsPrimary(false);
        this.setIsSecondary(true);
        this.setSecondary(this.getPlayer().getplayerID());
        this.pingServer();
    }

    public void ping() {}

    public void pingServer() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

//                System.out.println("scheduling ...");

                String monitoredServer = "";
                // Get server stub
                GameInterface serverStub = null;
                if (Game.this.isPrimary()) {
                    monitoredServer = "Secondary";
                    State state = Game.this.getGameState().getStateByPlayerID(Game.this.secondary);
                    if (state != null) {
                        serverStub = state.getStub();
                    }
                } else if (Game.this.isSecondary()) {
                    monitoredServer = "Primary";
                    State state = Game.this.getGameState().getStateByPlayerID(Game.this.primary);
                    if (state != null) {
                        serverStub = state.getStub();
                    }
                }

                // If server stub is not null, start pinging.
                if (serverStub != null) {
                    try {
                        serverStub.ping();
//                        System.out.println("ping " + monitoredServer);
                    } catch (Exception e) {
                        timer.cancel();
                        Game.this.recover(monitoredServer);
                        Game.this.pingServer();
                        System.err.println("Ping exception: " + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        }, 500, 500);
    }

    private void recover(String serverType) {

        // Recover the primary server
        if (serverType == "Primary") {

            // Remove the old primary server from the game state
            this.gameState.removePlayer(this.primary);

            // Remove the old primary server from the tracker
            try {
                this.trackerStub.removePlayer(this.primary);
            } catch (Exception e) {
                
            }

            // Set the secondary server to be the primary server
            this.setIsPrimary(true);
            this.setIsSecondary(false);
            this.setPrimary(this.player.getplayerID());
            this.setSecondary(null);

        } else if (serverType == "Secondary") {

            // Remove the old secondary server from game state
            this.gameState.removePlayer(this.secondary);
            this.setSecondary(null);

            // Remove the old secondary from the tracker
            try {
                this.trackerStub.removePlayer(this.secondary);
            } catch (Exception e) {

            }

        }

        this.version += 1;
//        System.out.println("after recover, version is "+ this.version);

        boolean done = false;

        // If there is only one player in the game state, do not try to find a secondary server
        if (this.gameState.getStates().size() == 1) {
            return;
        }

        while (!done) {
            // Randomly select a server from game state
            String selectedID = this.getRandomPlayerFromGameState();
            GameInterface selectedStub = this.gameState.getStateByPlayerID(selectedID).getStub();

            try {
                // Sync game state
                selectedStub.setGameState(this.gameState);

                // Set selected player as secondary server
                selectedStub.setIsSecondary(true);
                this.setSecondary(selectedStub.getPlayer().getplayerID());

                // Sync version
                selectedStub.setVersion(this.version);

                // Start pinging the new secondary server
                this.pingServer();

                // Update the new secondary server's primary and secondary server ID
                selectedStub.setPrimary(this.getPlayer().getplayerID());
                selectedStub.setSecondary(selectedStub.getPlayer().getplayerID());

                // Start pinging the new primary server
                selectedStub.pingServer();

                done = true;

            } catch (Exception e) {
                System.err.println("Syncing game state error: " + e.toString());
                e.printStackTrace();
            }

        }

        System.out.println("primary: " + this.primary);
        System.out.println("secondary: " + this.secondary);


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
                // Add the player to the game state
                this.gameState.addPlayer(player);

                if (this.secondary == null) {
                    this.secondary = player.getplayerID();
                    GameInterface playerStub = player.getStub();
                    try {
                        playerStub.startSecondary();
                    } catch (Exception e) {
                        System.err.println("Starting secondary exception: " + e.toString());
                    }
                }

                // Sync the game state with the secondary server
                if (this.secondary != null) {
                    this.updateSecondaryGameState();
                }

                this.trackerStub.print(this.gameState.toString());
                System.out.println(this.gameState.toString());
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

        BaseDesktop baseDesktop = new BaseDesktop(this.player, this.gameState);
//        baseDesktop.DisplayGrid();
//        baseDesktop.DisplayScores();

        boolean playing = true;
        Scanner reader = new Scanner(System.in);

//        BaseDesktop gui = new BaseDesktop(this.player, this.gameState);
//        gui.DisplayGrid();

        while (playing) {

            // Ask user for input
            System.out.println("Please enter a command: ");
            char command = reader.nextLine().charAt(0);
            if (COMMANDS.contains(command)) {

                // Send move request to the primary server. If primary server is not reachable
                // deal with exception and then keep trying to move
                Map<String, Object> primaryReturn = null;

                boolean done = false;
                while (!done) {
                    // Get primary stub
                    GameInterface primaryStub = this.gameState.getStates().get(this.primary).getStub();
                    try {
                        primaryReturn = primaryStub.makeMove(this.player.getplayerID(), command);
                        done = true;
                    } catch (Exception e) {
                        playerDealWithPrimaryDown();//xyx
                        System.err.println("Make move exception: " + e.toString());
                        e.printStackTrace();
                    }
                }

                // If primary returns not null, update the player game state and server details if neccessary.
                // Else the move is not allowed
                if (primaryReturn != null) {
                    GameState gameState = (GameState) primaryReturn.get("gameState");
                    this.setGameState(gameState);
                    int primaryVersion = (int) primaryReturn.get("version");
                    if (primaryVersion != this.version) {
                        this.version = primaryVersion;
                        this.primary = (String) primaryReturn.get("primary");
                        this.secondary = (String) primaryReturn.get("secondary");
                    }
                    baseDesktop.refresh(this.gameState);
                } else {
                    System.out.println("The move request was declined");
                }

                // Quit the game
                if (command == '9') {
                    this.exitGame();
                    playing = false;
                }

                if (command == 'p') {
                    System.out.println("Is primary: " + this.isPrimary());
                    System.out.println("Is secondary: " + this.isSecondary());
                    System.out.println("Primary is: " + this.primary);
                    System.out.println("Secondary is: " + this.secondary);
                }

                System.out.println(this.gameState.getGrid().toString());

            } else {
                System.out.println("Invalid command");
            }


        }
    }

    public void exitGame() {
        boolean done = false;
        while (!done) {
            GameInterface primaryStub = this.gameState.getStateByPlayerID(this.primary).getStub();
            try {
                primaryStub.exitGameServer(this.player.getplayerID());
                done = true;
            } catch (Exception e) {
                this.playerDealWithPrimaryDown();
                System.err.println("Contact primary exception: " + e.toString());
                e.printStackTrace();
            }
        }

    }

    public boolean exitGameServer(String playerID) {
        if (!this.isPrimary()) {
            return false;
        }

        boolean trackerUpdated = false;
        try {
            this.trackerStub.removePlayer(playerID);
            trackerUpdated = true;
        } catch (Exception e) {
            System.err.println("Contact tracker error: " + e.toString());
        }

        if (trackerUpdated) {
            System.out.println("Now the game state is:");
            System.out.println(this.gameState.toString());
            System.out.println("playerID: " + playerID);
            this.gameState.removePlayer(playerID);
            if (this.secondary != null) {
                this.updateSecondaryGameState();
            }
            System.out.println(this.gameState.toString());
            return true;
        }

        return false;
    }

    /**
     * Remote method.
     * Players remote call this primary server method to make moves during the game
     *
     * @param playerID String.
     * @param command  char. Game instruction.
     * @return GameState. THe updated game state.
     */
    public synchronized Map<String, Object> makeMove(String playerID, char command) {
        if (this.isPrimary()) {

            // Make move
            this.gameState.move(playerID, command);

            // If secondary is not null, sync the game state over
            if (this.secondary != null) {
                this.updateSecondaryGameState();
            }
        }

        Map<String, Object> primaryReturn = new HashMap<>();
        primaryReturn.put("version", this.version);
        primaryReturn.put("primary", this.primary);
        primaryReturn.put("secondary", this.secondary);
        primaryReturn.put("gameState", this.gameState);
        return primaryReturn;
    }

    private void updateSecondaryGameState() {

        if (!this.isPrimary()) {
            return;
        }

        System.out.println("");
        GameInterface secondaryStub = this.gameState.getStateByPlayerID(this.secondary).getStub();
        try {
            secondaryStub.setGameState(this.gameState);
        } catch (Exception e) {
            System.err.println("Make move exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private String getRandomPlayerFromGameState() {
        Random generator = new Random();
        Object[] playerIDs = this.getGameState().getStates().keySet().toArray();
        System.out.println("playerIDs length: " + playerIDs.length);

        boolean done = false;
        String selectedID = "";

        while (!done) {
            if (playerIDs.length == 1) {
                selectedID = (String) playerIDs[0];
            } else {
                selectedID = (String) playerIDs[generator.nextInt(playerIDs.length)];
            }
            if (!selectedID.equals(this.player.getplayerID())) {
                done = true;
            }
        }

        return selectedID;
    }

    /**
     * Helper method.
     * This method is used to get a random player from a player list.
     *
     * @return String. player ID.
     */
    private String getRandomPlayerFromPlayerList() {
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
            randomPlayerID = this.getRandomPlayerFromPlayerList();

            // Try to get the primary server ID from the random player
            try {
                GameInterface stub = this.playerList.getPlayer(randomPlayerID).getStub();
                primaryID = stub.getPrimary();
                secondaryID = stub.getSecondary();
                this.gameState = stub.getGameState();
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
     * Remote method.
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

    private PrimaryUpdate generatePrimaryUpdate(){
        State primaryState = this.gameState.getStateByPlayerID(this.primary);
        State secondryState = this.gameState.getStateByPlayerID(this.secondary);
        Player localPrimary = new Player(this.primary,primaryState.getStub());
        Player localSecondry = null;
        if(secondryState != null){
            localSecondry = new Player(this.secondary,secondryState.getStub());
        }
        PrimaryUpdate updateInformation = new PrimaryUpdate(this.version,localPrimary,localSecondry);
        return updateInformation;
    }
    @Override
    public PrimaryUpdate gossipPullUpdatePrimary() throws RemoteException {
        PrimaryUpdate updateInformation = generatePrimaryUpdate();
        return updateInformation;
    }

    @Override
    public void gossipPushUpdatePrimary(PrimaryUpdate updateInformation) throws RemoteException {
        int updateVersion = updateInformation.getVersion();
        if(updateVersion > this.version){
            Player primary = updateInformation.getPrimary();
            Player secondry = updateInformation.getSecondry();
            this.primary = primary.getplayerID();
            this.secondary = secondry.getplayerID();
            this.version = updateVersion;
            this.gameState.addPlayer(primary);
            this.gameState.addPlayer(secondry);
        }
    }

    @Override
    public void dealWithUnactivePlayer(String PlayerID) throws RemoteException {
        if(this.isPrimary){
            primaryDealWithUnactivePlayer(PlayerID);
        }else if(this.isSecondary){
            secondaryDealWithUnactivePlayer(PlayerID);
        }else{
            playerDealWithUnactivePlayer(PlayerID);
        }

    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    private  void playerDealWithPrimaryDown() {
        if (!(isPrimary | isSecondary)) {//this player is not primary or secondary, just a player
            boolean keepContactSecondary = true;
            while (keepContactSecondary) {
                //first ask secondary for the new primary adress
                State secondary = this.gameState.getStateByPlayerID(this.secondary);
                if(secondary != null) {
                    GameInterface secondaryStub = secondary.getStub();
                    try {
                        System.out.println("contacting secondary");
                        PrimaryUpdate updateInformation = secondaryStub.gossipPullUpdatePrimary();
                        int updateVersion = updateInformation.getVersion();
                        System.out.println("asking secondary: version : " + updateVersion);
                        if (updateVersion > this.version) {
                            Player primary = updateInformation.getPrimary();
                            Player secondry = updateInformation.getSecondry();
                            this.primary = updateInformation.getPrimary().getplayerID();
                            this.secondary = updateInformation.getSecondry().getplayerID();
                            this.version = updateVersion;
                            this.gameState.addPlayer(primary);
                            this.gameState.addPlayer(secondry);
                        }
                        try {
                            State primaryState = this.gameState.getStateByPlayerID(this.primary);
                            GameInterface primaryStub = primaryState.getStub();
                            primaryStub.ping();
                            keepContactSecondary = false;
                        } catch (Exception e) {
                            keepContactSecondary = true;
                        }
                    } catch (Exception e) {
                        playerDealWithSecondaryDown();
                        keepContactSecondary = false;
                    }
                }else{
                    keepContactSecondary = false;
                    playerDealWithSecondaryDown();
                }

            }

        }
    }
    private void playerDealWithSecondaryDown(){
        boolean keepPull = true;
        try {
            Map<String, Object> parameters = this.trackerStub.returnParametersPlayers();
            PlayerList playerList = (PlayerList) parameters.get("PlayerList");
            ArrayList playerIDArrayList = playerList.getPlayerIDArrayList();
            if (playerIDArrayList.size() > 0) {
                playerIDArrayList.remove(this.player.getplayerID());
                    while (keepPull) {
                        if (playerIDArrayList.size() > 0) {
                        int rnd = new Random().nextInt(playerIDArrayList.size());
                        //System.out.println(rnd);
                        GameInterface nextStub = this.gameState.getStateByPlayerID((String) playerIDArrayList.get(rnd)).getStub();
                        try {
                            PrimaryUpdate updateInformation = nextStub.gossipPullUpdatePrimary();
                            int updateVersion = updateInformation.getVersion();
                            System.out.println("pulling from player: version : " + updateVersion);
                            System.out.println("current version: " + this.version);
                            if (updateVersion > this.version) {
                                Player primary = updateInformation.getPrimary();
                                Player secondry = updateInformation.getSecondry();
                                if(primary != null) {
                                    this.primary = primary.getplayerID();
                                    this.gameState.addPlayer(primary);
                                }
                                if(secondry != null) {
                                    this.secondary = secondry.getplayerID();
                                    this.gameState.addPlayer(secondry);
                                }
                                this.version = updateVersion;
                            }
                            try {
                                State primaryState = this.gameState.getStateByPlayerID(this.primary);
                                GameInterface primaryStub = primaryState.getStub();
                                primaryStub.ping();
                                keepPull = false;
                            }catch (Exception e){
                                keepPull = true;
                            }
                        } catch (Exception e) {
                            //if nextStub is down, delete it and go to next
                            playerIDArrayList.remove(rnd);
                        }
                    }else{
                            //if playerIDArrayList.size() == 0 which means all players are down
                            //stop pull
                            keepPull = false;
                        }
                }
            }
        }catch (Exception e){
            //trcker will never down
        }
    }

    private void randomlyGossipPushUpdatePrimary(PrimaryUpdate updateInformation){
        ArrayList playerIDList = this.gameState.getPlayerIDArrayList();
//        System.out.println(playerIDList);
//        System.out.println("version is :" + this.version);
        if(playerIDList.size() > 0) {
            playerIDList.remove(this.player.getplayerID());
            if (playerIDList.size() > 0) {
                int rnd = new Random().nextInt(playerIDList.size());
//                System.out.println(rnd);
                GameInterface nextStub = this.gameState.getStateByPlayerID((String) playerIDList.get(rnd)).getStub();
                try {
                    //nextStub.gossipPushUpdatePrimary(updateInformation);
                    nextStub.ping();
                } catch (Exception e) {
                    State primary = this.gameState.getStateByPlayerID(this.primary);
                    GameInterface primaryStub = primary.getStub();
                    try {
                        primaryStub.dealWithUnactivePlayer((String) playerIDList.get(rnd));
                        String playerIDtoBeDelete = (String) playerIDList.get(rnd);
                        if(playerIDtoBeDelete.equals(this.secondary)){
                            this.secondary = null;
                        }
                        this.gameState.removePlayer(playerIDtoBeDelete);
                    } catch (Exception ee) {

                    }
                }
            }
        }
    }

    private void primaryDealWithUnactivePlayer(String PlayerID) throws RemoteException {
        State playerToBeDelete = this.gameState.getStateByPlayerID(PlayerID);
        if (playerToBeDelete != null) {
            int TrackerDeleteSucc = this.trackerStub.removePlayer(PlayerID);//let tracker delete first
            System.out.println("TrackerDeleteSucc" + TrackerDeleteSucc);
            if (TrackerDeleteSucc == 1) {//tracker delete success
                System.out.println("get secondary");
                State secondary = this.gameState.getStateByPlayerID(this.secondary);
                GameInterface secondaryStub = secondary.getStub();
                System.out.println("send unactive player to secondary");
                try {
                    secondaryStub.dealWithUnactivePlayer(PlayerID);//let secondary delete
                } catch (Exception e) {

                }
                System.out.println("secondary dealed with it");
                this.gameState.removePlayer(PlayerID);//primary itself delete
                System.out.println("Primary remove player:" + PlayerID);
                this.version = this.version + 1;
            }
        }
    }

    private void secondaryDealWithUnactivePlayer(String PlayerID) throws RemoteException {
        this.gameState.removePlayer(PlayerID);
    }

    private void playerDealWithUnactivePlayer(String PlayerID) throws RemoteException {
        State primary = this.gameState.getStateByPlayerID(this.primary);
        GameInterface primaryStub = primary.getStub();
        try {
            primaryStub.dealWithUnactivePlayer(PlayerID);
        }catch (Exception ee){

        }
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
        game.trackerStub = trackerStub;//xyx add
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
        if (game.playerList.getSize() == 1) {
            game.startPrimary();
            game.startGame();
            System.out.println(game.getPrimary());
            System.out.println(game.getGameState().getGrid().toString());

            // If there are two players, set the second one as secondary server
        }
//        else if (game.playerList.getSize() == 2) {
//            game.startSecondary();
//        }


        // Join game
        boolean hasJoined = game.joinGame(trackerStub);


        if (hasJoined) {
            System.out.println("Player has joined the game successfully");
            System.out.println(game.getGameState().toString());
            System.out.println(game.getGameState().getGrid().toString());
        }

        //xyx add
        //begine gossip 2 seconds later
        //and run it every 1 second
        Timer timer = new Timer();
        long delay = 100;
        long interval = 500;
        timer.schedule(new TimerTask(){
            public void run() {
//                System.out.println("priamry is " + game.primary);
//                System.out.println("secondary is " + game.secondary);
//                System.out.println("version is "+ game.version);
                //System.out.println("\n");
//                System.out.println(game.gameState.getPlayerIDArrayList());
                game.randomlyGossipPushUpdatePrimary(game.generatePrimaryUpdate());

            }
        }, delay, interval);

        // Play
        game.play();

    }

}
