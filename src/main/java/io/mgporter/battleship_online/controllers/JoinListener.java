package io.mgporter.battleship_online.controllers;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import io.mgporter.battleship_online.config.StompPrincipal;
import io.mgporter.battleship_online.enums.MessageType;
import io.mgporter.battleship_online.models.CredentialMessage;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.services.LobbyService;


/**
 * This Controller manages logic related to users joining and leaving. It accepts
 * websocket messages and listens for events, and then sends them to the
 * appropriate controller.
  */

@RestController
@CrossOrigin(origins = "*")
public class JoinListener {
  
  private final LobbyService lobbyService;
  private final SimpMessagingTemplate messagingTemplate;
  private final GameController gameController;
  private final LobbyController lobbyController;

  public JoinListener(
    LobbyService lobbyService, 
    SimpMessagingTemplate messagingTemplate,
    GameController gameController,
    LobbyController lobbyController) {
    this.lobbyService = lobbyService;
    this.messagingTemplate = messagingTemplate;
    this.gameController = gameController;
    this.lobbyController = lobbyController;
  }


  @MessageMapping("/getCredentials")
  public void handleGetCredentials(@Payload String username, StompPrincipal principal) {

    principal.setPlayerName(username);

    CredentialMessage credentialsMessage = 
      new CredentialMessage(principal.getPlayerName(), principal.getPlayerId(), MessageType.CREDENTIALS);
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/lobby", credentialsMessage);

    lobbyController.addUserToLobby(principal);
  }

  
  /**
   * Passes a joinLobby message on to the LobbyController. If the player is joining
   * the lobby from a game, then it first handles the leaveGame event.
   * @param username
   * @param principal
    */

  @MessageMapping("/joinLobby")
  public void handleJoinLobby(@Payload StompPrincipal principal) {
    
    /* If the player is joining the lobby after coming from a 
    gameroom, we need to remove them from the gameroom first */
    if (principal.isInRoom()) {
      int roomNumber = principal.removeRoomNumber();
      handlePlayerLeavingGameroom(principal, roomNumber);
    }

    lobbyController.addUserToLobby(principal);
  }


  private void sendRejectedJoinMessage(StompPrincipal principal, MessageType type) {
    Message rejectionMessage = Message.fromPrincipalAndType(principal, type);
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/player", rejectionMessage);
  }

  /**
   * Checks for various conditions before allowing a player to join a room.
   * Currently, it checks that a player is not already in a room, that the room is
   * successfully retrieved from the database, and that there are not more than two players.
   * 
   * In regards to this last point, in later versions, it is my intention to allow observers in
   * the room that can watch, however this is not complete yet.
   * 
   * @param message
   * @param principal
    */

  @MessageMapping("/joinGame")
  public void joinGameAccept(@Payload Message message, StompPrincipal principal) {

    /* Check if the player has already joined a room */
    if (principal.isInRoom()) {
      sendRejectedJoinMessage(principal, MessageType.REJECTEDJOIN_ALREADY_IN_GAME);
      return;
    }

    Integer numberOfPlayersInRoom = lobbyService.getNumberOfPlayersInRoom(message.getRoomNumber());

    /* Check if the game was successfully retrieved. */
    if (numberOfPlayersInRoom == null) {
      sendRejectedJoinMessage(principal, MessageType.REJECTEDJOIN_GAME_NOT_FOUND);
      return;
    }

    /* Check if the game already has two players. */
    if (numberOfPlayersInRoom > 1) {
      sendRejectedJoinMessage(principal, MessageType.REJECTEDJOIN_ROOM_FULL);
      return;
    }

    /* Finally, add the room number to the Principal object, and send a message
     * to them to allow the game room join event.
     */
    int roomNumber = message.getRoomNumber();
    principal.setRoomNumber(roomNumber);

    Message acceptedMessage = Message.fromPrincipalAndType(principal, MessageType.ACCEPTEDJOIN);
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/lobby", acceptedMessage);
  }

  /**
   * Once the client has loaded the GameRoom and subscribed to the "/game/{number}"
   * topic, then they will send a packet to gameloaded. From there, we know that the player
   * will receive anything sent to the game room, so we can handle the rest of the
   * join event. 
   * 
   * If we send out information to the game room too early (such as when the player
   * sends the /joingame message), then the player might miss it.
   * @param principal
    */

  @MessageMapping("/gameloaded")
  public void handleClientGameLoaded(StompPrincipal principal) {

    if (!principal.isInRoom()) return; 

    gameController.handleJoinGame(principal);

    Message outMessage = Message.fromPrincipalAndType(principal, MessageType.JOINGAME);
    messagingTemplate.convertAndSend("/lobby", outMessage);
  }

  /**
   * When a player connects to the WebSockets session for the first time, we automatically
   * send them a credentials packet with a UUID and default name generated from the server.
   * Once the client receives this, they will finish the websocket connection. If the client has
   * a previous name they are using, they will send a separate packet to change it on the
   * server.
   * 
   * TO IMPLEMENT: in the future, we would like to do this in the HTTP or WebSocket handshake stage,
   * and retrieve previous credentials using cookies.
   * 
   * @param event
    */

  @EventListener
  public void playerConnected(SessionSubscribeEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    StompPrincipal principal = (StompPrincipal) headerAccessor.getUser();
    String destination = headerAccessor.getDestination();

    System.out.println(destination);

    if (destination.equals("/user/queue/credentials")) {
      System.out.println("Sending credentials message\n");

      CredentialMessage credentialsMessage = 
        new CredentialMessage(principal.getPlayerName(), principal.getPlayerId(), MessageType.CREDENTIALS);
      messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/credentials", credentialsMessage);
    }
  }

  /**
   * Detect whether the player disconnected from a game room, or just from the lobby.
   * Then handle the disconnection accordingly.
   * 
   * @param event
    */

  @EventListener
  public void playerDisconnected(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    StompPrincipal principal = (StompPrincipal) headerAccessor.getUser();

    if (principal.isInRoom()) {
      int roomNumber = principal.removeRoomNumber();
      handlePlayerLeavingGameroom(principal, roomNumber);
    } else {
      lobbyController.handlePlayerLeavingLobby(principal);
    }
  }

  /**
   * Call the .handleLeaveGame method in the gameController and inform the lobby
   * that a player left and that a game was removed. This method also removes
   * the room number from the principal.
   * 
   * @param principal
    */

  private void handlePlayerLeavingGameroom(StompPrincipal principal, int roomNumber) {
    boolean wasGameRemoved = gameController.handleLeaveGame(principal, roomNumber);
    Message disconnectMessage = Message.fromPrincipalAndType(principal, MessageType.EXITEDGAME);
    disconnectMessage.setRoomNumber(roomNumber);
    messagingTemplate.convertAndSend("/lobby", disconnectMessage);

    if (wasGameRemoved) {
      Message gameRemovedMessage = new Message();
      gameRemovedMessage.setSender(Player.fromPrincipal(principal));
      gameRemovedMessage.setRoomNumber(roomNumber);
      gameRemovedMessage.setType(MessageType.GAMEREMOVED);
      messagingTemplate.convertAndSend("/lobby", gameRemovedMessage);
    }
  }

}
