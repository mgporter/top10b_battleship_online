package io.mgporter.battleship_online.models;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Gameboard {
  
  private List<List<Cell>> board;
  private List<Ship> ships;
  private byte sunkShips;

  public Gameboard() {
    this.board = generateGameBoard();
    this.ships = new ArrayList<>(Constants.maxShips);
    this.sunkShips = 0;
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
    return coord.getRow() >= 0 && coord.getRow() < Constants.rows && coord.getCol() >= 0 && coord.getCol() < Constants.cols;
  }

  private Cell getCellByCoordinate(Coordinate coord) {
    return board.get(coord.getRow()).get(coord.getCol());
  }

  public void placeShip(Ship ship) {

    int coordCount = ship.getLength();

    List<Coordinate> coords = ship.getLocation();

    if (ship.getLength() != coordCount) throw new Error("Ship's length does not match coordinates");

    for (int i = 0; i < coordCount; i++) {
      // Coordinate c = new Coordinate(coords[i][0], coords[i][1]);

      Coordinate c = coords.get(i);

      if (!isInBounds(c)) throw new Error(
        "Out of bounds coordinate: " + c.getRow() + ", " + c.getCol()
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

    System.out.println("GameBoard receive attack function called!");
    for (Ship s : ships) {
      System.out.println(s.getLocation());
    }

    return ship;
  }



  public boolean allPlaced() {
    return ships.size() == Constants.maxShips;
  }

  public boolean allSunk() {
    return sunkShips == ships.size();
  }

  @Override
  public String toString() {
    return "Gameboard with " + ships.size() + " ships";
  }

  // public boolean getSunkShipCount() {
  //   ships.stream().filter(s -> s.isSunk()).count();
  // }

}
