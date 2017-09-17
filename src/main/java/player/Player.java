package player;

import java.io.Serializable;

/**
 * THis is the Player Class
 * @author Chuan Yu
 * @version 0.1
 * @since 2016-09-17
 */


public class Player implements Serializable {

    private String userName;
//    private int score;
    private String ip;

    /**
     * This method initialize a Player instance
     * @param userName This defines player user name
     * @param score This defines the player score
     */
    public Player(String userName, String ip) {
        this.userName = userName;
        this.ip = ip;
//        this.score = score;
    }

    /**
     * This method gets the player user name
     * @return String This returns the player user name
     */
    public String getUserName() {
        return this.userName;
    }

//    /**
//     * This method gets the player score
//     * @return int This returns the player score
//     */
//    public int getScore() {
//        return this.score;
//    }

    /**
     * This method sets the player user name
     * @param userName This is the player user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

//    /**
//     * This method sets the player score
//     * @param score This is the play score
//     */
//    public void setScore(int score) {
//        this.score = score;
//    }

    /**
     * This method gets the player IP address
     * @return
     */
    public String getIP() {
        retrun this.ip;
    }


    /**
     * This method sets the player IP address
     * @param ip
     */
    public void setIP(String ip) {
        this.ip = ip;
    }
}
