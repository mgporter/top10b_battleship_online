package io.mgporter.battleship_online.models;

public class CreateShip {
  
  public static Ship createBattleShip(byte id) {
    return new Ship(id, ShipType.BATTLESHIP);
  }

  public static Ship createCarrier(byte id) {
    return new Ship(id, ShipType.CARRIER);
  }

  public static Ship createPatrolBoat(byte id) {
    return new Ship(id, ShipType.PATROLBOAT);
  }

  public static Ship createSubmarine(byte id) {
    return new Ship(id, ShipType.SUBMARINE);
  }

  public static Ship createDestroyer(byte id) {
    return new Ship(id, ShipType.DESTROYER);
  }

}
