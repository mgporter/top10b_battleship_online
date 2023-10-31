package io.mgporter.battleship_online;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.mgporter.battleship_online.models.GameRoom;
import io.mgporter.battleship_online.repositories.GameRoomRepository;
import io.mgporter.battleship_online.services.LobbyService;

@SpringBootTest
class BattleshipOnlineApplicationTests {

	// @Test
	// @DisplayName("Test that game rooms can be retrieved by room number")
	// void serviceRetrievesGameroom() {
	// 	GameRoomRepository gameRoomRepository = mock(GameRoomRepository.class);
	// 	MongoTemplate mongoTemplate = mock(MongoTemplate.class);

	// 	LobbyService lobbyService = new LobbyService(gameRoomRepository, mongoTemplate);

	// 	when(mongoTemplate.findOne(Query.query(Criteria.where("roomNumber").is(1234)), GameRoom.class))
	// 		.thenReturn(new GameRoom());
		
	// 	lobbyService.getRoomById(1234);


	// }

}
