package io.mgporter.battleship_online.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
  
  private String id;
  private String name;

  public static Player fromName(String name) {
    Player player = new Player();
    player.setName(name);
    return player;
  }

}
