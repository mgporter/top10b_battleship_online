package io.mgporter.battleship_online.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Ship {
  
  private final byte shipId;
  private final ShipType type;
  private byte hits;
  private List<byte[]> location;
  // private boolean isPlaced;
  // private Direction direction;
  // private List<Byte> partsHit;

  public void receiveHit(Cell cell) {
    this.hits++;
    // this.partsHit.add(cell.getShipPartNumber());
  }

  public boolean isSunk() {
    return this.hits >= this.getLength();
  }

  public int getLength() {
    return this.location.size();
  }

  public Ship(byte shipId, ShipType type) {
    this.shipId = shipId;
    this.type = type;
    this.hits = 0;
    // this.isPlaced = false;
    // this.partsHit = new ArrayList<>(length);
  }

}
