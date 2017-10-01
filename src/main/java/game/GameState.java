package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import player.Player;

/**
 * Created by chuanyu on 17/9/17.
 */
public class GameState implements Serializable {
    private int n, k;

    // {userName: {score: , i: , j: ,}
    private Map<String, State> states;
    private Grid grid;

    public GameState(int n, int k) {
        this.n = n;
        this.k = k;
        this.states = new HashMap<>();
        this.grid = new Grid(this.n);
    }

    public void addPlayer(Player player) {

        boolean done = false;
        int i = 0;
        int j = 0;
        State state;

        while (!done) {
            i = ThreadLocalRandom.current().nextInt(0, this.n);
            j = ThreadLocalRandom.current().nextInt(0, this.n);
            if (!this.grid.isOccupied(i, j)) {
                done = true;
            }
        }

        state = new State(i, j, 0, player.getStub());
        this.states.put(player.getplayerID(), state);
        this.grid.setOccupied(1, i, j);
    }

    public void removePlayer(String playerID) {

        State state = this.states.get(playerID);

        // set the cell empty
        int i = state.getI();
        int j = state.getJ();
        this.grid.setEmpty(i, j);

        // delete the player from game state
        this.states.remove(playerID);

    }

    public void move(String playerID, char command) {
        State state = this.states.get(playerID);
        int i = state.getI();
        int j = state.getJ();
        boolean needTreasureCreation = false;

        switch (command) {
            case '1':
                // move left

                if ((j != 0) && (!this.grid.isOccupiedByPlayer(i, j - 1))) {
                    // update location
                    state.setJ(j - 1);

                    // update score
                    if (this.grid.isOccupiedByTreasure(i, j - 1)) {
                        state.setScore(state.getScore() + 1);
                        needTreasureCreation = true;
                    }


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
                    state.setI(i + 1);

                    // update score
                    if (this.grid.isOccupiedByTreasure(i + 1, j)) {
                        state.setScore(state.getScore() + 1);
                        needTreasureCreation = true;
                    }


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
                    state.setJ(j + 1);

                    // update score
                    if (this.grid.isOccupiedByTreasure(i, j + 1)) {
                        state.setScore(state.getScore() + 1);
                        needTreasureCreation = true;
                    }


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
                    state.setI(i - 1);

                    // update score
                    if (this.grid.isOccupiedByTreasure(i - 1, j)) {
                        state.setScore(state.getScore() + 1);
                        needTreasureCreation = true;
                    }


                    // set the original location as empty
                    this.grid.setEmpty(i, j);

                    // set the new location as occupied
                    this.grid.setOccupied(1, i - 1, j);
                }
                break;

            case '9':
                // exit the game
                this.removePlayer(playerID);
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

    public void createKTreasures() {
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

    public Map<String, State> getStates() {
        return this.states;
    }

    public State getStateByPlayerID(String playerID) {
        return this.states.get(playerID);
    }

    public ArrayList getPlayerIDArrayList(){
        ArrayList playerIDArrayList = new ArrayList();
        for (String key : this.states.keySet()){
            playerIDArrayList.add(key);
        }
        return playerIDArrayList;
    }

    @Override
    public String toString() {
        String str = "";
        Iterator itr = this.states.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next().toString();
            str += key + "-";
            State state = this.states.get(key);
            str += "i: " + state.getI() + " j: " + state.getJ() + " score: " + state.getScore() + "\n";
        }
        return str;
    }

}
