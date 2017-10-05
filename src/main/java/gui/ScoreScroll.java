/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javax.swing.JInternalFrame;
import game.GameState;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author macbook
 */
public class ScoreScroll extends JInternalFrame{

    private JTable table;
    private DefaultTableModel tableModel;
    private String[] headers = {"Player", "Score"};
    
    public ScoreScroll(GameState gameState){
        super("Score");
        setLocation(10, 10);
	    setSize(250, 730);
	    setBackground(Color.white);

        String[][] data = getData(gameState);

        tableModel = new DefaultTableModel(data, this.headers);
        this.table = new JTable(this.tableModel);
        JScrollPane scroll = new JScrollPane(this.table);
        getContentPane().add(scroll);
        this.tableModel.fireTableDataChanged();
        setVisible(true);
    }
    
    public void refreshScores(GameState gameState){
        String[][] data = getData(gameState);
        this.tableModel.setDataVector(data, this.headers);
    }


    private String[][] getData(GameState gameState) {
        Map<String, Integer> unsortedScore = new HashMap<String, Integer>();

        for(String uid:gameState.getStates().keySet()){
            unsortedScore.put(uid,gameState.getStates().get(uid).getScore());
        }

        String[][] data = new String[gameState.getStates().size()+1][2];

        int i = 0;
        for(String uid:unsortedScore.keySet()){

            data[i][0]=uid;
            data[i][1]=Integer.toString(unsortedScore.get(uid));
            i++;
        }

        return data;
    }
}
