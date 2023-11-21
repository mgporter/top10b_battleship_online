package io.mgporter.battleship_online.packets;

import java.util.List;

import io.mgporter.battleship_online.enums.MessageType;
import io.mgporter.battleship_online.models.GameRoom;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameRoomList {
  public MessageType type;
  public List<GameRoom> gameRoomList;  
}
