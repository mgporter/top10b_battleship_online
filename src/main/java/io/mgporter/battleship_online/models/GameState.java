package io.mgporter.battleship_online.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class GameState {

  /* This component stores all of the information needed to recreate a game, but
   * does not store the actual gameboards.
   */

  // private Gameboard playerOneGameboard;
  // private Gameboard playerTwoGameboard;

  private String playerOneId = null;
  private String playerTwoId = null;
  public List<Ship> playerOneShipList;
  public List<Ship> playerTwoShipList;
  public List<Coordinate> playerOnesAttacks;
  public List<Coordinate> playerTwosAttacks;

  public GameState() {
    this.playerOneShipList = new ArrayList<>(Constants.maxShips);
    this.playerTwoShipList = new ArrayList<>(Constants.maxShips);
    this.playerOnesAttacks = new ArrayList<>(40);
    this.playerTwosAttacks = new ArrayList<>(40);
  }

  // public void setPlayerOneId(String id) {
  //   this.playerOneId = id;
  // }

  // public void setPlayerTwoId(String id) {
  //   this.playerTwoId = id;
  // }

  public boolean getPlayer(String id) {
    if (playerOneId.equals(id)) return true;
    else if (playerTwoId.equals(id)) return false;
    else throw new Error("ID passed is not a player in this Game State");
  }

  // public String getPlayerOneId() {
  //   return this.playerOneId;
  // }

  // public String getPlayerTwoId() {
  //   return this.playerTwoId;
  // }

  public boolean bothPlayersReady() {
    return this.playerOneId != null && this.playerTwoId != null;
  }

  public boolean allPlacementsComplete() {
    return playerOneShipList.size() == Constants.maxShips && playerTwoShipList.size() == Constants.maxShips;
  }

  public void updatePlayers(List<Player> playerList) {

    int playerCount = playerList.size();
    if (playerCount == 0) return;
    boolean atLeastTwoPlayers = playerCount >= 2;

    String atIndexZero = playerList.get(0).getId();

    /* Set the person at index 0 to be the first player, UNLESS this person is the second player already.
     * This could happen if the first player leaves, in which case, the second player
     * would be at index 0 in the list. If this happens, set the person at index 1 to be the first player, or null
     * if there isn't another player
     */

    if (!atIndexZero.equals(this.playerTwoId)) {
      this.playerOneId = atIndexZero;
      this.playerTwoId = atLeastTwoPlayers ? playerList.get(1).getId() : null;
    } else {
      // In this case, the player at index 0 is the second player
      this.playerOneId = atLeastTwoPlayers ? playerList.get(1).getId() : null;
    }
  }

  // public void setPlayerOneShipList(List<Ship> list) {
  //   this.playerOneShipList = list;
  // }

  // public void setPlayerTwoShipList(List<Ship> list) {
  //   this.playerTwoShipList = list;
  // }

  // public void addShipToPlayerOne(Ship ship) {
  //   addShip(playerOneShipList, ship);
  // }

  // public void addShipToPlayerTwo(Ship ship) {
  //   addShip(playerTwoShipList, ship);
  // }

  // public void addShip(List<Ship> list, Ship ship) {
  //   list.add(ship);
  // }

}
