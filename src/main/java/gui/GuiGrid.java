package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import javax.swing.JInternalFrame;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import player.Player;
import game.Game;
import game.GameState;
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
        this.gridDimension = gameState.getN();//gameState.getN
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
                String cor = i + "+" + j;
                gridMap.put(cor, n);
                add(n); 		
            }
	}
        
        this.refreshState(gameState);
        
    }
    
    public void refreshState(GameState gameState){ 
        for(JButton n : gridMap.values()){
            n.setIcon(null);
            n.setText(null);
            Border originBorder = new LineBorder(Color.GRAY, 1);
            n.setBorder(originBorder);
            n.setBackground(Color.WHITE);
        }   
        
     
        for(int i =0; i<gameState.getN();i++){
            for(int j =0;j<gameState.getN();j++){
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
        
    }

    public void drawTreasure(JButton n){
        n.setText("*");
    }
}
