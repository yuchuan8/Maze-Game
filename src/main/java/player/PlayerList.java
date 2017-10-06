package player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.*;

/**
 * THis is the PlayerList Class
 * @author Chuan Yu
 * @version 0.1
 * @since 2016-09-17
 */
public class PlayerList implements Serializable{

    //private ArrayList<Player> players;
    private Map <String, Player> players;
    /**
     * This method initialize an instance of PlayList
     */
    public PlayerList() {
        //this.players = new ArrayList<Player>();
        this.players = new HashMap<String, Player>();
    }

    /**
     * This method adds a player to a player list
     * @param player This is the player to be added
     */
    public boolean addPlayer(Player player) {

        boolean addSuccessful = false;

        // Get new player ID
        String key = player.getplayerID();
        Player value = this.players.get(key);

        this.players.put(key, player);
        addSuccessful = true;

//        // If player ID already exits, return false.
//        // Otherwise, add the new player to the player list
//        if (value != null) {
//            addSuccessful = false;
//            System.err.println("Add new player Fail. Player exists. Player name: " + player.getplayerID());
//        } else {
//            this.players.put(key, player);
//            addSuccessful = true;
//        }

        return addSuccessful;
    }

    /**
     * This method removes a player from a player list
     */
    public Map <String, Object> removePlayer(String playerID) {
        String key = playerID;
        int addSuccessful = 0;
        String Message = "";
        Player value = this.players.get(key);
        if (value != null) {
            this.players.remove(key);
            addSuccessful = 1;
            Message = "Delete player successfully.";
        } else {
            addSuccessful = 0;
            Message = "Delete player fail. Player don't exist.";
        }
        Map <String, Object> returnMessage = new HashMap <String, Object> ();
        returnMessage.put("isSuccessful_int", addSuccessful);
        returnMessage.put("message_String", Message);
        return returnMessage;
    }

    /**
     * This method get all players from a player list
     * @return Player[] An array of players
     */
    public Map<String, Player> getPlayers() {
        return this.players;
    }

    public Player getPlayer(String playerID) {
        return this.players.get(playerID);
    }

    public int getSize() {
        return this.players.size();
    }


    public ArrayList getPlayerIDArrayList(){
        ArrayList playerIDArrayList = new ArrayList();
        for (String key : this.players.keySet()){
            playerIDArrayList.add(key);
        }
        return playerIDArrayList;
    }

    @Override
    public String toString() {
        String str = "";
        for (String key : this.players.keySet()) {
            Player player = this.players.get(key);
            str += player.toString() + " ";
        }
        return str;
    }
}
