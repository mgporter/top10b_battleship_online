package io.mgporter.battleship_online.controllers;

import java.util.Map;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.services.LobbyService;

@Component
public class WSEventListener {

  private final SimpMessagingTemplate messageTemplate;
  private final JoinController joinController;

  public WSEventListener(SimpMessagingTemplate messagingTemplate, JoinController joinController) {
    this.messageTemplate = messagingTemplate;
    this.joinController = joinController;
  }

  // @EventListener
  // public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
  //   StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
  //   System.out.println("Session disconnect event called. Event object: " + headerAccessor);
  //   Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
  //   String username = (String) sessionAttributes.get("username");
  //   String id = (String) sessionAttributes.get("id");
  //   Player player = new Player(id, username);

  //   int roomNumber;
  //   if (sessionAttributes.containsKey("room")) roomNumber = (int) sessionAttributes.get("room");
  //   else roomNumber = -1;
  //   System.out.println(roomNumber);

  //   if (roomNumber != -1) {
  //     // leaveGame(player, roomNumber);
  //   }
  //   if (username != null) {
  //     sendDisconnectMessageToLobby(player);
  //   }
  // }

  // public void sendDisconnectMessageToLobby(Player player) {
  //   System.out.println(player.getName() + " left the lobby");
  //   Message message = new Message();
  //   message.setSender(player);
  //   message.setMessageType(MessageType.EXITEDLOBBY);
  //   messageTemplate.convertAndSend("/lobby", message);
  // }

}