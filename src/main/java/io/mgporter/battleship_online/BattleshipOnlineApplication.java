package io.mgporter.battleship_online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODOs:
 * 	1.) Allow players to keep the same id after a refresh. Currently, if the player
 * 	refreshes their browser, they get a new Id by the server, and are therefore seen
 * as a new player. Only their name persists, because it is stored in local storage.
 * 
 * 2.) Save gamestate to the database progressively, and allow observers to watch the game
 * and fill in for players that leave prematurely. Also allow observers to enter a 
 * game that is already in progress.
  */

@SpringBootApplication
public class BattleshipOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(BattleshipOnlineApplication.class, args);
	}

}
