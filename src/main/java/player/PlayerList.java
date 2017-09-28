package player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * THis is the PlayerList Class
 * @author Chuan Yu
 * @version 0.1
 * @since 2016-09-17
 */
public class PlayerList implements Serializable{

    private ArrayList<tracker.player.Player> players;

    /**
     * This method initialize an instance of PlayList
     */
    public PlayerList() {
        this.players = new ArrayList<tracker.player.Player>();
    }

    /**
     * This method adds a player to a player list
     * @param player This is the player to be added
     */
    public void addPlayer(tracker.player.Player player) {
        this.players.add(player);
    }

    /**
     * This method removes a player from a player list
     */
    public void removePlayer() {

    }

    /**
     * This method get all players from a player list
     * @return Player[] An array of players
     */
    public ArrayList<tracker.player.Player> getPlayers() {
        return this.players;
    }


    @Override
    public String toString() {
        String str = "";
        for (tracker.player.Player player : this.players) {
            str += player.toString() + " ";
        }
        return str;
    }
}
