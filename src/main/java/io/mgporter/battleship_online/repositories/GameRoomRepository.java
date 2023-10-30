package io.mgporter.battleship_online.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.mgporter.battleship_online.models.GameRoom;

@Repository
public interface GameRoomRepository extends MongoRepository<GameRoom, ObjectId> {

}
