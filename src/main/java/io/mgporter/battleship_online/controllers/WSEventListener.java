package io.mgporter.battleship_online.controllers;

import java.util.Map;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.services.LobbyService;

@Component
public class WSEventListener {

  private final SimpMessagingTemplate messageTemplate;
  private final LobbyService lobbyService;

  public WSEventListener(SimpMessagingTemplate messagingTemplate, LobbyService lobbyService) {
    this.messageTemplate = messagingTemplate;
    this.lobbyService = lobbyService;
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
    String username = (String) sessionAttributes.get("username");
    String id = (String) sessionAttributes.get("id");
    Player player = new Player(id, username);

    int roomNumber;
    if (sessionAttributes.containsKey("room")) roomNumber = (int) sessionAttributes.get("room");
    else roomNumber = -1;

    if (roomNumber != -1) {
      removePlayerFromGameRoom(player, roomNumber);
    } else if (username != null) {
      sendDisconnectMessageToLobby(player);
    }
  }

  public void sendDisconnectMessageToLobby(Player player) {
    System.out.println(player.getName() + " left the lobby");
    Message message = new Message();
    message.setSender(player);
    message.setMessageType(MessageType.EXITEDLOBBY);
    messageTemplate.convertAndSend("/lobby", message);
  }

  public void removePlayerFromGameRoom(Player player, int roomNumber) {
    GameRoom gameRoom = lobbyService.getRoomById(roomNumber);
    if (gameRoom.getPlayerList().size() <= 1) {
      lobbyService.deleteGameRoom(roomNumber);
      Message message = new Message(player, MessageType.GAMEREMOVED, gameRoom);
      messageTemplate.convertAndSend("/lobby", message);
    } else {
      lobbyService.leaveGameRoom(player, roomNumber);
      Message message = new Message(player, MessageType.EXITEDGAME, gameRoom);
      messageTemplate.convertAndSend("/lobby", message);
    }
  }


}