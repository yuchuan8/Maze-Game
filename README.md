# Distributed Maze Game

## Introduction
This is a maze game distibuted system built using Java RMI. Players can move around a n-by-n grid to search for treasures. 

The system does not have dedicated servers. Two players act as primary and secondary servers respectively. Players can leave the game any time and this means new primary and secondary servers need to be created when the old ones leave the game. And servers and players can crash.

There are two main programmes in the system, i.e. Tracker and Game. Tracker is used to track the players. When joining the game, a player contacts the tracker to obtain a list of exiting players and then find out who is the primary. Each player runs the Game programme to join the game.

## Files
- Tracker
	- TrackerServer: the main tracker programme
	- TrackerInterface: RMI remote interface for tracker
- Game
	- Game: the main Game programme
	- GameInterface: RMI remote interface for Game programme
	- GameState: class to store game state information
	- Grid: class to define the game grid
	- PrimaryUpdate: class to define primary server update
	- State: class to define state
- Player
	- player: class to define a player
	- PlayerList: class to define a list of players
- GUI
	- BaseDesktop: defines the main GUI
	- GuiGrid: defines the game grid
	- ScoreScroll: defines the score panel
