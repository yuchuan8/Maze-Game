package player;

import game.GameInterface;

import java.io.Serializable;
import java.util.*;
/**
 * THis is the Player Class
 * @author Chuan Yu
 * @version 0.1
 * @since 2016-09-17
 */


public class Player implements Serializable {

    private String playerID;
    private int portNo;
    private GameInterface stub;

    /**
     * This method initialize a Player instance
     * @param playerID This defines player user name
     * @param ip This defines the player's IP address
     */
    public Player(String playerID, GameInterface stub) {
        this.playerID = playerID;
        this.stub = stub;
    }

    /**
     * This method gets the player user name
     * @return String This returns the player user name
     */
    public String getplayerID() {
        return this.playerID;
    }


    /**
     * This method sets the player user name
     * @param playerID This is the player user name
     */
    public void setplayerID(String playerID) {
        this.playerID = playerID;
    }

    public void setStub(GameInterface stub){
        this.stub = stub;
    }


    public GameInterface getStub(){
        return this.stub;
    }


    @Override
    public String toString() {
        return this.playerID;
    }
}
