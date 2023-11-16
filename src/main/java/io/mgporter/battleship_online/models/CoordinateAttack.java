package io.mgporter.battleship_online.models;

import io.mgporter.battleship_online.enums.PacketType;

public class CoordinateAttack extends Coordinate {
  
  public final PacketType result;

  public CoordinateAttack(byte row, byte col, PacketType result) {
    super(row, col);
    this.result = result;
  }

}
