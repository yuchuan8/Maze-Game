package player;

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
    //private String ip;
    private String portNo;
    private Map<String,Object> stubDict;
    //private String uid;

    /**
     * This method initialize a Player instance
     * @param playerID This defines player user name
     * @param ip This defines the player's IP address
     */
    public Player(String portNo, String playerID, Map<String,Object> stubDict) {
        this.playerID = playerID;
        //this.ip = ip;
        this.portNo = portNo;
        this.stubDict = stubDict;
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
    //public String getIP() {
    //    return this.ip;
    //}


    /**
     * This method sets the player IP address
     * @param ip
     */
    //public void setIP(String ip) {
    //    this.ip = ip;
    //}

    public String getPortNo() { return this.portNo; }

    public void setPortNo(String portNo) { this.portNo = portNo; }

    public String getUID() {
        return this.playerID;
    }

    public void setStubDict(Map<String,Object> stubDict){
        this.stubDict = stubDict;
    }
    public Map<String,Object> getStubDict(){
        return this.stubDict;
    }
    public void addStubDict(String key, Object object){
        this.stubDict.put(key, object);
    }
    public void removeStubDict(String key){
        Object value = this.stubDict.get(key);
        if (value != null) {
            this.stubDict.remove(key);
        }
    }
    //public void setUID(String uid) { this.uid = uid; }

    @Override
    public String toString() {
        return this.playerID;
    }
}
