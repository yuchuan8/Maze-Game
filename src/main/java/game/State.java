package game;

import java.io.Serializable;

/**
 * Created by chuanyu on 29/9/17.
 */
public class State implements Serializable{
    private int i;
    private int j;
    private int score;
    private GameInterface stub;

    public State(int i, int j, int score, GameInterface stub) {
        this.i = i;
        this.j = j;
        this.score = score;
        this.stub = stub;
    }

    public int getI() {

        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public GameInterface getStub() {
        return stub;
    }

    public void setStub(GameInterface stub) {
        this.stub = stub;
    }
}
