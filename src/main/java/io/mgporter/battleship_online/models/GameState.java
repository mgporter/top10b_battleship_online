package io.mgporter.battleship_online.models;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import java.util.List;

@Component
public class GameState {

  private Gameboard playerOneGameboard;
  private Gameboard playerTwoGameboard;
  private String playerOneId = null;
  private String playerTwoId = null;

  public GameState() {
    this.playerOneGameboard = new Gameboard();
    this.playerTwoGameboard = new Gameboard();
  }

  public Gameboard getPlayerOneGameboard() {
    return this.playerOneGameboard;
  }

  public Gameboard getPlayerTwoGameboard() {
    return this.playerTwoGameboard;
  }

  public void setPlayerOneId(String id) {
    this.playerOneId = id;
  }

  public void setPlayerTwoId(String id) {
    this.playerTwoId = id;
  }

  public String getPlayerOneId() {
    return this.playerOneId;
  }

  public String getPlayerTwoId() {
    return this.playerTwoId;
  }

  public Gameboard getBoardById(String id) {
    boolean isPlayerOne = id == this.playerOneId;
    boolean isPlayerTwo = id == this.playerTwoId;
    if (!isPlayerOne && !isPlayerTwo) throw new Error("Incorrect ID passed");
    if (isPlayerOne) return this.playerOneGameboard;
    else return this.playerTwoGameboard;
  }

  public boolean isReady() {
    return this.playerOneId != null && this.playerTwoId != null;
  }

  public void updatePlayers(List<Player> playerList) {

    int playerCount = playerList.size();
    if (playerCount == 0) return;
    boolean atLeastTwoPlayers = playerCount >= 2;

    String atIndexZero = playerList.get(0).getId();

    // Set the person at index 0 to be the first player, UNLESS this person is the second player already.
    // This could happen if the first player leaves, in which case, the second player
    // would be at index 0 in the list. If this happens, set the person at index 1 to be the first player, or null
    // if there isn't another player
    if (!atIndexZero.equals(this.playerTwoId)) {
      this.playerOneId = atIndexZero;
      this.playerTwoId = atLeastTwoPlayers ? playerList.get(1).getId() : null;
    } else {
      // In this case, the player at index 0 is the second player
      this.playerOneId = atLeastTwoPlayers ? playerList.get(1).getId() : null;
    }

  }

}
