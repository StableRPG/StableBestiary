package me.jeremiah.bestiary.data.configuration.entry.rewards;

import org.bukkit.entity.Player;

public class ExpReward implements LevelReward {

  private final int exp;

  public ExpReward(int exp) {
    this.exp = exp;
  }

  @Override
  public void execute(Player player) {
    player.giveExp(exp);
  }

}
