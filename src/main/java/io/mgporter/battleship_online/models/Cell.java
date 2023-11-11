package io.mgporter.battleship_online.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Cell {
  
  private boolean hasShip = false;
  private Ship ship = null;
  private boolean alreadyHit = false;
  private byte shipPartNumber;

  public void addShip(Ship ship, byte partNum) {
    this.hasShip = true;
    this.ship = ship;
    this.shipPartNumber = partNum;
  }

  public void removeShip() {
    this.hasShip = false;
    this.ship = null;
  }

}
