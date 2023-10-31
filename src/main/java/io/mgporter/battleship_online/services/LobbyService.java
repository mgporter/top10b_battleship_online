package io.mgporter.battleship_online.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.repositories.GameRoomRepository;

@Service
public class LobbyService {
  
  private final GameRoomRepository gameRoomRepository;
  private final MongoTemplate mongoTemplate;

  // Load the GameRoomRepository by dependency injection
  public LobbyService(GameRoomRepository gameRoomRepository, MongoTemplate mongoTemplate) {
    this.gameRoomRepository = gameRoomRepository;
    this.mongoTemplate = mongoTemplate;
  }

  public List<GameRoom> getAllRooms() {
    return gameRoomRepository.findAll();
  }

  public Set<Integer> getAllRoomNumbers() {
    return gameRoomRepository.findAll().stream().map(x -> x.getRoomNumber()).collect(Collectors.toSet());
  }

  public GameRoom getRoomById(int number) {
    return mongoTemplate.findOne(Query.query(Criteria.where("roomNumber").is(number)), GameRoom.class);
  }

  public GameRoom createGameRoom() {

    // Get a set of all room numbers
    Set<Integer> roomNumbers = getAllRoomNumbers();

    // Keep generating a random room number until one is found that isn't already used
    int randomRoomNumber;
    do {
      randomRoomNumber = (int) (Math.random() * 9000) + 1000;
    } while (roomNumbers.contains(randomRoomNumber));

    GameRoom gameRoom = gameRoomRepository.insert(GameRoom.fromNumber(randomRoomNumber));

    return gameRoom;
  }
  public GameRoom joinGameRoom(Player player, int roomNumber) {

    GameRoom gameRoom = getRoomById(roomNumber);
    System.out.println(roomNumber);
    System.out.println(gameRoom);
    gameRoom.addPlayerToGame(player);
    mongoTemplate.save(gameRoom, "GameRoom");

    return gameRoom;
  }

  public GameRoom leaveGameRoom(Player player, int roomNumber) {

    GameRoom gameRoom = getRoomById(roomNumber);
    gameRoom.removePlayerFromGame(player);
    mongoTemplate.save(gameRoom, "GameRoom");
      
    return gameRoom;
  }

  public void deleteGameRoom(int roomNumber) {
    mongoTemplate.findAndRemove(Query.query(Criteria.where("roomNumber").is(roomNumber)), GameRoom.class);
  }

}
