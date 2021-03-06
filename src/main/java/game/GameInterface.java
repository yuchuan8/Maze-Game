package game;

import player.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by chuanyu on 17/9/17.
 */
public interface GameInterface extends Remote {

    // Server methods

    public Map<String, Object> joinGameServer(Player player) throws RemoteException;

    public Map<String, Object> makeMove(String uid, char command) throws RemoteException;

    public GameState getGameState() throws RemoteException;

    public void setGameState(GameState gameState) throws RemoteException;

    public Player getPlayer() throws RemoteException;

    public String getPrimary() throws RemoteException;

    public String getSecondary() throws RemoteException;

    public void setIsPrimary(boolean isPrimary) throws RemoteException;

    public void setIsSecondary(boolean isSecondary) throws RemoteException;

    public void setPrimary(String primaryID) throws RemoteException;

    public void setSecondary(String secondaryID) throws RemoteException;

    public void startPrimary() throws RemoteException;

    public void startSecondary() throws RemoteException;

    public void ping() throws RemoteException;

    public void pingServer() throws RemoteException;

    public boolean exitGameServer(String playerID) throws RemoteException;

    public void gossipPushUpdatePrimary(PrimaryUpdate updateInformation) throws RemoteException;

    public PrimaryUpdate gossipPullUpdatePrimary() throws RemoteException;

    public void dealWithUnactivePlayer(String PlayerID) throws RemoteException;

    public void setVersion(int version) throws RemoteException;



}
