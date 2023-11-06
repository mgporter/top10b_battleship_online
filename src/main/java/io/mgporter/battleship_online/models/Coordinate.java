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


/* public class Coordinate {
  public final byte[] coordinates;

  public Coordinate(byte row, byte col) {
    this.coordinates = new byte[] {row, col};
  }

  public byte getRow() {
    return coordinates[0];
  }

  public byte getCol() {
    return coordinates[1];
  }
} */
