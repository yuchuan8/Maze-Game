package tracker;

import java.util.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import player.Player;
import player.PlayerList;


public class TrackerServer implements TrackerInterface {

    private int N = -1;
    private int K = -1;
    private int portNum = -1;
    PlayerList playerList = new PlayerList();

    public TrackerServer() {}

    /**
     * set N
     * @param n
     */
    public void setN(int n) {
        this.N = n;
    }

    /**
     * Set K
     * @param k
     */
    public void setK(int k) {
        this.K = k;
    }

    /**
     * Set PortNum
     * @param portNum
     */
    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }

    /**
     * This remote method is used to return N, K and a full player list.
     * @return Map A hashmap with key "N", "K" and "Players".
     * @throws RemoteException
     */
    public Map<String, Object> returnParametersPlayers(){
        Map <String, Object> ParametersPlayers = new HashMap<>();
        ParametersPlayers.put("N", this.N);
        ParametersPlayers.put("K", this.K);
        ParametersPlayers.put("PlayerList", this.playerList);
        return ParametersPlayers;
    }

    /**
     * update the whole playerList
     * @param players
     */
    public void updatePlayerList(PlayerList players) {
        this.playerList = players;
    }

    /**
     * add one player
     * @param player
     */
    public void addPlayer(Player player) {
        this.playerList.addPlayer(player);
        System.out.println(this.playerList.toString());
    }

    /**
     * remove on player according to the uid provided
     * @param playerID
     * @return
     */
    public int removePlayer(String playerID) {
        System.out.println(playerID);
        Map <String, Object> message = this.playerList.removePlayer(playerID);
        int successful = (int)message.get("isSuccessful_int");
        System.out.println((String)message.get("message_String"));
        System.out.println(this.playerList.toString());
        return successful;
    }

    public void print(String str) {
        System.out.println(str);
    }

    /**
     * java TrackerServer portNum=0 N=7 K=8
     * @param args
     */
    public static void main(String args[]) {

        TrackerServer obj = new TrackerServer();
        if(args.length == 3){
            obj.setPortNum(Integer.parseInt(args[0]));
            obj.setN(Integer.parseInt(args[1]));
            obj.setK(Integer.parseInt(args[2]));
        }
        TrackerInterface stub = null;
        Registry registry = null;
        try {
            System.err.println("TrackerServer Port: " + Integer.toString(obj.portNum));
            stub = (TrackerInterface) UnicastRemoteObject.exportObject(obj, obj.portNum);
            registry = LocateRegistry.getRegistry();
            registry.rebind("Tracker", stub);
            System.err.println("Tracker ready");
        } catch (Exception e) {
            try{
                System.err.println("Tracker exception: " + e.toString());
                registry.unbind("Tracker");
                registry.bind("Tracker",stub);
                System.err.println("Tracker ready");
            }catch(Exception ee){
                System.err.println("Tracker exception: " + ee.toString());
                ee.printStackTrace();
            }
        }
    }
}
