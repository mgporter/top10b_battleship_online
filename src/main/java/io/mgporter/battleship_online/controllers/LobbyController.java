package io.mgporter.battleship_online.controllers;

import org.springframework.asm.Handle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.MessagingAdviceBean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import io.mgporter.battleship_online.models.ApplicationState;
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.packets.GamePacket;
import io.mgporter.battleship_online.packets.PacketType;
import io.mgporter.battleship_online.services.LobbyService;

@Controller
@RestController
@RequestMapping("/gamerooms")
@CrossOrigin(origins = "*")
public class LobbyController {
  
  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;

  public LobbyController(LobbyService lobbyService, SimpMessagingTemplate messagingTemplate) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
  }

  @GetMapping
  public ResponseEntity<List<GameRoom>> getAllGameRooms() {
    return new ResponseEntity<List<GameRoom>>(lobbyService.getAllRooms(), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<GameRoom> createGameRoom(@RequestBody Map<String, String> payload) {

    Player player = new Player(payload.get("id"), payload.get("playerName"));

    GameRoom newRoom = lobbyService.createGameRoom();

    Message message = Message.fromSenderTypeRoomnumber(player, MessageType.CREATEDGAME, newRoom.getRoomNumber());

    messagingTemplate.convertAndSend("/lobby", message);

    return new ResponseEntity<>(newRoom, HttpStatus.CREATED);
  }

  @MessageMapping("/joinLobby")
  public Message addUserToLobby(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
    Player newPlayer = new Player(message.getSender().getId(), message.getSender().getName());
    Message outMessage = Message.fromSenderAndType(newPlayer, MessageType.JOINLOBBY);

    headerAccessor.getSessionAttributes().put("username", newPlayer.getName());
    headerAccessor.getSessionAttributes().put("id", newPlayer.getId());
    messagingTemplate.convertAndSend("/lobby", outMessage);

    return outMessage;
  }

  @MessageMapping("/changeName")
  public void changePlayerName(@Payload String newName, SimpMessageHeaderAccessor headerAccessor) {
    headerAccessor.getSessionAttributes().replace("username", newName);
    System.out.println("Player name changed to " + newName);
  }


}
