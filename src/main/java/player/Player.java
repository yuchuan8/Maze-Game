package player;

import java.io.Serializable;

/**
 * THis is the Player Class
 * @author Chuan Yu
 * @version 0.1
 * @since 2016-09-17
 */


public class Player implements Serializable {

    private String playerID;
    private String ip;
    private String portNo;
    //private String uid;

    /**
     * This method initialize a Player instance
     * @param playerID This defines player user name
     * @param ip This defines the player's IP address
     */
    public Player(String ip, String portNo, String playerID) {
        this.playerID = playerID;
        this.ip = ip;
        this.portNo = portNo;
        //this.uid = ip + ':' + playerID;
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


    /**
     * This method gets the player IP address
     * @return
     */
    public String getIP() {
        return this.ip;
    }


    /**
     * This method sets the player IP address
     * @param ip
     */
    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getPortNo() { return this.portNo; }

    public void setPortNo(String portNo) { this.portNo = portNo; }

    public String getUID() {
        return this.ip + ":" + this.playerID;
    }

    //public void setUID(String uid) { this.uid = uid; }

    @Override
    public String toString() {
        return this.playerID;
    }
}
