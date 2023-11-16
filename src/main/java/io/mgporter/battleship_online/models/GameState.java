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
  public List<CoordinateAttack> playerOnesAttacks;
  public List<CoordinateAttack> playerTwosAttacks;

  public GameState() {
    this.playerOneShipList = new ArrayList<>(Constants.MAXSHIPS);
    this.playerTwoShipList = new ArrayList<>(Constants.MAXSHIPS);
    this.playerOnesAttacks = new ArrayList<>(40);
    this.playerTwosAttacks = new ArrayList<>(40);
    this.playerOneId = null;
    this.playerTwoId = null;
  }

  public boolean isPlayerOne(String id) {
    if (playerOneId == null) return false;
    return playerOneId.equals(id);
  }

  public boolean isPlayerTwo(String id) {
    if (playerTwoId == null) return false;
    return playerTwoId.equals(id);
  }

  public boolean bothPlayersReady() {
    return this.playerOneId != null && this.playerTwoId != null;
  }

  public boolean allPlacementsComplete() {
    return playerOneShipList.size() == Constants.MAXSHIPS && playerTwoShipList.size() == Constants.MAXSHIPS;
  }

  /**
   * @param id
   * @return true if the playerId has completed their ship placements, otherwise false. Also
   * returns false if the id is not playerOne or playerTwo.
    */

  public boolean myPlacementsComplete(String id) {
    if (isPlayerOne(id)) return playerOneShipList.size() == Constants.MAXSHIPS;
    if (isPlayerTwo(id)) return playerTwoShipList.size() == Constants.MAXSHIPS;
    else return false;
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
