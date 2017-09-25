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
    public Map <String, Object> addPlayer(Player player) {
        String key = player.getUID();
        int addSuccessful = 0;
        String Message = "";
        Player value = this.players.get(key);
        if (value != null) {
            addSuccessful = 0;
            Message = "Add new player Fail. Player exists.";
        } else {
            this.players.put(key, player);
            addSuccessful = 1;
            Message = "Add new player successfully.";
        }
        Map <String, Object> returnMessage = new HashMap <String, Object> ();
        returnMessage.put("isSuccessful_int", addSuccessful);
        returnMessage.put("message_String", Message);
        return returnMessage;
    }

    /**
     * This method removes a player from a player list
     */
    public Map <String, Object> removePlayer(String ip, String playerID) {
        String key = ip + ":" + playerID;
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
    public Map <String, Player> getPlayers() {
        return this.players;
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
