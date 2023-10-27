package io.mgporter.battleship_online.models;

import lombok.Data;

@Data
public class Ship {
  
  private final byte shipId;
  private final ShipType type;
  private final String displayName;
  private final byte length;
  private byte hits;
  private boolean isPlaced;
  private Direction direction;

  public void receiveHit() {
    this.hits++;
  }

  public boolean isSunk() {
    return this.hits >= length;
  }

  public Ship(byte shipId, ShipType type, String displayName, byte length) {
    this.shipId = shipId;
    this.type = type;
    this.length = length;
    this.displayName = displayName;
    this.hits = 0;
    this.isPlaced = false;
  }

}
