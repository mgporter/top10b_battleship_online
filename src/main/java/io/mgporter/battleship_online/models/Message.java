package io.mgporter.battleship_online.models;

import java.util.List;

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

}
