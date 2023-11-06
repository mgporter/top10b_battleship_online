package io.mgporter.battleship_online.config;

import java.security.Principal;

import lombok.Data;

@Data
public class StompPrincipal implements Principal {

  private final String id;
  private String name;
  private int roomNumber;

  // public StompPrincipal(String id, String name) {
  //   super(id, name);
  // }

  public StompPrincipal(String id, String name) {
    this.id = id;
    this.name = name;
    this.roomNumber = -1;
  }

  public static StompPrincipal fromId(String id) {
    return new StompPrincipal(id, "Player#" + id.substring(0, 5).toUpperCase());
  }

  @Override
  public String getName() {
    return id;
  }

  public void setPlayerName(String name) {
    this.name = name;
  }

  public String getPlayerId() {
    return id;
  }

  public String getPlayerName() {
    return name;
  }

  public boolean isInRoom() {
    return roomNumber >= 1000;
  }

  @Override
  public String toString() {
    return "Stomp Principal (id=" + id + ", name=" + name + ")";
  }
  // String name;
  // String playerId;

  // public StompPrincipal(String playerId) {
  //   this.name = "Player#" + playerId.substring(0, 5);
  //   this.playerId = playerId;
  // }

  // @Override
  // public String getName() {
  //   return userId;
  // }
}
