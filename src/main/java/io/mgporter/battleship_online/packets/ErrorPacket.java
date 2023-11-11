package io.mgporter.battleship_online.packets;

import io.mgporter.battleship_online.enums.PacketType;

public class ErrorPacket {
  public PacketType type = PacketType.ERROR;
  public String message;

  public ErrorPacket(String message) {
    this.message = message;
  }
}
