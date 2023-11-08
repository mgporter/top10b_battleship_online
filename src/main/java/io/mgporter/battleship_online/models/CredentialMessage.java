package io.mgporter.battleship_online.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CredentialMessage {
  public String name;
  public String id;
  public MessageType messageType;
}
