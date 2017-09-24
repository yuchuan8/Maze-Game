package game;

import player.Player;
import player.PlayerList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by chuanyu on 17/9/17.
 */
public class GameState implements Serializable {
    private int n, k;

    // {userName: {score: , i: , j: ,}
    private Map<String, Map<String, Integer>> states;
    private Grid grid;

    public GameState(int n, int k) {
        this.n = n;
        this.k = k;
        this.states = new HashMap<String, Map<String, Integer>>();
        this.grid = new Grid(this.n);
    }

    public void addPlayer(String userName) {

        boolean done = false;
        int i = 0;
        int j = 0;
        while (!done) {
            i = ThreadLocalRandom.current().nextInt(0, this.n);
            j = ThreadLocalRandom.current().nextInt(0, this.n);
            if (!this.grid.isOccupied(i, j)) {
                done = true;
            }
        }
        Map<String, Integer> infoMap = new HashMap<String, Integer>();
        infoMap.put("score", 0);
        infoMap.put("i", i);
        infoMap.put("j", j);

        this.states.put("score", infoMap);
    }

    public void move(String userName, int i, int j) {

    }

    public void updateScore(String userName, int score) {

    }

    public void createTreasure(int i, int j) {

    }

    public void createKTreuarues() {
        // generate k random numbers in the range between 0 and n*n
        int[] randInts= ThreadLocalRandom.current().ints(0, this.n * this.n).distinct().limit(this.k).toArray();

        // Set k random cells to be occupied by treasures
        for (int randInt : randInts) {
            int x = (int) Math.floor(randInt / this.n);
            int y = randInt % this.n;
            this.grid.setOccupied(2, x, y);
        }
    }

    public Grid getGrid() {
        return this.grid;
    }

    public Map<String, Map<Character, Integer>> getStates() {
        return this.states;
    }

}
