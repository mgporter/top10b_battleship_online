package io.mgporter.battleship_online.models;

public class CreateShip {
  
  public static Ship createBattleShip(byte id) {
    return new Ship(id, ShipType.BATTLESHIP, "Battleship", (byte) 4);
  }

  public static Ship createCarrier(byte id) {
    return new Ship(id, ShipType.CARRIER, "Carrier", (byte) 5);
  }

  public static Ship createPatrolBoat(byte id) {
    return new Ship(id, ShipType.PATROLBOAT, "Patrol Boat", (byte) 2);
  }

  public static Ship createSubmarine(byte id) {
    return new Ship(id, ShipType.SUBMARINE, "Submarine", (byte) 3);
  }

  public static Ship createDestroyer(byte id) {
    return new Ship(id, ShipType.DESTROYER, "Destroyer", (byte) 3);
  }

}
