package io.mgporter.battleship_online.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Ship {
  
  private final byte shipId;
  private final ShipType type;
  private final String displayName;
  private final byte length;
  private byte hits;
  private boolean isPlaced;
  // private Direction direction;
  private List<Byte> partsHit;

  public void receiveHit(Cell cell) {
    this.hits++;
    this.partsHit.add(cell.getShipPartNumber());
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
    this.partsHit = new ArrayList<>(length);
  }

}
