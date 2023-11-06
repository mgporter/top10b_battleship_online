package io.mgporter.battleship_online.services;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import io.mgporter.battleship_online.models.Coordinate;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Gameboard;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.models.Ship;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
// @SessionScope
@Data
public class GameService {
  
  private GameState gameState;
  private Gameboard playerOneGameboard;
  private Gameboard playerTwoGameboard;

  public GameService() {
    this.playerOneGameboard = new Gameboard();
    this.playerTwoGameboard = new Gameboard();
  }

  public void addShipsToBoard(String playerId, List<Ship> ships) {
    boolean isPlayerOne = gameState.getPlayer(playerId);
    // Gameboard board = isPlayerOne ? playerOneGameboard : playerTwoGameboard;

    // for (Ship ship : ships) {
    //   board.placeShip(ship);
    // }

    if (isPlayerOne) gameState.setPlayerOneShipList(ships);
    else gameState.setPlayerTwoShipList(ships);

  }

  public Optional<Ship> attack(String playerId, Coordinate coordinates) {
    boolean isPlayerOne = gameState.getPlayer(playerId);
    if (isPlayerOne) {
      gameState.playerOnesAttacks.add(coordinates);
      return playerTwoGameboard.receiveAttack(coordinates);
    } else {
      gameState.playerTwosAttacks.add(coordinates);
      return playerOneGameboard.receiveAttack(coordinates);
    }
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
    boolean isPlayerTwo = id.equals(gameState.getPlayerTwoId());
    if (!isPlayerOne && !isPlayerTwo) throw new Error("Incorrect ID passed");
    if (isPlayerOne) return this.playerOneGameboard;
    else return this.playerTwoGameboard;
  }

  public Gameboard getMyOpponentsBoard(String id) {
    boolean isPlayerOne = id.equals(gameState.getPlayerOneId());
    boolean isPlayerTwo = id.equals(gameState.getPlayerTwoId());
    if (!isPlayerOne && !isPlayerTwo) throw new Error("Incorrect ID passed");
    if (isPlayerOne) return this.playerTwoGameboard;
    else return this.playerOneGameboard;
  }

  public void loadDataToBoard(GameState gameState) {
    System.out.println("loading data to board!");
    this.gameState = gameState;

    for (Ship ship : gameState.playerOneShipList) {
      playerOneGameboard.placeShip(ship);
    }

    for (Ship ship : gameState.playerTwoShipList) {
      playerTwoGameboard.placeShip(ship);
    }

    for (Coordinate c : gameState.playerOnesAttacks) {
      playerOneGameboard.receiveAttack(c);
    }

    for (Coordinate c : gameState.playerTwosAttacks) {
      playerTwoGameboard.receiveAttack(c);
    }
  }

  @Override
  public String toString() {
    return "GameService: " + String.valueOf(this.hashCode()) + " with boards " + playerOneGameboard.hashCode() + " " + playerTwoGameboard.hashCode();
  }

}
