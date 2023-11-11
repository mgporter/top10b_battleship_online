package io.mgporter.battleship_online.packets;

import io.mgporter.battleship_online.enums.PacketType;
import io.mgporter.battleship_online.models.Player;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class PlayerListPacket {
  
  public List<Player> playerList;
  public String playerOneId;
  public String playerTwoId;
  public PacketType type;

}
