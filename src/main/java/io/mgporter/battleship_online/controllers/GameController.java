package io.mgporter.battleship_online.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mgporter.battleship_online.services.GameRoomService;

@RestController
@RequestMapping("/game")
@CrossOrigin("*")
public class GameController {
  
  private final SimpMessagingTemplate messagingTemplate;

  public GameController(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
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
