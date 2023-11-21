package io.mgporter.battleship_online.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.mgporter.battleship_online.config.StompPrincipal;
import io.mgporter.battleship_online.enums.MessageType;
import io.mgporter.battleship_online.models.Constants;
import io.mgporter.battleship_online.models.CredentialMessage;
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.packets.GameRoomList;
import io.mgporter.battleship_online.services.LobbyService;


/**
 * This controller handles the functions related to the lobby.
  */


@RestController
@CrossOrigin(origins = "*")
public class LobbyController {
  
  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;

  public LobbyController(
    LobbyService lobbyService,
    SimpMessagingTemplate messagingTemplate) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
  }

  @GetMapping("/helloMessageTest")
  public String sendTestMessage() {
    return "MGPorter's web server is working! Updated origins";
  }

  /**
   * Return a list of all the gamerooms currently active. The GameState associated with
   * the gameroom is not returned in order to save bandwidth.
   * 
   * @return
    */

  @GetMapping("/getGameRooms")
  public ResponseEntity<List<GameRoom>> getAllGameRooms() {
    return new ResponseEntity<List<GameRoom>>(lobbyService.getAllRooms(), HttpStatus.OK);
  }

  /**
   * Creates a new GameRoom, which will have a unique 4-digit id. Also checks to make sure 
   * that there are not already too many game rooms.
   * 
   * @param payload
   * @return
    */

  @PostMapping("/createGameRoom")
  public ResponseEntity<GameRoom> createGameRoom(@RequestBody Map<String, String> payload) {

    Player player = new Player(payload.get("id"), payload.get("playerName"));
    Set<Integer> gameRoomNumbers = lobbyService.getAllRoomNumbers();

    if (gameRoomNumbers.size() > Constants.MAXGAMEROOMS) {
      Message errorMessage = Message.fromSenderAndType(player, MessageType.MAXGAMEROOMSREACHED);
      messagingTemplate.convertAndSendToUser(player.getId(), "/queue/player", errorMessage);
      return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
    
    GameRoom newRoom = lobbyService.createGameRoom(gameRoomNumbers);
    Message message = Message.fromSenderTypeRoomnumber(player, MessageType.CREATEDGAME, newRoom.getRoomNumber());

    messagingTemplate.convertAndSend("/lobby", message);

    return new ResponseEntity<>(newRoom, HttpStatus.CREATED);
  }


  /**
   * Sends a message to the lobby when the player joins (that is, when the client
   * sends a /joinLobby packet). If the user has a name that they have used previously,
   * then it is sent in the payload, and we can set it to their name. In this way, the name is
   * set by the user unless they do not have one, while the playerId is set by the server. This
   * information is sent back to the client in the CREDENTIALS packet.
   * 
   * @param username
   * @param principal
    */

  public void addUserToLobby(StompPrincipal principal) {

    Player player = new Player(principal.getId(), principal.getPlayerName());
    Message joinLobbyMessage = Message.fromSenderAndType(player, MessageType.JOINLOBBY);
    messagingTemplate.convertAndSend("/lobby", joinLobbyMessage);

    GameRoomList gameRoomList = new GameRoomList(MessageType.GAMEROOMLIST, lobbyService.getAllRooms());

    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/lobby", gameRoomList);
  }

  
  @MessageMapping("/changeName")
  public void changePlayerName(@Payload String newName, StompPrincipal principal) {
    principal.setPlayerName(newName);
  }


  public void handlePlayerLeavingLobby(StompPrincipal principal) {
    Message disconnectMessage = Message.fromPrincipalAndType(principal, MessageType.EXITEDLOBBY);
    messagingTemplate.convertAndSend("/lobby", disconnectMessage);
  }

}
