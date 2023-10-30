package io.mgporter.battleship_online.models;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class GameState {

  // private final int roomNumber;
  private Gameboard playerOneGameboard;
  private Gameboard playerTwoGameboard;
  private String playerOneId = null;
  private String playerTwoId = null;
  public ApplicationState applicationState = ApplicationState.UNINITIALIZED;

  public GameState() {
    this.playerOneGameboard = new Gameboard();
    this.playerTwoGameboard = new Gameboard();
    // this.roomNumber = roomNumber;
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

}
