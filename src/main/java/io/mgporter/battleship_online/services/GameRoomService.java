package io.mgporter.battleship_online.services;

import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.repositories.GameRoomRepository;

@Service
public class GameRoomService {
  
  private final GameRoomRepository gameRoomRepository;
  private final MongoTemplate mongoTemplate;

  // Load the GameRoomRepository by dependency injection
  public GameRoomService(GameRoomRepository gameRoomRepository, MongoTemplate mongoTemplate) {
    this.gameRoomRepository = gameRoomRepository;
    this.mongoTemplate = mongoTemplate;
  }

  public List<GameRoom> getAllRooms() {
    return gameRoomRepository.findAll();
  }

  public Set<Integer> getAllRoomNumbers() {
    return gameRoomRepository.findAll().stream().map(x -> x.getRoomNumber()).collect(Collectors.toSet());
  }

  public GameRoom createGameRoom(Player player) {

    // Get a set of all room numbers
    Set<Integer> roomNumbers = getAllRoomNumbers();

    // Keep generating a random room number until one is found that isn't already used
    int randomRoomNumber;
    do {
      randomRoomNumber = (int) (Math.random() * 9000) + 1000;
    } while (roomNumbers.contains(randomRoomNumber));

    GameRoom gameRoom = gameRoomRepository.insert(GameRoom.createNew(randomRoomNumber, player));

    return gameRoom;
  }

}
