package io.mgporter.battleship_online.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "GameRoom")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoom {
  
  @Id
  private ObjectId id;
  private int roomNumber;

  // We will save the players directly to this object, and not save the players to the database
  private List<Player> playerList;

  private GameState gameState;

  // static factory method
  // public static GameRoom createNew(int roomNumber, Player player) {
  //   List<Player> playerList = new ArrayList<>();
  //   playerList.add(player);
  //   GameRoom gameRoom = new GameRoom();
  //   gameRoom.setRoomNumber(roomNumber);
  //   gameRoom.setPlayerList(playerList);
  //   GameState gameState = new GameState();
  //   gameRoom.setGameState(gameState);
  //   return gameRoom;
  // }

  public static GameRoom fromNumber(int number) {
    GameRoom gameRoom = new GameRoom();
    gameRoom.setRoomNumber(number);
    gameRoom.setPlayerList(new ArrayList<>());
    GameState gameState = new GameState();
    gameRoom.setGameState(gameState);
    return gameRoom;
  }

}
