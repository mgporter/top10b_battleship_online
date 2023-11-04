package io.mgporter.battleship_online.models;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;

public class Gameboard {
  
  private List<List<Cell>> board;
  private List<Ship> ships;

  public Gameboard() {
    this.board = generateGameBoard();
    this.ships = new ArrayList<>(Constants.maxShips);
  }

  private List<List<Cell>> generateGameBoard() {
    List<List<Cell>> newBoard = new ArrayList<>(Constants.rows);

    for (int i = 0; i < Constants.rows; i++) {
      List<Cell> row = new ArrayList<>(Constants.cols);
      for (int j = 0; j < Constants.cols; j++) {
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
    return coord.row >= 0 && coord.row < Constants.rows && coord.col >= 0 && coord.col < Constants.cols;
  }

  private Cell getCellByCoordinate(Coordinate coord) {
    return board.get(coord.row).get(coord.col);
  }

  public void placeShip(Ship ship) {

    int coordCount = ship.getLength();

    List<byte[]> coords = ship.getLocation();

    if (ship.getLength() != coordCount) throw new Error("Ship's length does not match coordinates");

    for (int i = 0; i < coordCount; i++) {
      Coordinate c = new Coordinate(coords.get(i)[0], coords.get(i)[1]);

      if (!isInBounds(c)) throw new Error(
        "Out of bounds coordinate: " + c.row + ", " + c.col
      );

      // if (!isInBounds(c)) continue;

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

    if (cell.isAlreadyHit()) throw new Error("Cell has already been attacked");

    Optional<Ship> ship = Optional.ofNullable(cell.getShip());
    ship.ifPresent((s) -> s.receiveHit(cell));

    return ship;
  }

  public boolean allPlaced() {
    return ships.size() == Constants.maxShips;
  }

}
