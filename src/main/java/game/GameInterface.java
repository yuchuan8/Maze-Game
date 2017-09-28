package game;

import tracker.player.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Created by chuanyu on 17/9/17.
 */
public interface GameInterface extends Remote {

    // Server methods

    public GameState joinGameServer(Player player) throws RemoteException;

    public GameState makeMove(String uid, char command) throws RemoteException;

    public void setGameState(GameState gameState) throws RemoteException;

    public GameState getGameState() throws RemoteException;


//    public void startGame();

//    public boolean exitGame(String userName) throws RemoteException;
//
//
//
//    public boolean makeMove(String userName, char input) throws RemoteException;
//
//    public boolean becomePrimary() throws RemoteException;
//
//    public boolean assignSecondary() throws RemoteException;
//
//    public boolean pingServer();
//
//
//    // Player methods
//    public void issueCommand();
//
//    public void updateGameStates();
//
//    public void requestExitGame();
//
//    public void pingPlayer();

}
