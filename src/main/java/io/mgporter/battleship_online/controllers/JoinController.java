package io.mgporter.battleship_online.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import io.mgporter.battleship_online.config.StompPrincipal;
import io.mgporter.battleship_online.models.ApplicationState;
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.packets.GamePacket;
import io.mgporter.battleship_online.packets.PacketType;
import io.mgporter.battleship_online.packets.PlayerListPacket;
import io.mgporter.battleship_online.services.GameService;
import io.mgporter.battleship_online.services.LobbyService;

@Controller
@RestController
@RequestMapping("/gamerooms")
@CrossOrigin(origins = "*")
public class JoinController {
  
  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;

  public JoinController(
    LobbyService lobbyService, 
    SimpMessagingTemplate messagingTemplate) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/joinGame")
  public void joinGame(@Payload Message message, SimpMessageHeaderAccessor headerAccessor, StompPrincipal principal) {

    Player player = Player.fromPrincipal(principal);
    // Player player = new Player(message.getSender().getId(), message.getSender().getName());
    int roomNumber = message.getRoomNumber();

    // Add the room to the headerAccessor
    if (principal.isInRoom()) {
      Message rejectionMessage = Message.fromSenderAndType(player, MessageType.ERROR_ONEGAMEONLY);
      messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/message", rejectionMessage);
    } else {
      principal.setRoomNumber(roomNumber);
    }

    // Send message to lobby
    System.out.println(message.getSender().getName() + " has joined game " + roomNumber);
    Message outMessage = Message.fromSenderTypeRoomnumber(player, MessageType.JOINGAME, roomNumber);
    messagingTemplate.convertAndSend("/lobby", outMessage);

    // Add the new player to the game's playerlist and update the database
    GameRoom gameRoom = lobbyService.joinGameRoom(player, roomNumber);

    // Inform everybody else in the gameroom on the new player arrangement
    sendUpdatedPlayerList(gameRoom);

    // Send out the GAME_START packet if the gamestate is ready (this is, has two players)
    if (gameRoom.getGameState().bothPlayersReady()) {
      messagingTemplate.convertAndSend("/game/" + roomNumber, new GamePacket(PacketType.GAME_START));
    }

  }

  @EventListener
  public void playerLeftGame(SessionDisconnectEvent event) {
    System.out.println("This Join Controller got the session disconnect event!");
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    StompPrincipal principal = (StompPrincipal) headerAccessor.getUser();
    Player player = Player.fromPrincipal(principal);

    // System.out.println("Session disconnect event called. Event object: " + headerAccessor);
    // Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
    // String username = (String) sessionAttributes.get("username");
    // String id = (String) sessionAttributes.get("id");
    // headerAccessor.
    // Player player = new Player(id, username);

    // int roomNumber;
    // if (sessionAttributes.containsKey("room")) roomNumber = (int) sessionAttributes.get("room");
    // else roomNumber = -1;

    if (principal.isInRoom()) {
      System.out.println("leave game function called");
      leaveGame(player, principal.getRoomNumber());
    } else {   // if the player is not in a room
      sendDisconnectMessageToLobby(player);
    }
  }

  public void sendDisconnectMessageToLobby(Player player) {
    System.out.println(player.getName() + " left the lobby");
    Message message = new Message();
    message.setSender(player);
    message.setMessageType(MessageType.EXITEDLOBBY);
    messagingTemplate.convertAndSend("/lobby", message);
  }

  public void leaveGame(Player player, int roomNumber) {

    // Remove the player from the game room, and get a reference to the gameroom back
    GameRoom gameRoom = lobbyService.leaveGameRoom(player, roomNumber);

    System.out.println("Leave game function gameroom: " + gameRoom);

    // Send a message to the lobby
    Message message = Message.fromSenderTypeRoomnumber(player, MessageType.EXITEDGAME, roomNumber);
    messagingTemplate.convertAndSend("/lobby", message);

    // Delete the game room if the last person has left. Otherwise, send a PlayerList update to the other players in the room
    if (gameRoom.getPlayerList().size() == 0) {
      System.out.println("Deleting game number: " + roomNumber);
      lobbyService.deleteGameRoom(roomNumber);
    } else {
      sendUpdatedPlayerList(gameRoom);
    }
  }

  public void sendUpdatedPlayerList(GameRoom gameRoom) {
    // Create the playerlist update packet and send it to everybody in the room
    PlayerListPacket packet = new PlayerListPacket(
      gameRoom.getPlayerList(),
      gameRoom.getPlayerOneId(),
      gameRoom.getPlayerTwoId(),
      PacketType.PLAYERLIST_UPDATE
    );
    System.out.println("Sending player list packet: " + packet);
    messagingTemplate.convertAndSend("/game/" + gameRoom.getRoomNumber(), packet);
  }

}
