package io.mgporter.battleship_online.packets;

import java.util.List;
import java.util.Map;

import io.mgporter.battleship_online.models.Coordinate;
import io.mgporter.battleship_online.models.Ship;
import io.mgporter.battleship_online.models.ShipType;

public class PlacementPacket {
  public String playerId;
  public int roomNumber;
  public PacketType type;
  public List<Ship> placementList;
  // public Map<ShipType, List<Coordinate>> placementList;
}
