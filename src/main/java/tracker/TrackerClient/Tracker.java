package trackerClient;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.*;

import player.Player;
import player.PlayerList;

public class Tracker {
    private Tracker() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            System.out.println("begain");
            Registry registry = LocateRegistry.getRegistry(host);
            System.out.println("after get host");

            TrackerInterface stub = (TrackerInterface) registry.lookup("Tracker");
            Map<String,Object> playerStub = new HashMap <String,Object>();
            playerStub.put("TrackerInterface",stub);
            Player newPlayer = new Player("0","up",playerStub);
            stub.addPlayer(newPlayer);
            System.out.println("stub.addPlayer(newPlayer)");

            Map<String, Object> playerParameters = stub.returnParametersPlayers();

            PlayerList newplayerlist = (PlayerList)playerParameters.get("PlayerList");
            Player newplayer = (Player)newplayerlist.getPlayers().get("up");
            Map<String,Object> newstubDict = newplayer.getStubDict();
            TrackerInterface newstub = (TrackerInterface)newstubDict.get("TrackerInterface");

            Player newPlayer2 = new Player("0","xy",playerStub);
            newstub.addPlayer(newPlayer2);
            //stub.removePlayer("192.0.0.2:yu");
            //PlayerList newPlayerList = new PlayerList();
            //newPlayerList.addPlayer(newPlayer);
            //stub.updatePlayerList(newPlayerList);
            Map<String, Object> parameters = stub.returnParametersPlayers();
            System.out.println("N: " + parameters.get("N"));
            System.out.println("K: " + parameters.get("K"));
            PlayerList playerList = (PlayerList)parameters.get("PlayerList");
            Map <String, Player> playerDict = playerList.getPlayers();
            for(String key : playerDict.keySet()){
                System.out.println("Key = " + key);
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
