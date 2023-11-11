package io.mgporter.battleship_online.models;

import io.mgporter.battleship_online.enums.MessageType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CredentialMessage {
  public String name;
  public String id;
  public MessageType messageType;
}
