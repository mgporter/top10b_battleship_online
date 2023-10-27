package io.mgporter.battleship_online.controllers;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;

@Component
public class WSEventListener {

  private final SimpMessagingTemplate messageTemplate;

  public WSEventListener(SimpMessagingTemplate messagingTemplate) {
    this.messageTemplate = messagingTemplate;
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String username = (String) headerAccessor.getSessionAttributes().get("username");
    if (username != null) {
      System.out.println(username + " left the lobby");
      Message message = new Message();
      message.setSender(Player.fromName(username));
      message.setMessageType(MessageType.EXITEDLOBBY);
      messageTemplate.convertAndSend("/lobby", message);
    }
  }


}