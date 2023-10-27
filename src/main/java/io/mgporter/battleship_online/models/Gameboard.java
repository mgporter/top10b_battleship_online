package io.mgporter.battleship_online.models;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;

public class Gameboard {
  
  private final List<List<Cell>> board;
  private final List<Ship> ships;
  private final String ownerId;

  public Gameboard(String id) {
    this.board = new ArrayList<>(Constants.rows);

    for (int i = 0; i < Constants.rows; i++) {
      List<Cell> row = new ArrayList<>(Constants.cols);
      for (int j = 0; j < Constants.cols; j++) {
        row.add(new Cell());
      }
      board.add(row);
    }

    this.ships = new ArrayList<>(Constants.maxShips);
    this.ownerId = id;
  }

  public List<Ship> getShips() {
    return ships;
  }

  public String getOwnerId() {
    return ownerId;
  }

  private boolean isInBounds(Coordinate coord) {
    return coord.row >= 0 && coord.row < Constants.rows && coord.col >= 0 && coord.col < Constants.cols;
  }

  private Cell getCellByCoordinate(Coordinate coord) {
    return board.get(coord.row).get(coord.col);
  }

  public void placeShip(Ship ship, List<Coordinate> coords) {
    // coords[0] is the starting point, the bow of the ship

    int coordCount = coords.size();

    if (ship.getLength() != coordCount) throw new Error("Ship's length does not match coordinates");

    for (int i = 0; i < coordCount; i++) {
      Coordinate c = coords.get(i);
      if (!isInBounds(c)) throw new Error(
        "Out of bounds coordinate: " + coords.get(i).row + ", " + coords.get(i).col
      );

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
    ship.ifPresent((s) -> s.receiveHit());

    return ship;
  }

}