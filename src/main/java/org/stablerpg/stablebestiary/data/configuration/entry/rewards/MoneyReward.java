package org.stablerpg.stablebestiary.data.configuration.entry.rewards;

import org.bukkit.entity.Player;
import org.stablerpg.stablebestiary.hooks.VaultHook;

public class MoneyReward implements LevelReward {

  private final double amount;

  public MoneyReward(double amount) {
    this.amount = amount;
  }

  @Override
  public void execute(Player player) {
    VaultHook.get().getEconomy().depositPlayer(player, amount);
  }

}
