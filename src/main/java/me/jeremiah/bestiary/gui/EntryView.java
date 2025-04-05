package me.jeremiah.bestiary.gui;

import fr.mrmicky.fastinv.FastInv;
import me.jeremiah.bestiary.data.BestiaryPlayer;
import me.jeremiah.bestiary.data.configuration.entry.BestiaryEntry;
import me.jeremiah.bestiary.gui.configuration.CustomIcon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class EntryView extends FastInv {

  public BestiaryGUI prev;

  private EntryView(BestiaryPlayer bPlayer, Player player, BestiaryEntry entry, BestiaryGUI prev) {
    super(owner -> {
      int size = 6;
      Component title = MiniMessage.miniMessage().deserialize("<red>Undefined Title</red>");
      return Bukkit.createInventory(owner, size, title);
    });
    this.prev = prev;

    ItemStack backgroundItem = new ItemStack(entry.getGuiTemplate().getBackgroundMaterial(), 1);
    backgroundItem.editMeta(meta -> meta.displayName(Component.space()));

    setItems(entry.getGuiTemplate().getBackgroundSlots(), backgroundItem);

    for (CustomIcon icon : entry.getGuiTemplate().getCustomIcons())
      loadCustomIcon(icon, bPlayer, player);
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
