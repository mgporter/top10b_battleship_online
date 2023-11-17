package io.mgporter.battleship_online.services;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import io.mgporter.battleship_online.enums.PacketType;
import io.mgporter.battleship_online.models.Coordinate;
import io.mgporter.battleship_online.models.CoordinateAttack;
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Gameboard;
import io.mgporter.battleship_online.models.Ship;
import lombok.Data;

/**
 * This service provides functions to modify the game state that each player can see. 
 * In particular, it interfaces directly with the gameboards with which attacks and
 * ship placements are checked.
  */

@Component
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
public class GameService {
  
  private GameState gameState;
  private Gameboard playerOneGameboard;
  private Gameboard playerTwoGameboard;

  public GameService() {
    this.playerOneGameboard = new Gameboard();
    this.playerTwoGameboard = new Gameboard();
  }

  public void saveShipsInGameState(String playerId, List<Ship> ships) {
    boolean isPlayerOne = gameState.isPlayerOne(playerId);
    if (isPlayerOne) gameState.setPlayerOneShipList(ships);
    else gameState.setPlayerTwoShipList(ships);

  }

  public Optional<Ship> attackBy(String playerId, Coordinate coordinates) {
    boolean isPlayerOne = gameState.isPlayerOne(playerId);
    if (isPlayerOne) {
      // gameState.playerOnesAttacks.add(coordinates);
      return playerTwoGameboard.receiveAttack(coordinates);
    } else {
      // gameState.playerTwosAttacks.add(coordinates);
      return playerOneGameboard.receiveAttack(coordinates);
    }
  }

  public GameRoom update(GameRoom gameRoom, String id) {

    GameState gameStateFromRoom = gameRoom.getGameState();

    boolean isPlayerOne = gameState.isPlayerOne(id);
    boolean isPlayerTwo = gameState.isPlayerTwo(id);

    if (isPlayerOne)
      gameStateFromRoom.setPlayerOnesAttacks(gameState.getPlayerOnesAttacks());
    else if (isPlayerTwo)
      gameStateFromRoom.setPlayerTwosAttacks((gameState.getPlayerTwosAttacks()));

    gameState = gameStateFromRoom;

    return gameRoom;
  }

  public void resetGameState(GameState gameState) {
    this.gameState = gameState;
    this.playerOneGameboard = new Gameboard();
    this.playerTwoGameboard = new Gameboard();
  }

  public boolean allPlacementsComplete() {
    return gameState.allPlacementsComplete();
  }

  public Gameboard getPlayerOneGameboard() {
    return this.playerOneGameboard;
  }

  public Gameboard getPlayerTwoGameboard() {
    return this.playerTwoGameboard;
  }

  public Gameboard getBoardById(String id) {
    boolean isPlayerOne = id.equals(gameState.getPlayerOneId());
    if (isPlayerOne) return this.playerOneGameboard;
    else return this.playerTwoGameboard;
  }

  public Gameboard getMyOpponentsBoard(String id) {
    boolean isPlayerOne = id.equals(gameState.getPlayerOneId());
    if (isPlayerOne) return this.playerTwoGameboard;
    else return this.playerOneGameboard;
  }

  public void loadDataToBoard(GameState gameState) {
    resetGameState(gameState);

    for (Ship ship : gameState.playerOneShipList) {
      playerOneGameboard.placeShip(ship);
    }

    for (Ship ship : gameState.playerTwoShipList) {
      playerTwoGameboard.placeShip(ship);
    }

    for (Coordinate c : gameState.playerOnesAttacks) {
      playerTwoGameboard.receiveAttack(c);
    }

    for (Coordinate c : gameState.playerTwosAttacks) {
      playerOneGameboard.receiveAttack(c);
    }
  }

  public void addAttackResult(String id, byte row, byte col, PacketType result) {
    boolean isPlayerOne = gameState.isPlayerOne(id);
    if (isPlayerOne) gameState.playerOnesAttacks.add(new CoordinateAttack(row, col, result));
    else gameState.playerTwosAttacks.add(new CoordinateAttack(row, col, result));
  }

  public List<Ship> getPlayerOnesShips() {
    return playerOneGameboard.getShips();
  }

  public List<Ship> getPlayerTwosShips() {
    return playerTwoGameboard.getShips();
  }

  public List<Ship> getPlayerOnesSunkShips() {
    return playerOneGameboard.getSunkShips();
  }

  public List<Ship> getPlayerTwosSunkShips() {
    return playerTwoGameboard.getSunkShips();
  }

  public List<CoordinateAttack> getPlayerOnesAttackResults() {
    return gameState.getPlayerOnesAttacks();
  }

  public List<CoordinateAttack> getPlayerTwosAttackResults() {
    return gameState.getPlayerTwosAttacks();
  }

  public boolean opponentAllSunk(String id) {
    Gameboard gb = getMyOpponentsBoard(id);
    return gb.allSunk();
  }

  public String getPlayerOneId() {
    return this.gameState.getPlayerOneId();
  }

  public String getPlayerTwoId() {
    return this.gameState.getPlayerTwoId();
  }

  @Override
  public String toString() {
    return "GameService: " + String.valueOf(this.hashCode()) + " with boards " + playerOneGameboard.hashCode() + " " + playerTwoGameboard.hashCode();
  }

}
