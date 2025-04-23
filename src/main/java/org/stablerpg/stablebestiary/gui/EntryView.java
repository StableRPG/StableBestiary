package org.stablerpg.stablebestiary.gui;

import fr.mrmicky.fastinv.FastInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.stablerpg.stablebestiary.data.BestiaryPlayer;
import org.stablerpg.stablebestiary.data.configuration.entry.BestiaryEntry;
import org.stablerpg.stablebestiary.gui.configuration.CustomIcon;

import java.util.function.Consumer;

public class EntryView extends FastInv {

  private final BestiaryPlayer bPlayer;
  private final Player player;
  private final BestiaryEntry entry;
  private final BestiaryGUI prev;

  private final int maxPage;

  private final ItemStack backgroundItem;
  private static final ItemStack previousPageItem = ItemStack.of(Material.ARROW);
  private static final ItemStack nextPageItem = ItemStack.of(Material.ARROW);
  static {
      previousPageItem.editMeta(meta -> meta.displayName(Component.text("Previous Page", NamedTextColor.YELLOW)));
      nextPageItem.editMeta(meta -> meta.displayName(Component.text("Next Page", NamedTextColor.YELLOW)));
  }
  /*
  start:
  Down 4
  Right 2
  Up 4
  Right 2
  goto start
   */

  private EntryView(BestiaryPlayer bPlayer, Player player, BestiaryEntry entry, BestiaryGUI prev) {
    super(owner -> {
      int size = 6;
      Component title = MiniMessage.miniMessage().deserialize("<red>Undefined Title</red>");
      return Bukkit.createInventory(owner, size, title);
    });
    this.bPlayer = bPlayer;
    this.player = player;
    this.entry = entry;
    this.prev = prev;

    this.maxPage = Math.ceilDiv(entry.getLevelController().getMaxLevel(), 19);

    backgroundItem = ItemStack.of(entry.getGuiTemplate().getBackgroundMaterial());
    backgroundItem.editMeta(meta -> meta.displayName(Component.space()));
    setItems(entry.getGuiTemplate().getBackgroundSlots(), backgroundItem);

    for (CustomIcon icon : entry.getGuiTemplate().getCustomIcons())
      loadCustomIcon(icon, bPlayer, player);
    loadPage(0);
  }

  private void loadPage(int page) {
    ItemStack previousPage = page != 0 ? previousPageItem : backgroundItem;
    ItemStack nextPage = page < maxPage ? nextPageItem : backgroundItem;
    if (page < 2)
      setItem(45, previousPage);
    if (page > maxPage - 2)
      setItem(53, nextPage);
  }

  private void loadCustomIcon(CustomIcon icon, BestiaryPlayer bPlayer, Player player) {
    ItemStack item = icon.getIcon(bPlayer, player);
    Consumer<InventoryClickEvent> clickHandler = null;
    switch (icon.getClickAction().getType()) {
      case CLOSE -> clickHandler = inventoryClickEvent -> player.closeInventory();
      case BACK -> clickHandler = inventoryClickEvent -> {
        if (prev != null) {
          player.closeInventory();
          prev.open(player);
        }
      };
      case PREVIOUS_PAGE -> {}
      case NEXT_PAGE -> {}
    }
    for (int slot : icon.getSlots())
      setItem(slot, item, clickHandler);
  }

}
