package io.mgporter.battleship_online.packets;

import io.mgporter.battleship_online.enums.PacketType;
import io.mgporter.battleship_online.enums.ShipType;

public class AttackPacket {

   public String playerId;
   public int roomNumber;
   public PacketType type;
   public PacketType result;
   public byte row;
   public byte col;
   public ShipType shipType;
   public byte direction;
   public byte startingRow;
   public byte startingCol;

   public static AttackPacket createSunkshipPacketFromPacket(AttackPacket packet) {
      AttackPacket p = new AttackPacket();
      p.playerId = packet.playerId;
      p.roomNumber = packet.roomNumber;
      p.type = PacketType.ATTACK;
      p.result = PacketType.SUNKSHIP_INFO;
      p.shipType = packet.shipType;
      return p;
   }
}
