package io.mgporter.battleship_online.models;

public class Coordinate {
  public final byte row;
  public final byte col;

  public Coordinate(byte row, byte col) {
    this.row = row;
    this.col = col;
  }

  public byte getRow() {
    return row;
  }

  public byte getCol() {
    return col;
  }

  @Override
  public String toString() {
    return "Coordinate (" + row + ", " + col + ")";
  }
}
