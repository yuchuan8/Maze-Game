package tracker;
/*
 * Copyright 2004 Sun Microsystems, Inc. All  Rights Reserved.
 *  
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 * -Redistributions of source code must retain the above copyright  
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright 
 *  notice, this list of conditions and the following disclaimer in 
 *  the documentation and/or other materials provided with the 
 *  distribution.
 *  
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *  
 * You acknowledge that Software is not designed, licensed or 
 * intended for use in the design, construction, operation or 
 * maintenance of any nuclear facility.
 */
//package example.hello;

import java.util.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import player.Player;
import player.PlayerList;


public class TrackerServer implements TrackerInterface {
	
	private int N = -1;
	private int K = -1;
	private int portNum = -1;
	PlayerList VPlayerList = new PlayerList();
	
    public TrackerServer() {}

	/**
	 * set N
	 * @param n
	 */
	public void setN(int n) {
		this.N = n;
	}

	/**
	 * Set K
	 * @param k
	 */
	public void setK(int k) {
		this.K = k;
	}

	/**
	 * Set PortNum
	 * @param portNum
	 */
	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

	/**
     * This remote method is used to return N, K and a full player list.
     * @return Map A hashmap with key "N", "K" and "Players".
     * @throws RemoteException
     */
	public Map<String, Object> returnParametersPlayers(){
        Map <String, Object> ParametersPlayers = new HashMap<String, Object>();
		ParametersPlayers.put("N",N);
		ParametersPlayers.put("K",K);
        ParametersPlayers.put("PlayerList",VPlayerList);
		return ParametersPlayers;
	}

	/**
	 * update the whole playerList
	 * @param players
	 */
	public void updatePlayerList(PlayerList players) {
		VPlayerList = players;
    }

	/**
	 * add one player
	 * @param player
	 */
    public void addPlayer(Player player) {
		VPlayerList.addPlayer(player);
    }

	/**
	 * remove on player according to the uid provided
	 * @param uid
	 */
    public void removePlayer(String playerID) {
		VPlayerList.removePlayer(playerID);
    }

	/**
	 * java TrackerServer portNum=0 N=7 K=8
	 * @param args
	 */
	public static void main(String args[]) {
	
	    TrackerServer obj = new TrackerServer();
		if(args.length == 3){
		obj.setPortNum(Integer.parseInt(args[0]));
		obj.setN(Integer.parseInt(args[1]));
		obj.setK(Integer.parseInt(args[2]));
		}
		TrackerInterface stub = null;
		Registry registry = null;
	try {
		System.err.println("TrackerServer Port: " + Integer.toString(obj.portNum));
	    stub = (TrackerInterface) UnicastRemoteObject.exportObject(obj, obj.portNum);
        registry = LocateRegistry.getRegistry();
	    registry.bind("Tracker", stub);
	    System.err.println("Tracker ready");
	} catch (Exception e) {
	    try{
			System.err.println("Tracker exception: " + e.toString());
			registry.unbind("Tracker");
			registry.bind("Tracker",stub);
	    	System.err.println("Tracker ready");
	    }catch(Exception ee){
			System.err.println("Tracker exception: " + ee.toString());
	    	ee.printStackTrace();
	    }
	}
    }
}
