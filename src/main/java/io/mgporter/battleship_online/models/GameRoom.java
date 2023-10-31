package io.mgporter.battleship_online.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.context.annotation.SessionScope;

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

  public static GameRoom fromNumber(int number) {
    GameRoom gameRoom = new GameRoom();
    gameRoom.setRoomNumber(number);
    gameRoom.setPlayerList(new ArrayList<>());
    GameState gameState = new GameState();
    gameRoom.setGameState(gameState);
    return gameRoom;
  }

  public void addPlayerToGame(Player player) {

    this.playerList.add(player);
    if (this.playerList.size() <= 2) this.gameState.updatePlayers(this.playerList);
  }

  public void removePlayerFromGame(Player player) {

    // int playerIndex = this.playerList.indexOf(player);
    System.out.println("This player will be removed from game: " + player);
    int playerIndex = -1;
    for (int i = 0; i < this.playerList.size(); i++) {

      if (player.getId().equals(this.playerList.get(i).getId())) {
        playerIndex = i;
        break;
      }
    }

    if (playerIndex == -1) throw new Error("This player is not in the game");
    // if (playerIndex == -1) return;

    this.playerList.remove(playerIndex);
    if (playerIndex <= 1) this.gameState.updatePlayers(this.playerList);

  }

  public String getPlayerOneId() {
    return this.gameState.getPlayerOneId();
  }

  public String getPlayerTwoId() {
    return this.gameState.getPlayerTwoId();
  }

}
