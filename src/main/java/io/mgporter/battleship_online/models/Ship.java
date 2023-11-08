package io.mgporter.battleship_online.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Ship {
  
  private final byte shipId;
  private final ShipType type;
  private final byte direction;
  private byte hits;
  private List<Coordinate> location;
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

  public Coordinate getStartingCoordinate() {
    return this.location.get(0);
  }

  public Ship(byte shipId, ShipType type, byte direction) {
    this.shipId = shipId;
    this.type = type;
    this.direction = direction;
    this.hits = 0;
    // this.isPlaced = false;
    // this.partsHit = new ArrayList<>(length);
  }

}
