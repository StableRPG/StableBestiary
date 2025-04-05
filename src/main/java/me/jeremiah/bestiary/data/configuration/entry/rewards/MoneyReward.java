package me.jeremiah.bestiary.data.configuration.entry.rewards;

import me.jeremiah.bestiary.hooks.VaultHook;
import org.bukkit.entity.Player;

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
