package game;

import com.sun.org.apache.regexp.internal.RE;
import player.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by chuanyu on 17/9/17.
 */
public interface GameInterface extends Remote {

    // Server methods

    public GameState joinGameServer(Player player) throws RemoteException;

    public GameState makeMove(String uid, char command) throws RemoteException;

    public GameState getGameState() throws RemoteException;

    public void setGameState(GameState gameState) throws RemoteException;

    public String getPrimary() throws RemoteException;

    public String getSecondary() throws RemoteException;

    public void setSecondary(String secondaryID) throws RemoteException;

    public void ping() throws RemoteException;

}
