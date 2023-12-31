package io.mgporter.battleship_online.controllers;

import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import io.mgporter.battleship_online.config.StompPrincipal;
import io.mgporter.battleship_online.enums.MessageType;
import io.mgporter.battleship_online.enums.PacketType;
import io.mgporter.battleship_online.models.Constants;
import io.mgporter.battleship_online.models.Coordinate;
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.Ship;
import io.mgporter.battleship_online.packets.AttackPacket;
import io.mgporter.battleship_online.packets.GamePacket;
import io.mgporter.battleship_online.packets.GameStartPacket;
import io.mgporter.battleship_online.packets.LoadGamePacket;
import io.mgporter.battleship_online.packets.PlacementPacket;
import io.mgporter.battleship_online.packets.PlayerListPacket;
import io.mgporter.battleship_online.services.GameService;
import io.mgporter.battleship_online.services.LobbyService;

/**
* Controller that accepts game-related packets and messages, i.e., anything
* that occurs when the user is playing the actual game.
*/


@RestController
@CrossOrigin("*")
public class GameController {
  
  private final SimpMessagingTemplate messagingTemplate;
  private final LobbyService lobbyService;
  private final GameService gameService;

  public GameController(
    SimpMessagingTemplate messagingTemplate, 
    LobbyService lobbyService,
    GameService gameService) {
    this.messagingTemplate = messagingTemplate;
    this.lobbyService = lobbyService;
    this.gameService = gameService;
  }

  private void sendErrorMessage(StompPrincipal principal, MessageType type) {
    Message errorMessage = Message.fromPrincipalAndType(principal, type);
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/game", errorMessage);
  }


  /**
  * Sends out a notification to the players that a ship has
  * been placed.
  *
  * @param packet
  * @param principal
  */

  @MessageMapping("/game/placeShip")
  public void playerPlacedShip(@Payload PlacementPacket packet, StompPrincipal principal) {
    packet.playerId = principal.getPlayerId();
    messagingTemplate.convertAndSend("/game/public/" + principal.getRoomNumber(), packet);
  }


  /** 
   * When a player finishes ship placement by pressing the start button, a packet is send to this
   * routing. Once both players have finished placing their ships, the server informs the
   * clients with a PLACED_COMPLETE packet. The clients then send another packet to invoke
   * the loadGameData method, which loads up the data from the other player.
   * 
   * @param packet
   * @param principal
  */

  @MessageMapping("/game/placementComplete")
  public void addPlayerShips(@Payload PlacementPacket packet, StompPrincipal principal) {
    
    Optional<GameRoom> gameRoomOptional = lobbyService.getRoomById(principal.getRoomNumber());
    
    if (!gameRoomOptional.isPresent()) {
      sendErrorMessage(principal, MessageType.REJECTEDJOIN_GAME_NOT_FOUND);
      return;
    }

    GameRoom gameRoom = gameRoomOptional.get();

    GameState gameState = gameRoom.getGameState();
    gameService.setGameState(gameState);
    gameService.saveShipsInGameState(principal.getPlayerId(), packet.placementList);
    lobbyService.saveGameRoom(gameRoom);

    if (gameService.allPlacementsComplete()) {
      sendPlacedCompletedPacket(principal.getRoomNumber());
    } else {

      /* Inform the opponent that a player has finished placing
       * their ships.
       */

      boolean playerOneHasPlaced = gameState.playerOnesPlacementsComplete();
      boolean playerTwoHasPlaced = gameState.playerTwosPlacementsComplete();

      GameStartPacket gameStartPacket = new GameStartPacket(playerOneHasPlaced, playerTwoHasPlaced);
      messagingTemplate.convertAndSend("/game/public/" + principal.getRoomNumber(), gameStartPacket);  
    }


  }

  private void sendPlacedCompletedPacket(int roomNumber) {
    GamePacket placedCompletePacket = new GamePacket(PacketType.PLACED_COMPLETE);
    messagingTemplate.convertAndSend("/game/public/" + roomNumber, placedCompletePacket);
  }

  /**
   * After both players complete their ship placements, they send a packet to the server to
   * instruct it to load the GameState from the database into their own GameState. This way,
   * both players have access to their opponent's ship placements (though they are unable to
   * access this client-side).
   * 
   * @param principal
    */

  @MessageMapping("/game/loadGameData")
  public void loadGameData(StompPrincipal principal) {

    Optional<GameRoom> gameRoomOptional = lobbyService.getRoomById(principal.getRoomNumber());
    
    if (!gameRoomOptional.isPresent()) {
      sendErrorMessage(principal, MessageType.REJECTEDJOIN_GAME_NOT_FOUND);
      return;
    }

    GameRoom gameRoom = gameRoomOptional.get();

    gameService.loadDataToBoard(gameRoom.getGameState());

    GamePacket startAttackPhasePacket = new GamePacket();
    startAttackPhasePacket.type = PacketType.GAME_ATTACK_PHASE_START;
    messagingTemplate.convertAndSend("/game/public/" + principal.getRoomNumber(), startAttackPhasePacket);
  }


  /**
   * Load the data from the gameRoom (fetched from the DB) to the player's gameService.
   * Then create a packet with the required information to recreate the game.
   * The opponent's ships are NOT sent (except for the already sunk ones that
   * the player would have already known about), in order to prevent cheating.
   * 
   * @param principal
   * @param gameRoom
    */

  public void loadInProgressGameData(StompPrincipal principal, GameRoom gameRoom) {

    gameService.loadDataToBoard(gameRoom.getGameState());

    LoadGamePacket loadGamePacket = new LoadGamePacket();

    boolean playerOneGoesFirst = 
      gameService.getPlayerOnesAttackResults().size() == gameService.getPlayerTwosAttackResults().size();

    if (gameService.getPlayerOneId().equals(principal.getPlayerId())) {
      loadGamePacket.setMyShips(gameService.getPlayerOnesShips());
      loadGamePacket.setOpponentSunkShips(gameService.getPlayerTwosSunkShips());
      loadGamePacket.setMyAttacks(gameService.getPlayerOnesAttackResults());
      loadGamePacket.setOpponentAttacks(gameService.getPlayerTwosAttackResults());
      loadGamePacket.setGoFirst(playerOneGoesFirst);
      loadGamePacket.setOpponentHasPlaced(gameService.getPlayerTwosShips().size() == Constants.MAXSHIPS);

    } else {
      loadGamePacket.setMyShips(gameService.getPlayerTwosShips());
      loadGamePacket.setOpponentSunkShips(gameService.getPlayerOnesSunkShips());
      loadGamePacket.setMyAttacks(gameService.getPlayerTwosAttackResults());
      loadGamePacket.setOpponentAttacks(gameService.getPlayerOnesAttackResults());
      loadGamePacket.setGoFirst(!playerOneGoesFirst);
      loadGamePacket.setOpponentHasPlaced(gameService.getPlayerOnesShips().size() == Constants.MAXSHIPS);
      
    }
    
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/game", loadGamePacket);

  }




  /**
   * All attacks are directed here. The server sends the same attack packet back to all of
   * the clients in the gameroom, but also adds information about the result, namely,
   * ATTACK_MISSED, ATTACK_HITSHIP, and ATTACK_SUNKSHIP.
   * 
   * @param packet
   * @param principal
    */

  @MessageMapping("/game/attack")
  public void handleAttack(@Payload AttackPacket packet, StompPrincipal principal) {
    Optional<Ship> ship = gameService.attackBy(principal.getPlayerId(), new Coordinate(packet.row, packet.col));
    ship.ifPresentOrElse((s) -> handleHit(principal, packet, ship.get()), () -> handleMiss(principal, packet));
  }

  private void handleHit(StompPrincipal principal, AttackPacket packet, Ship ship) {
    packet.shipType = ship.getType();
    packet.playerId = principal.getPlayerId();
    
    if (!ship.isSunk()) {
      packet.result = PacketType.ATTACK_HITSHIP;
      messagingTemplate.convertAndSend("/game/public/" + principal.getRoomNumber(), packet);
      gameService.addAttackResult(principal.getPlayerId(), packet.row, packet.col, PacketType.H);

    } else {
      packet.result = PacketType.ATTACK_SUNKSHIP;
      gameService.addAttackResult(principal.getPlayerId(), packet.row, packet.col, PacketType.S);

      // Add information about the sunk ship, 
      // so that the client can display it on the opponent board
      Coordinate startingCoordinate = ship.getStartingCoordinate();
      packet.startingRow = startingCoordinate.getRow();
      packet.startingCol = startingCoordinate.getCol();
      packet.direction = ship.getDirection();

      messagingTemplate.convertAndSend("/game/public/" + principal.getRoomNumber(), packet);

      if (gameService.opponentAllSunk(packet.playerId)) {
        sendAllSunkPacket(principal);
      }
    }   
  }

  private void sendAllSunkPacket(StompPrincipal principal) {

    /* The playerId on this packet describes the player who sunk all
    of their opponent's ships, NOT the person whose ships were all sunk. */

    AttackPacket allSunkPacket = new AttackPacket();
    allSunkPacket.playerId = principal.getPlayerId();   
    allSunkPacket.type = PacketType.ATTACK_ALLSUNK;
    messagingTemplate.convertAndSend("/game/public/" + principal.getRoomNumber(), allSunkPacket);
  }

  private void handleMiss(StompPrincipal principal, AttackPacket packet) {
    packet.result = PacketType.ATTACK_MISSED;
    packet.playerId = principal.getPlayerId();
    messagingTemplate.convertAndSend("/game/public/" + principal.getRoomNumber(), packet);

    gameService.addAttackResult(principal.getPlayerId(), packet.row, packet.col, PacketType.M);
  }

  /**
   * Removes the player from the game and sends out an updated PlayerList packet to
   * everybody in the room. If there is no one left in the room, just delete the
   * game room entirely.
   * 
   * @param principal
   * @return {@code true} if the room was deleted as a result of the player leaving.
    */

  public boolean handleLeaveGame(StompPrincipal principal, int roomNumber) {

    // Remove the player from the game room, and get a reference to the gameroom back
    Optional<GameRoom> gameRoomOptional = lobbyService.leaveGameRoom(principal, roomNumber);

    if (!gameRoomOptional.isPresent()) {
      return false;
    }

    GameRoom gameRoom = gameRoomOptional.get();

    if (gameRoom.getPlayerList().size() == 0) {
      lobbyService.deleteGameRoom(gameRoom.getRoomNumber());
      return true;
    } else {
      // Update the gameState (which includes any attacks that have occurred since the last save)
      saveGameState(principal, gameRoom);
      broadcastUpdatedPlayerList(gameRoom);
      return false;
    }
  }

  @MessageMapping("/game/saveState")
  public void saveGameState(StompPrincipal principal) {
    Optional<GameRoom> gameRoomOptional = lobbyService.getRoomById(principal.getRoomNumber());
    if (!gameRoomOptional.isPresent()) {
      return;
    }
    GameRoom gameRoom = gameRoomOptional.get();

    saveGameState(principal, gameRoom);
  }

  public void saveGameState(StompPrincipal principal, GameRoom gameRoom) {

    gameService.update(gameRoom, principal.getPlayerId());
    lobbyService.saveGameRoom(gameRoom);
  }

  /**
   * Adds a player to the room and saves the GameState to this player's GameService.
   * Sends out an updated PlayerList to everybody in the room and, if there are
   * two players, sends a GAME_START packet to tell each client to begin
   * ship placement.
   * 
   * @param principal
    */

  public void handleJoinGame(StompPrincipal principal) {

    Optional<GameRoom> gameRoomOptional = lobbyService.joinGameRoom(principal);

    if (gameRoomOptional.isEmpty()) return;
    GameRoom gameRoom = gameRoomOptional.get();

    GameState gameState = gameRoom.getGameState();

    gameService.resetGameStateWith(gameState);

    broadcastUpdatedPlayerList(gameRoom);

    // There are not at least two players in the room, stop here
    if (!gameState.bothPlayersReady()) return;
    
    // If the incoming player has not placed ships yet, send them the Game Start packet.
    // If they have placed ships (i.e., they are taking over for a player that left after
    // placing ships), then load the game data.

    boolean playerOneHasPlaced = gameState.playerOnesPlacementsComplete();
    boolean playerTwoHasPlaced = gameState.playerTwosPlacementsComplete();
    boolean myPlacementsComplete = gameState.isPlayerOne(principal.getPlayerId()) ?
      playerOneHasPlaced : playerTwoHasPlaced;

    if (myPlacementsComplete) {
      loadInProgressGameData(principal, gameRoom);
    } 

    GameStartPacket packet = new GameStartPacket(playerOneHasPlaced, playerTwoHasPlaced);
    messagingTemplate.convertAndSend("/game/public/" + principal.getRoomNumber(), packet);   
  }

  /**
   * Send an updated PlayerList to everybody in the game room to inform them who else is in the
   * room, and who is playerOne and playerTwo.
   * 
   * @param gameRoom
    */

  private void broadcastUpdatedPlayerList(GameRoom gameRoom) {
    PlayerListPacket packet = new PlayerListPacket(
      gameRoom.getPlayerList(),
      gameRoom.getGameState().getPlayerOneId(),
      gameRoom.getGameState().getPlayerTwoId(),
      PacketType.PLAYERLIST_UPDATE
    );
    messagingTemplate.convertAndSend("/game/public/" + gameRoom.getRoomNumber(), packet);
  }

}
