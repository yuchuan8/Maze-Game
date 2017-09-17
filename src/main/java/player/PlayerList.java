package player;

import java.io.Serializable;

/**
 * THis is the PlayerList Class
 * @author Chuan Yu
 * @version 0.1
 * @since 2016-09-17
 */
public class PlayerList implements Serializable{

    private Player[] players;

    /**
     * This method initialize an instance of PlayList
     * @param players This is an array of players
     */
    public PlayerList(Player[] players) {
        this.players = players;
    }

    /**
     * This method adds a player to a player list
     * @param player This is the player to be added
     */
    public void addPlayer(Player player) {

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
    public Player[] getPlayers() {
        return this.players;
    }
}
