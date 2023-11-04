package io.mgporter.battleship_online.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import io.mgporter.battleship_online.services.LobbyService;

@Controller
@RestController
@RequestMapping("/game")
@CrossOrigin("*")
public class GameController {
  
  private final SimpMessagingTemplate messagingTemplate;
  private final LobbyService lobbyService;
  private GameRoom currentGameRoom; 
  private int currentRoomNumber;

  public GameController(SimpMessagingTemplate messagingTemplate, LobbyService lobbyService) {
    this.messagingTemplate = messagingTemplate;
    this.lobbyService = lobbyService;
  }

  @MessageMapping("/game/placeShip")
  public void playerPlacedShip(@Payload PlacementPacket packet) {
    messagingTemplate.convertAndSend("/game/" + packet.roomNumber, packet);
  }

  @MessageMapping("/game/placementComplete")
  public void addPlayerShips(@Payload PlacementPacket packet) {
    
    GameRoom gameRoom = lobbyService.getRoomById(packet.roomNumber);
    this.currentGameRoom = gameRoom;
    this.currentRoomNumber = gameRoom.getRoomNumber();

    GameState gameState = gameRoom.getGameState();
    Gameboard board = gameState.getBoardById(packet.playerId);

    for (Ship ship : packet.placementList) {
      board.placeShip(ship);
    }

    lobbyService.saveGameRoom(gameRoom);

    if (gameState.allPlacementsComplete()) {
      GamePacket placedCompletePacket = new GamePacket();
      placedCompletePacket.type = PacketType.GAME_ATTACK_PHASE_START;
      messagingTemplate.convertAndSend("/game/" + packet.roomNumber, placedCompletePacket);
    }
  }

  @MessageMapping("/game/attack")
  public void handleAttack(@Payload AttackPacket packet) {
    Gameboard board = this.currentGameRoom.getGameState().getMyOpponentsBoard(packet.playerId);
    Optional<Ship> ship = board.receiveAttack(new Coordinate(packet.row, packet.col));
    ship.ifPresentOrElse((s) -> handleHit(packet, ship.get()), () -> handleMiss(packet));
  }

  public void handleHit(AttackPacket packet, Ship ship) {
    packet.shipType = ship.getType();
    if (!ship.isSunk()) {
      packet.result = PacketType.ATTACK_HITSHIP;
    } else {
      packet.result = PacketType.ATTACK_SUNKSHIP;
    }
    messagingTemplate.convertAndSend("/game/" + packet.roomNumber, packet);
  }

  public void handleMiss(AttackPacket packet) {
    packet.result = PacketType.ATTACK_MISSED;
    messagingTemplate.convertAndSend("/game/" + packet.roomNumber, packet);
  }


}
