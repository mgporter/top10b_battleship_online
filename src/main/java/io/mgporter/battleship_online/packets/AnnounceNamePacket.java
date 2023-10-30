package io.mgporter.battleship_online.packets;

import lombok.AllArgsConstructor;

/* The server can use this packet to announce the players names to each other when they join the gameroom*/

@AllArgsConstructor
public class AnnounceNamePacket {
  
  public String id;
  public String name;
  public PacketType type;

}
