package org.stablerpg.stablebestiary.data.configuration.entry.rewards;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemReward implements LevelReward {

  private final ItemStack item;

  public ItemReward(ItemStack item) {
    this.item = item;
  }

  @Override
  public void execute(Player player) {
    if (player.getInventory().firstEmpty() != -1) {
      player.getInventory().addItem(item);
    } else {
      player.getWorld().dropItem(player.getLocation(), item);
    }
  }

}
