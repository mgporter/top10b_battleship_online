package io.mgporter.battleship_online.models;

import io.mgporter.battleship_online.config.StompPrincipal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {
  
  @EqualsAndHashCode.Include
  private String id;
  private String name;

  public static Player fromName(String name) {
    Player player = new Player();
    player.setName(name);
    return player;
  }

  public static Player fromPrincipal(StompPrincipal principal) {
    return new Player(principal.getPlayerId(), principal.getPlayerName());
  }

}
