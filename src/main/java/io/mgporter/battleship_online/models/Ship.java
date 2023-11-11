package io.mgporter.battleship_online.models;

import java.util.List;

import io.mgporter.battleship_online.enums.ShipType;
import lombok.Data;

@Data
public class Ship {
  
  private final byte shipId;
  private final ShipType type;
  private final byte direction;
  private byte hits;
  private List<Coordinate> location;

  public void receiveHit(Cell cell) {
    this.hits++;
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
  }

}
