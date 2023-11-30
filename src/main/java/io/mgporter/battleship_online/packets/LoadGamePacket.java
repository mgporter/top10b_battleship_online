package io.mgporter.battleship_online.packets;

import java.util.List;

import io.mgporter.battleship_online.enums.PacketType;
import io.mgporter.battleship_online.models.CoordinateAttack;
import io.mgporter.battleship_online.models.Ship;
import lombok.Data;

@Data
public class LoadGamePacket {
  public PacketType type = PacketType.LOAD_ALL_DATA;
  public boolean goFirst;
  public boolean opponentHasPlaced;
  public byte opponentShipsPlacedCount;
  public List<Ship> myShips;
  public List<Ship> opponentSunkShips;
  public List<CoordinateAttack> myAttacks;
  public List<CoordinateAttack> opponentAttacks;
}
