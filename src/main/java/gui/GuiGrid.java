/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import javax.swing.JInternalFrame;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import player.Player;
import game.Game;
import game.GameState;
import game.Grid;
import game.State;

/**
 *
 * @author macbook
 */
public class GuiGrid extends JInternalFrame {
    
    private BaseDesktop desktop;
    private int gridDimension;
    private GameState gameState;
    private HashMap<String, JButton> gridMap;
    private Player player;
    private Game game;
      
    public GuiGrid(BaseDesktop desktop, GameState gameState){
        super("Maze Game", false, false, false, false);
		
	this.desktop = desktop;
        this.gridDimension = 15;//gameState.getN 
        this.game = game;
        this.player = player;//game.getPlayer();//????
        this.gameState = gameState;
        
        	
	setSize(730, 730);
	setBackground(Color.white);
        setLayout(new GridLayout(gridDimension, gridDimension));
		
	gridMap = new HashMap<String, JButton>();
        for(int i = 0; i < gridDimension; i++) {
            for(int j = 0; j < gridDimension; j++) {
                JButton n = new JButton();
                setKeyListener(n);
                String cor = i + "+" + j;
                gridMap.put(cor, n);
                add(n); 		
            }
	}
        
        this.refreshState(gameState);
        desktop.DisplayScores();
        
    }
    
    public void refreshState(GameState gameState){ 
        for(JButton n : gridMap.values()){
            n.setIcon(null);
            n.setText(null);
            Border originBorder = new LineBorder(Color.GRAY, 1);
            n.setBorder(originBorder);
            n.setBackground(Color.WHITE);
        }   
        
     
        for(int i =0; i<15;i++){
            for(int j =0;j<15;j++){
                if(gameState.getGrid().isOccupiedByTreasure(i,j)==true){
                String cor = i + "+" + j;
                drawTreasure(gridMap.get(cor));
                }
            }
        }
        
        for(String uid: gameState.getStates().keySet()){
            State state = gameState.getStates().get(uid);
            String cor = state.getI() + "+" + state.getJ();
            drawPlayer(gridMap.get(cor), uid);
        }
    }
    
    public void drawPlayer(JButton n, String s){
        
        n.setText(s);
        n.setBackground(Color.PINK);
        n.setOpaque(true);
        Border thickBorder = new LineBorder(Color.black, 2);
	n.setBorder(thickBorder);
        
        if(s==player.getplayerID()){
            Border currentBorder = new LineBorder(Color.red, 2);
            n.setBorder(currentBorder);
        }
        
    }
    
    public void drawTreasure(JButton n){
        ImageIcon icon = new ImageIcon("/Users/macbook/Documents/JavaLearn/MazeGame/diamond.png");
        Image img = icon.getImage() ;  
        Image newimg = img.getScaledInstance( 30, 30, java.awt.Image.SCALE_SMOOTH ) ;  
        icon = new ImageIcon( newimg );
                        
        n.setIcon(icon);
    }
    
    private void setKeyListener(JButton jb) {
        jb.addKeyListener(new KeyAdapter() {
      
        @Override
        public void keyPressed(KeyEvent e) {
            String uid = player.getplayerID();
            if(e.getKeyCode() == 38) {
                char c = '4';	
                refreshState(game.makeMove(uid,c));
   
		}
		else if(e.getKeyCode() == 39) {
                    char c = '3';	
                    refreshState(game.makeMove(uid,c));
                    
		}
		else if(e.getKeyCode() == 40) {
			
                    char c = '2';	
		    refreshState(game.makeMove(uid,c));
                    
		}
		else if(e.getKeyCode() == 37) {

                    char c = '1';	
		    refreshState(game.makeMove(uid,c));
                    
		}
		else {
			//do nothing
		}
      }
    });
    }
}