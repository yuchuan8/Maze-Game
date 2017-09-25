
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;
import player.Player;
import player.PlayerList;

public class Tracker {
    private Tracker() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            TrackerInterface stub = (TrackerInterface) registry.lookup("Tracker");
            Player newPlayer = new Player("192.1.3.2","0","up");
            //stub.addPlayer(newPlayer);
            //stub.removePlayer("192.0.0.2:yu");
            PlayerList newPlayerList = new PlayerList();
            newPlayerList.addPlayer(newPlayer);
            stub.updatePlayerList(newPlayerList);
            Map<String, Object> parameters = stub.returnParametersPlayers();
            System.out.println("N: " + parameters.get("N"));
            System.out.println("K: " + parameters.get("K"));
            PlayerList playerList = (PlayerList)parameters.get("PlayerList");
            ArrayList plyaerArrayList = playerList.getPlayers();
            for (Object playerObject : plyaerArrayList){
                Player player = (Player)playerObject;
                System.out.println(player.getUID());
                System.out.println("\n");
            }


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
