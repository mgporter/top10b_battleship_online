package io.mgporter.battleship_online.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import io.mgporter.battleship_online.config.StompPrincipal;
import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.GameState;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.repositories.GameRoomRepository;

/**
 * This service interfaces with the database to create, store, and retreive
 * game rooms.
  */

@Service
public class LobbyService {
  
  private final GameRoomRepository gameRoomRepository;
  private final MongoTemplate mongoTemplate;

  public LobbyService(
    GameRoomRepository gameRoomRepository, 
    MongoTemplate mongoTemplate) {
    this.gameRoomRepository = gameRoomRepository;
    this.mongoTemplate = mongoTemplate;
  }

  /**
   * Returns ONLY the roomNumber and playerList fields of each gameroom
   * for display in the lobby.
   * 
   * @return
    */
  public List<GameRoom> getAllRooms() {
    Query query = new Query();
    query.fields().include("roomNumber", "playerList").exclude("id");
    return mongoTemplate.find(query, GameRoom.class);
  }

  /**
   * Returns a set of numbers which have already been used for gamerooms.
   * 
   * @return
    */

  public Set<Integer> getAllRoomNumbers() {
    Query query = new Query();
    query.fields().include("roomNumber").exclude("id");
    return mongoTemplate
      .find(query, GameRoom.class)
      .stream()
      .map(x -> x.getRoomNumber())
      .collect(Collectors.toSet());
  }

  public boolean doesExist(int roomNumber) {
    return mongoTemplate.exists(
      Query.query(Criteria.where("roomNumber").is(roomNumber)), 
      GameRoom.class);
  }

  public Optional<GameRoom> getRoomById(int number) {
    return Optional.ofNullable(
      mongoTemplate.findOne(
        Query.query(Criteria.where("roomNumber").is(number)), 
      GameRoom.class));
  }

  public void saveGameRoom(GameRoom gameRoom) {
    mongoTemplate.save(gameRoom, "GameRoom");
  }

  /**
   * Create a new game room and assign it a unique number by creating a 
   * random four-digit number, and then checking to make sure no other
   * game rooms use that number.
   * 
   * @return
    */

  public GameRoom createGameRoom(Set<Integer> roomNumbers) {

    // Keep generating a random room number until one is found that isn't already used
    int randomRoomNumber;
    do {
      randomRoomNumber = (int) (Math.random() * 9000) + 1000;
    } while (roomNumbers.contains(randomRoomNumber));

    GameRoom gameRoom = gameRoomRepository.insert(GameRoom.fromNumber(randomRoomNumber));
    
    return gameRoom;
  }

  /**
   * This method is used to check whether a player can join a room, so we intentionally
   * return null if the room is not found. That way, we can be sure to not let the player
   * join a room that doesn't exist.
   *
   * @param roomNumber
   * @return the number of players in the room, or null if the room is not found.
    */

  public Integer getNumberOfPlayersInRoom(int roomNumber) {

    Query query = new Query();
    query.addCriteria(
        Criteria.where("roomNumber").is(roomNumber)
      ).fields().include("playerList").exclude("id");

    GameRoom gameRoom = mongoTemplate.findOne(query, GameRoom.class);

    if (gameRoom == null) return null;
    else return gameRoom.getPlayerList().size();
  }

  public Optional<GameRoom> joinGameRoom(StompPrincipal principal) {

    Optional<GameRoom> gameRoom = getRoomById(principal.getRoomNumber());
    gameRoom.ifPresent(g -> joinGameRoom(principal, g));

    return gameRoom;
  }

  public void joinGameRoom(StompPrincipal principal, GameRoom gameRoom) {

    gameRoom.addPlayerToGame(Player.fromPrincipal(principal));
    mongoTemplate.save(gameRoom, "GameRoom");
  }

  public Optional<GameRoom> leaveGameRoom(StompPrincipal principal, int roomNumber) {
    Optional<GameRoom> gameRoom = getRoomById(roomNumber);
    System.out.println("GAME ROOM RETREIVED: " + gameRoom);
    gameRoom.ifPresent((g) -> {
      g.removePlayerFromGame(Player.fromPrincipal(principal));
      mongoTemplate.save(g, "GameRoom");
    });
      
    return gameRoom;
  }

  // public Optional<GameRoom> leaveGameRoom(StompPrincipal principal) {
  //   return leaveGameRoom(principal, principal.getRoomNumber());
  // }

  public void deleteGameRoom(int roomNumber) {
    mongoTemplate.findAndRemove(
      Query.query(
        Criteria.where("roomNumber").is(roomNumber)
      ), GameRoom.class);
  }

  public void updateGameState(GameState gameState, int roomNumber) {

    mongoTemplate.update(GameRoom.class)
      .matching(Criteria.where("roomNumber").is(roomNumber))
      .apply(new Update().set("gameState", gameState))
      .first();

  }

}
