package io.mgporter.battleship_online.packets;

import io.mgporter.battleship_online.enums.PacketType;

public class GameStartPacket {
   public PacketType type = PacketType.GAME_START;
   public boolean playerOneHasPlaced;  
   public boolean playerTwoHasPlaced;  

   public GameStartPacket(boolean playerOneHasPlaced, boolean playerTwoHasPlaced) {
    this.playerOneHasPlaced = playerOneHasPlaced;
    this.playerTwoHasPlaced = playerTwoHasPlaced;
   }
}
