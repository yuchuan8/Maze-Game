/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;
import player.Player;
import game.GameState;
import game.Game;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author macbook
 */
public class BaseDesktop extends JFrame implements WindowListener {
    
    private Container c;
    private JDesktopPane window;
    private int width = 1000;
    private int height = 750;
    private Player player;
    private GameState gameState;
    private Game game;
    
    public BaseDesktop(Player player, GameState gameState){
        super();
        //Initialize the window content;
        this.player = player;
        this.gameState = gameState;
        c = getContentPane();
        c.setBackground(Color.GRAY);
        setSize(width, height);
        setTitle("Maze Game - Player: " + player.getplayerID());
        window = new JDesktopPane();
	window.setBackground(Color.GRAY);        
	c.add(window, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public void DisplayGrid(){
        GuiGrid gridMap;
        gridMap =new GuiGrid(this, gameState);
        AddInternalFrame(gridMap);
        gridMap.setLocation(280, 10);      
    }
    public void DisplayScores(){
        ScoreScroll scorePane;
        scorePane =new ScoreScroll(gameState);
        AddInternalFrame(scorePane);
              
    }
    private void AddInternalFrame(JInternalFrame f){
        window.add(f);
        f.setVisible(true);
    }
    @Override
    public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
    }

    @Override
    public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
    }
    
    @Override
    public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
    }

    @Override
    public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
    }

    @Override
    public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
    }

        
    @Override
    public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
    }
    
}
