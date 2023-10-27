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
import java.util.UUID;

import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.services.GameRoomService;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
@RestController
@RequestMapping("/gamerooms")
@CrossOrigin(origins = "*")
public class GameRoomController {
  
  private final GameRoomService gameRoomService;
  private final SimpMessagingTemplate messagingTemplate;

  public GameRoomController(GameRoomService gameRoomService, SimpMessagingTemplate messagingTemplate) {
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
    System.out.println(player);
    Message message = new Message(player, MessageType.CREATEDGAME);
    messagingTemplate.convertAndSend("/lobby", message);

    return new ResponseEntity<>(gameRoomService.createGameRoom(player), HttpStatus.CREATED);
  }

  // @GetMapping("/getNewId")
  // public ResponseEntity<Player> getNewPlayerId() {
  //   System.out.println("get new id called");
  //   Player player = new Player();
  //   player.setId(UUID.randomUUID().toString());
  //   player.setName("Player#" + player.getId().substring(0, 5).toUpperCase());
    
  //   return new ResponseEntity<>(player, HttpStatus.OK);
  // }

  // @MessageMapping("/createGame")
  // public GameRoom createGameRoom(@Payload Message message) {

  //   Player player = message.getSender();
  //   GameRoom gameRoom = gameRoomService.createGameRoom(player);
    
  //   messagingTemplate.convertAndSend("/lobby", gameRoom);

  //   return gameRoom;
  // }

  // Add , SimpMessageHeaderAccessor headerAccessor ??
  @MessageMapping("/joinLobby")
  public Message addUserToLobby(@Payload Message message) {
    System.out.println(message);
    Player newPlayer = new Player(message.getSender().getId(), message.getSender().getName());
    Message javaMessage = new Message(newPlayer, MessageType.JOINLOBBY);

    // headerAccessor.getSessionAttributes().put("user", message.getSender());
    messagingTemplate.convertAndSend("/lobby", javaMessage);

    return javaMessage;
  }



}
