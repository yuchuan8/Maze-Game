package game;

import player.Player;
import player.PlayerList;
import java.io.Serializable;

/**
 * Created by chuanyu on 19/9/17.
 */
public class PrimaryUpdate implements Serializable {
    private Player primary;
    private Player secondry;
    private int version;

    public PrimaryUpdate(int version, Player primary, Player secondry){
        this.primary = primary;
        this.secondry = secondry;
        this.version = version;
    }

    public void setPrimary(Player primary){
        this.primary = primary;
    }
    public void setSecondry(Player secondry){
        this.secondry = secondry;
    }
    public void setVersion(int version){
        this.version = version;
    }

    public int getVersion(){
        return this.version;
    }
    public Player getPrimary(){
        return this.primary;
    }
    public Player getSecondry(){
        return this.secondry;
    }

}
