package io.mgporter.battleship_online.controllers;

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
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.packets.GamePacket;
import io.mgporter.battleship_online.packets.PacketType;

@Controller
@RestController
@RequestMapping("/game")
@CrossOrigin("*")
public class GameController {
  
  private final SimpMessagingTemplate messagingTemplate;

  public GameController(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }



  @MessageMapping("/initializeGame")
  public void initializeGame(@Payload GamePacket packet) {
    /* Create the gameboard object as a session-scoped object, add reference to gameboards for both players
     * 
     * When a person joins a room, they see a waiting screen and an initializegame packet is sent.
     * At this time, the gamestate object is created and player one's ID is assigned to p1 slot.
     * The player is also subscribed to the room topic.
     * When a second player joins, the second player's id is put into the gamestate's p2 slot.
     */

  }

  @MessageMapping("/getGameDetails")
  public void sendGameDetails() {
    // Send game number, players in room, observers in room, board state for both players, but NOT ship placement
  }

  @MessageMapping("/sendShipPlacementData")
  public void receiveShipPlacementData() {
    // When user finishes ship placement, send a packet with details of where all of the ships are placed to the server
    // The server adds them to the virtual board.
  }

  @MessageMapping("/sendGamePacket")
  public void sendGamePacket() {
    // When the player makes a move, check the server's virtual board for a result.
    // Send the results back to both players.
    
  }

}
