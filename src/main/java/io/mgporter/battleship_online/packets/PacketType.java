package io.mgporter.battleship_online.packets;

public enum PacketType {
  ATTACK, ATTACK_MISSED, ATTACK_HITSHIP, ATTACK_SUNKSHIP, ATTACK_ALLSUNK,
  PLACED_SHIP, PLACED_COMPLETE, 
  GAME_INITIALIZED, GAME_START, GAME_ATTACK_PHASE_START, GAME_WAITING_FOR_PLAYERS,
  PLAYERLIST_UPDATE

}