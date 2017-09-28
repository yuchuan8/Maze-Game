package trackerClient;
import java.util.Map;
import java.rmi.Remote;
import java.rmi.RemoteException;

import player.Player;
import player.PlayerList;

public interface TrackerInterface extends Remote {
    /**
     * This remote method is used to return N, K and a full player list.
     * @return Map A hashmap with key "N", "K" and "Players".
     * @throws RemoteException
     */
    public Map<String, Object> returnParametersPlayers() throws RemoteException;

    /**
     * This remote method updates the Tracker player list.
     * @param players An array of players.
     * @throws RemoteException
     */
    public void updatePlayerList(PlayerList players) throws RemoteException;

    /**
     * This method adds a player to the Tracker player list.
     * @param player This is the player to be added.
     * @throws RemoteException
     */
    public void addPlayer(Player player) throws RemoteException;

    /**
     * This method remove a player from the Tracker player list.
     * @param userName This is the user name of the player to be removed.
     * @throws RemoteException
     */
    public void removePlayer(String playerID) throws RemoteException;

}
