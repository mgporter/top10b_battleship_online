package io.mgporter.battleship_online.config;

import java.security.Principal;

import io.mgporter.battleship_online.models.Constants;
import lombok.Data;

/** 
 * The StompPrincipal object holds an id, name, and room number.
 * The id is returned from the {@code .getName()} function that Spring
 * expects for identifying the principal, such as the {@code .convertAndSendToUser} 
 * method.
 * 
 * Within the game and server, however, we use {@code id} as an id, and {@code name} as the user-chosen name.
 * To eliminate confusion, use the {@code .getPlayerId} and {@code .getPlayerName} methods.
 */

@Data
public class StompPrincipal implements Principal {

  private final String id;
  private final String DEFAULTNAME;
  private String name;
  private int roomNumber = -1;
  private boolean isInLobby = false;

  public StompPrincipal(String id) {
    this.id = id;
    this.DEFAULTNAME = "Player-" + id.substring(0, 5).toUpperCase();
    this.name = this.DEFAULTNAME;
  }

  public static StompPrincipal fromId(String id) {
    return new StompPrincipal(id);
  }

  @Override
  public String getName() {
    return id;
  }

  public String getPlayerName() {
    return name;
  }

  public void setPlayerName(String name) {
    if (name.equals("null") || name == null || name.length() == 0) {
      this.name = this.DEFAULTNAME;
    } else {
      this.name = name.substring(0, Math.min(Constants.MAXNAMELENGTH, name.length()));
    }
  }

  public String getPlayerId() {
    return id;
  }

  public boolean isInRoom() {
    return roomNumber >= 1000;
  }

  public void joinLobby() {
    this.roomNumber = -1;
  }

  public int removeRoomNumber() {
    int oldNumber = this.roomNumber;
    this.roomNumber = -1;
    return oldNumber;
  }

  @Override
  public String toString() {
    return "Stomp Principal (id=" + id + ", name=" + name + ", roomNumber=" + roomNumber + ")";
  }
}
