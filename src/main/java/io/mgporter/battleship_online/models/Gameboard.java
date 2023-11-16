package io.mgporter.battleship_online.models;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

/* The Gameboard stores the logic needed to add and attack ships */

@Component
public class Gameboard {
  
  private List<List<Cell>> board;
  private List<Ship> ships;
  private byte sunkShips;

  public Gameboard() {
    this.board = generateGameBoard();
    this.ships = new ArrayList<>(Constants.MAXSHIPS);
    this.sunkShips = 0;
  }

  private List<List<Cell>> generateGameBoard() {
    List<List<Cell>> newBoard = new ArrayList<>(Constants.ROWS);

    for (int i = 0; i < Constants.ROWS; i++) {
      List<Cell> row = new ArrayList<>(Constants.COLS);
      for (int j = 0; j < Constants.COLS; j++) {
        row.add(new Cell());
      }
      newBoard.add(row);
    }

    return newBoard;
  }

  public List<Ship> getShips() {
    return ships;
  }

  private boolean isInBounds(Coordinate coord) {
    return coord.getRow() >= 0 && coord.getRow() < Constants.ROWS && coord.getCol() >= 0 && coord.getCol() < Constants.COLS;
  }

  private Cell getCellByCoordinate(Coordinate coord) {
    return board.get(coord.getRow()).get(coord.getCol());
  }

  public void placeShip(Ship ship) {

    int coordCount = ship.getLength();

    List<Coordinate> coords = ship.getLocation();

    // We do not need to do this check on the server.
    /* if (ship.getLength() != coordCount) 
      throw new Error("Ship's length does not match coordinates"); */

    for (int i = 0; i < coordCount; i++) {

      Coordinate c = coords.get(i);

      if (!isInBounds(c)) continue;

      Cell cell = getCellByCoordinate(c);
      cell.addShip(ship, (byte) (i+1));
    }

    addShipToList(ship);
  }

  private void addShipToList(Ship ship) {
    ships.add(ship);
  }

  public Optional<Ship> receiveAttack(Coordinate coord) {
    Cell cell = getCellByCoordinate(coord);

    // We do not need to do this check on the server
    /* if (cell.isAlreadyHit()) throw new Error("Cell has already been attacked"); */

    Optional<Ship> ship = Optional.ofNullable(cell.getShip());
    
    ship.ifPresent((s) -> {
      s.receiveHit(cell);
      if (s.isSunk()) sunkShips++;
    });

    System.out.println(getSunkShips());

    return ship;
  }

  public List<Ship> getSunkShips() {
    return ships.stream().filter(ship -> ship.isSunk()).collect(Collectors.toList());
  }

  public boolean allPlaced() {
    return ships.size() == Constants.MAXSHIPS;
  }

  public boolean allSunk() {
    return sunkShips == ships.size();
  }

  @Override
  public String toString() {
    return "Gameboard with " + ships.size() + " ships";
  }

}
