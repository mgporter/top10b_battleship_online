package io.mgporter.battleship_online.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* The GameRoom is the only part of the game that is stored on the database.
 * Its most important component is the GameState, which stores the gameboard
 * information for both players.
 */

@Document(collection = "GameRoom")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoom {
  
  @Id
  private ObjectId id;
  private int roomNumber;
  private List<Player> playerList;
  private GameState gameState;

  public static GameRoom fromNumber(int number) {
    GameRoom gameRoom = new GameRoom();
    gameRoom.setRoomNumber(number);
    gameRoom.setPlayerList(new ArrayList<>());
    gameRoom.setGameState(new GameState());
    return gameRoom;
  }

  /**
   * Add the player to the PlayerList, and update playerOne and playerTwo
   * if there are two or fewer players. If there are more, no update is necessary
   * because the first two players will always be playerOne and playerTwo, though
   * not necessarily in that order.
   * 
   * @param player
    */

  public void addPlayerToGame(Player player) {

    if (this.playerList.contains(player)) return;
    this.playerList.add(player);
    if (this.playerList.size() <= 2) this.gameState.updatePlayers(this.playerList);
  }

  /**
   * Remove the player from the PlayerList, and update playerOne and playerTwo
   * if either one of them left.
   * 
   * @param player
    */

  public void removePlayerFromGame(Player player) {

    int playerIndex = this.playerList.indexOf(player);
    if (playerIndex == -1) return;

    this.playerList.remove(playerIndex);
    if (playerIndex <= 1) this.gameState.updatePlayers(this.playerList);

  }

}
