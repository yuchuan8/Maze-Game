package game;

import player.Player;
import player.PlayerList;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by chuanyu on 17/9/17.
 */
public class GameState implements Serializable {
    private int n, k;

    // {userName: {score: , i: , j: ,}
    private Map<String, Map<Character, Integer>> states;
    private Grid grid;

    public GameState(PlayerList playerList, int n, int k, int score) {

    }

    public void move(String userName, int i, int j) {

    }

    public void updateScore(String userName, int score) {

    }

    public void createTreasure(int i, int j) {

    }

    public Map<String, Map<Character, Integer>> getStates() {
        return this.states;
    }

}
