package io.mgporter.battleship_online.packets;

import io.mgporter.battleship_online.models.Player;
import lombok.AllArgsConstructor;
import java.util.List;

/* The server can use this packet to announce the players names to each other when they join the gameroom*/

@AllArgsConstructor
public class PlayerListPacket {
  
  public List<Player> playerList;
  public String playerOneId;
  public String playerTwoId;
  public PacketType type;

}
