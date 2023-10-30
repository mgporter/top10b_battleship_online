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

import io.mgporter.battleship_online.models.ApplicationState;
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.packets.AnnounceNamePacket;
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

    GameRoom newRoom = lobbyService.createGameRoom(player);

    Message message = new Message(player, MessageType.CREATEDGAME, newRoom);
    messagingTemplate.convertAndSend("/lobby", message);

    return new ResponseEntity<>(newRoom, HttpStatus.CREATED);
  }

  @MessageMapping("/joinLobby")
  public Message addUserToLobby(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
    Player newPlayer = new Player(message.getSender().getId(), message.getSender().getName());
    Message outMessage = new Message(newPlayer, MessageType.JOINLOBBY, null);

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

  @MessageMapping("/joinGame")
  public Message joinGame(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {

    Player player = new Player(message.getSender().getId(), message.getSender().getName());
    int roomNumber = message.getGame().getRoomNumber();

    // Add the room to the headerAccessor
    if (headerAccessor.getSessionAttributes().containsKey("room")) {
      return new Message(player, MessageType.ERROR_ONEGAMEONLY, message.getGame());
    } else {
      headerAccessor.getSessionAttributes().put("room", roomNumber);
    }

    // Send message to lobby
    System.out.println(message.getSender().getName() + " has joined a game");
    Message outMessage = new Message(player, MessageType.JOINGAME, message.getGame());
    messagingTemplate.convertAndSend("/lobby", outMessage);

    // Add the new player to the game's playerlist and update the database
    GameRoom gameRoom = lobbyService.joinGameRoom(player, roomNumber);

    // Announce the player's name and id to everybody already in the gameroom
    for (Player p : gameRoom.getPlayerList()) {
      messagingTemplate.convertAndSend("/game/" + roomNumber, new AnnounceNamePacket(p.getId(), p.getName(), PacketType.ANNOUNCE_NAME));
    }

    if (gameRoom.getPlayerList().size() >= 2) {
      initializeGame(gameRoom);
    }

    return outMessage;
  }

  public void initializeGame(GameRoom gameRoom) {
    GameState gs = gameRoom.getGameState();
    Player p1 = gameRoom.getPlayerList().get(0);
    Player p2 = gameRoom.getPlayerList().get(1);
    gs.setPlayerOneId(p1.getId());
    gs.setPlayerTwoId(p2.getId());
    gs.applicationState = ApplicationState.SHIP_PLACEMENT;

    int roomNumber = gameRoom.getRoomNumber();
    GamePacket packet = new GamePacket(PacketType.GAME_START);

    messagingTemplate.convertAndSend("/game/" + roomNumber, packet);

  }


  // @MessageMapping("/wsReady")
  // public GamePacket wsConnectionReady(@Payload GamePacket packet) {
  //   // Mark player's connection as establish in the player object
  //   System.out.println("BEFORE Game Controller packet response sent!");
  //   GamePacket gp = new GamePacket();
  //   gp.type = PacketType.GAME_START;
  //   messagingTemplate.convertAndSend("/game/" + packet.roomNumber, gp);
  //   System.out.println("Game Controller packet response sent!");

  //   return gp;
  // }  

}
