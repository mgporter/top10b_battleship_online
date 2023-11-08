package io.mgporter.battleship_online.models;

import java.util.List;

import io.mgporter.battleship_online.config.StompPrincipal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
  private Player sender;
  private MessageType messageType;
  private int roomNumber;
  private List<Player> playerList;

  public static Message fromSenderAndType(Player player, MessageType type) {
    Message message = new Message();
    message.setSender(player);
    message.setMessageType(type);
    return message;
  }

  public static Message fromSenderTypeRoomnumber(Player player, MessageType type, int roomNumber) {
    Message message = new Message();
    message.setSender(player);
    message.setMessageType(type);
    message.setRoomNumber(roomNumber);
    return message;
  }

  public static Message fromPrincipalAndType(StompPrincipal principal, MessageType type) {
    Message message = new Message();
    message.setSender(new Player(principal.getPlayerId(), principal.getPlayerName()));
    message.setMessageType(type);
    if (principal.isInRoom()) message.setRoomNumber(principal.getRoomNumber());
    return message;
  }

}
