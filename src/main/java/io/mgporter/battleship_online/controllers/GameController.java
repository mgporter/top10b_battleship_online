package io.mgporter.battleship_online.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mgporter.battleship_online.config.StompPrincipal;
import io.mgporter.battleship_online.models.ApplicationState;
import io.mgporter.battleship_online.models.Coordinate;
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Gameboard;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.models.Ship;
import io.mgporter.battleship_online.packets.AttackPacket;
import io.mgporter.battleship_online.packets.GamePacket;
import io.mgporter.battleship_online.packets.PacketType;
import io.mgporter.battleship_online.packets.PlacementPacket;
import io.mgporter.battleship_online.services.GameService;
import io.mgporter.battleship_online.services.LobbyService;

@Controller
@RestController
@RequestMapping("/game")
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

  @MessageMapping("/game/placeShip")
  public void playerPlacedShip(@Payload PlacementPacket packet) {

    /* Currently, the clients just count the number of placed_ship packets
     * to get the number placed by the opponent. Players cannot remove ships,
     * so this works for now.
     */

    messagingTemplate.convertAndSend("/game/" + packet.roomNumber, packet);
  }

  @MessageMapping("/game/placementComplete")
  public void addPlayerShips(@Payload PlacementPacket packet) {
    
    GameRoom gameRoom = lobbyService.getRoomById(packet.roomNumber);

    GameState gameState = gameRoom.getGameState();

    gameService.setGameState(gameState);
    gameService.addShipsToBoard(packet.playerId, packet.placementList);
    lobbyService.saveGameRoom(gameRoom);

    /* If all placements are complete, the server sends a message to the clients in the
     * gameroom. The clients then send a load data packet to invoke the loadGameData method, and
     * the server loads the data into their gameboard, then sends a packet to begin the attack phase.
     */

    if (gameService.allPlacementsComplete()) {
      GamePacket placedCompletePacket = new GamePacket();
      placedCompletePacket.type = PacketType.PLACED_COMPLETE;
      messagingTemplate.convertAndSend("/game/" + packet.roomNumber, placedCompletePacket);
    }
  }

  @MessageMapping("/game/loadGameData")
  public void loadGameData(@Payload PlacementPacket packet, StompPrincipal principal) {
    GameRoom gameRoom = lobbyService.getRoomById(packet.roomNumber);
    gameService.loadDataToBoard(gameRoom.getGameState());

    GamePacket startAttackPhasePacket = new GamePacket();
    startAttackPhasePacket.type = PacketType.GAME_ATTACK_PHASE_START;
    messagingTemplate.convertAndSend("/game/" + packet.roomNumber, startAttackPhasePacket);

    if (principal.getPlayerId().equals(gameRoom.getPlayerOneId())) {
      messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/message", new GamePacket(PacketType.ATTACK));
    }
  }

  @MessageMapping("/game/attack")
  public void handleAttack(@Payload AttackPacket packet) {
    System.out.println("Game service is: " + gameService);
    Optional<Ship> ship = gameService.attack(packet.playerId, new Coordinate(packet.row, packet.col));
    ship.ifPresentOrElse((s) -> handleHit(packet, ship.get()), () -> handleMiss(packet));
  }

  public void handleHit(AttackPacket packet, Ship ship) {
    packet.shipType = ship.getType();
    if (!ship.isSunk()) {
      packet.result = PacketType.ATTACK_HITSHIP;
    } else {
        packet.result = PacketType.ATTACK_SUNKSHIP;
      // if (!board.allSunk()) {
      //   packet.result = PacketType.ATTACK_SUNKSHIP;
      // } else {
      //   packet.result = PacketType.ATTACK_ALLSUNK;
      // }
      
    }
    messagingTemplate.convertAndSend("/game/" + packet.roomNumber, packet);
  }

  public void handleMiss(AttackPacket packet) {
    packet.result = PacketType.ATTACK_MISSED;
    messagingTemplate.convertAndSend("/game/" + packet.roomNumber, packet);
  }


}
