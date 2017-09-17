package game;

import java.io.Serializable;

/**
 * This is the Grid class
 * @author Chuan Yu
 * @version 0.1
 * @since 2016-09-17
 */
public class Grid implements Serializable {
    private int[][] gridStates;

    /**
     * This method initialize a Grid instance
     * @param n A grid is nxn
     */
    public Grid(int n) {
        this.gridStates = new int[n][n];
        for (int i=0 ; i < n; i++) {
            for (int j=0; j<n; j++) {
                this.gridStates[i][j] = 0;
            }
        }

    }

    /**
     * This method sets a cell as occupied
     * @param occupiedBy The type of occupant: 1 - player, 2 - treasure.
     * @param i x-coordinate.
     * @param j y-coordinate.
     * @return boolean True indicates success, False indicates fail.
     */
    public boolean setOccupied(int occupiedBy, int i, int j) {

    }

    /**
     * This method sets a cell as empty
     * @param i x-coordinate.
     * @param j y-coordinate.
     * @return boolean True indicates success, False indicates fail.
     */
    public boolean setEmpty(int i, int j) {

    }
}
