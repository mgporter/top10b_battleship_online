package io.mgporter.battleship_online.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.services.GameRoomService;

@Controller
@RestController
@RequestMapping("/gamerooms")
@CrossOrigin(origins = "*")
public class LobbyController {
  
  private final GameRoomService gameRoomService;
  private final SimpMessagingTemplate messagingTemplate;

  public LobbyController(GameRoomService gameRoomService, SimpMessagingTemplate messagingTemplate) {
    this.gameRoomService = gameRoomService;
    this.messagingTemplate = messagingTemplate;
  }

  @GetMapping
  public ResponseEntity<List<GameRoom>> getAllGameRooms() {
    return new ResponseEntity<List<GameRoom>>(gameRoomService.getAllRooms(), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<GameRoom> createGameRoom(@RequestBody Map<String, String> payload) {

    Player player = new Player(payload.get("id"), payload.get("playerName"));

    GameRoom newRoom = gameRoomService.createGameRoom(player);

    Message message = new Message(player, MessageType.CREATEDGAME, newRoom);
    messagingTemplate.convertAndSend("/lobby", message);

    return new ResponseEntity<>(newRoom, HttpStatus.CREATED);
  }

  @MessageMapping("/joinLobby")
  public Message addUserToLobby(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
    System.out.println(message);
    Player newPlayer = new Player(message.getSender().getId(), message.getSender().getName());
    Message outMessage = new Message(newPlayer, MessageType.JOINLOBBY, null);

    headerAccessor.getSessionAttributes().put("username", message.getSender().getName());
    messagingTemplate.convertAndSend("/lobby", outMessage);

    return outMessage;
  }

  @MessageMapping("/changeName")
  public void changePlayerName(@Payload String newName, SimpMessageHeaderAccessor headerAccessor) {
    // Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
    // String oldName = (String) sessionAttributes.get("username");
    headerAccessor.getSessionAttributes().replace("username", newName);
    System.out.println("Player name changed to " + newName);
  }

  @MessageMapping("/joinGame")
  public Message joinGame(@Payload Message message) {
    System.out.println(message.getSender().getName() + " has joined a game");
    Message outMessage = new Message(new Player(message.getSender().getId(), message.getSender().getName()), MessageType.JOINGAME, message.getGame());

    messagingTemplate.convertAndSend("/lobby", outMessage);
    return outMessage;
  }

}
