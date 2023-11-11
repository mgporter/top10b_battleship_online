package io.mgporter.battleship_online.models;

import org.springframework.stereotype.Component;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/*
 * This component stores all of the information needed to recreate a game, but
 * does not store the actual gameboards.
 */

@Component
@Data
public class GameState {

  private String playerOneId;
  private String playerTwoId;
  public List<Ship> playerOneShipList;
  public List<Ship> playerTwoShipList;
  public List<Coordinate> playerOnesAttacks;
  public List<Coordinate> playerTwosAttacks;

  public GameState() {
    this.playerOneShipList = new ArrayList<>(Constants.MAXSHIPS);
    this.playerTwoShipList = new ArrayList<>(Constants.MAXSHIPS);
    this.playerOnesAttacks = new ArrayList<>(40);
    this.playerTwosAttacks = new ArrayList<>(40);
    this.playerOneId = null;
    this.playerTwoId = null;
  }

  public boolean isPlayerOne(String id) {
    if (playerOneId.equals(id)) return true;
    else return false;
  }

  public boolean bothPlayersReady() {
    return this.playerOneId != null && this.playerTwoId != null;
  }

  public boolean allPlacementsComplete() {
    return playerOneShipList.size() == Constants.MAXSHIPS && playerTwoShipList.size() == Constants.MAXSHIPS;
  }

  /**
   * Update playerOne's and playerTwo's IDs based on the new player list passed in.
   * This methods updates players by setting the person at index 0 of the playerlist to be the first player, 
   * UNLESS this person is the second player already. This could happen if there are two players, and
   * the first player leaves. In that case, the second player would be at index 0 in the list. 
   * If this happens, set the person at index 1 to be the first player, or null if there isn't another player
   * 
   * @param playerList
    */

  public void updatePlayers(List<Player> playerList) {

    int playerCount = playerList.size();
    if (playerCount == 0) return;
    boolean atLeastTwoPlayers = playerCount >= 2;

    String atIndexZero = playerList.get(0).getId();

    if (!atIndexZero.equals(this.playerTwoId)) {
      this.playerOneId = atIndexZero;
      this.playerTwoId = atLeastTwoPlayers ? playerList.get(1).getId() : null;
    } else {
      this.playerOneId = atLeastTwoPlayers ? playerList.get(1).getId() : null;
    }

  }

}
