package io.mgporter.battleship_online.packets;

import java.util.List;

import io.mgporter.battleship_online.enums.PacketType;
import io.mgporter.battleship_online.models.Ship;

public class PlacementPacket {
  public String playerId;
  public PacketType type;
  public byte shipsPlacedCount;
  public List<Ship> placementList;
}
