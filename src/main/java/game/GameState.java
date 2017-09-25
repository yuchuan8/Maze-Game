package game;

import player.Player;
import player.PlayerList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

    public void addPlayer(String uid) {

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

        this.states.put(uid, infoMap);
        this.grid.setOccupied(1, i, j);
    }

    public void removePlayer(String uid) {

        Map<String, Integer> infoMap = this.states.get(uid);

        // set the cell empty
        int i = infoMap.get("i");
        int j = infoMap.get("j");
        this.grid.setEmpty(i, j);

        // delete the player from game state
        this.states.remove(uid);

    }

    public void move(String uid, char command) {
        Map<String, Integer> infoMap = this.states.get(uid);
        int i = infoMap.get("i");
        int j = infoMap.get("j");
        boolean needTreasureCreation = false;

        switch (command) {
            case '1':
                // move left

                if ((j != 0) && (!this.grid.isOccupiedByPlayer(i, j - 1))) {
                    System.out.println("moving left");
                    // update location
                    infoMap.put("j", j - 1);

                    // update score
                    if (this.grid.isOccupiedByTreasure(i, j - 1)) {
                        infoMap.put("score", infoMap.get("score") + 1);
                        needTreasureCreation = true;
                    }

                    this.states.put(uid, infoMap);

                    // set the original location as empty
                    this.grid.setEmpty(i, j);

                    // set the new location as occupied
                    this.grid.setOccupied(1, i, j - 1);
                }
                break;

            case '2':
                // move down
                if ((i != this.n - 1) && (!this.grid.isOccupiedByPlayer(i + 1, j))) {

                    // update location
                    infoMap.put("i", i + 1);

                    // update score
                    if (this.grid.isOccupiedByTreasure(i + 1, j)) {
                        infoMap.put("score", infoMap.get("score") + 1);
                        needTreasureCreation = true;
                    }

                    this.states.put(uid, infoMap);

                    // set the original location as empty
                    this.grid.setEmpty(i, j);

                    // set the new location as occupied
                    this.grid.setOccupied(1, i + 1, j);
                }
                break;

            case '3':
                // move right
                if ((j != this.n - 1) && (!this.grid.isOccupiedByPlayer(i, j + 1))) {


                    // update location
                    infoMap.put("j", j + 1);

                    // update score
                    if (this.grid.isOccupiedByTreasure(i, j + 1)) {
                        infoMap.put("score", infoMap.get("score") + 1);
                        needTreasureCreation = true;
                    }

                    this.states.put(uid, infoMap);

                    // set the original location as empty
                    this.grid.setEmpty(i, j);

                    // set the new location as occupied
                    this.grid.setOccupied(1, i, j + 1);
                }
                break;

            case '4':
                // move up
                if ((i != 0) && (!this.grid.isOccupiedByPlayer(i - 1, j))) {

                    // update location
                    infoMap.put("i", i - 1);

                    // update score
                    if (this.grid.isOccupiedByTreasure(i - 1, j)) {
                        infoMap.put("score", infoMap.get("score") + 1);
                        needTreasureCreation = true;
                    }

                    this.states.put(uid, infoMap);

                    // set the original location as empty
                    this.grid.setEmpty(i, j);

                    // set the new location as occupied
                    this.grid.setOccupied(1, i - 1, j);
                }
                break;

            case '9':
                // exit the game
                this.removePlayer(uid);
                break;
            default:
                break;
        }

        if (needTreasureCreation) {
            this.createTreasure();
        }

    }

    public void updateScore(String userName, int score) {

    }

    public void createTreasure() {

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
        this.grid.setOccupied(2, i, j);
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

    public Map<String, Map<String, Integer>> getStates() {
        return this.states;
    }

    @Override
    public String toString() {
        String str = "";
        Iterator itr = this.states.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next().toString();
            str += key + "-";
            Map<String, Integer> infoMap = this.states.get(key);
            Iterator itrInner = infoMap.keySet().iterator();
            while (itrInner.hasNext()) {
                String keyInner = itrInner.next().toString();
                str += keyInner + ":";
                str += infoMap.get(keyInner) + " ";
            }
            str += "\n";
        }
        return str;
    }

}
