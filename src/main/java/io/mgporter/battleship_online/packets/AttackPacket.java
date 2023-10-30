package io.mgporter.battleship_online.packets;

import io.mgporter.battleship_online.models.ShipType;

public class AttackPacket {

   public String playerId;
   public int roomNumber;
   public PacketType type;
   public byte row;
   public byte col;
   public ShipType shiptype;
   public boolean isSunk;
}
