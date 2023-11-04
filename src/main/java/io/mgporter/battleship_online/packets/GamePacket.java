package io.mgporter.battleship_online.packets;

import io.mgporter.battleship_online.models.ShipType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// Used for sending attack coordinates, getting information on attack result (missed, name of ship that was hit, was it sunk?)
  // coordinates for placing a ship, gamestate info (finished placing ships?)

  /* Types of gamepackets
   * Send attack coordinates: need coordiantes
   * Send attack results: missed, hitship, sunkship, name of ship
   * Send ship placement: list of coordinates, ship type
   * Send gamestate info: which player? how many ships placed, ship placement complete (after user presses start button),
   *          start game (after both users have pressed start button), all ships sunk 
   */

// This packet is for sending out to players
@AllArgsConstructor
@NoArgsConstructor
public class GamePacket {

   public PacketType type;

}
