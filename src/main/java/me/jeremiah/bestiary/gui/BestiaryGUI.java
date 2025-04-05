package me.jeremiah.bestiary.gui;

import fr.mrmicky.fastinv.FastInv;
import me.jeremiah.bestiary.data.BestiaryPlayer;
import me.jeremiah.bestiary.data.configuration.BestiaryCategory;
import me.jeremiah.bestiary.gui.configuration.CustomIcon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BestiaryGUI extends FastInv {

  public static BestiaryGUI open(BestiaryPlayer bPlayer, Player player, BestiaryCategory category) {
    return new BestiaryGUI(bPlayer, player, category);
  }

  public static BestiaryGUI open(BestiaryPlayer bPlayer, Player player, BestiaryCategory category, BestiaryGUI prev) {
    return new BestiaryGUI(bPlayer, player, category, prev);
  }

  public @Nullable BestiaryGUI prev;

  private BestiaryGUI(BestiaryPlayer bPlayer, Player player, BestiaryCategory category, @Nullable BestiaryGUI prev) {
    this(bPlayer, player, category);
    this.prev = prev;
  }

  private BestiaryGUI(BestiaryPlayer bPlayer, Player player, BestiaryCategory category) {
    super(owner -> {
      int size = category.getGUITemplate().getSize();
      Component title = MiniMessage.miniMessage().deserialize(category.getGUITemplate().getTitle());
      return Bukkit.createInventory(owner, size, title);
    });

    ItemStack backgroundItem = new ItemStack(category.getGUITemplate().getBackgroundMaterial(), 1);
    backgroundItem.editMeta(meta -> meta.displayName(Component.space()));

    setItems(category.getGUITemplate().getBackgroundSlots(), backgroundItem);

    for (CustomIcon icon : category.getGUITemplate().getCustomIcons())
      loadCustomIcon(icon, bPlayer, player);
  }

  private void loadCustomIcon(CustomIcon icon, BestiaryPlayer bPlayer, Player player) {
    ItemStack item = icon.getIcon(bPlayer, player);
    Consumer<InventoryClickEvent> clickHandler = null;
    switch (icon.getLink().getType()) {
      case CATEGORY, ENTRY -> clickHandler = inventoryClickEvent -> icon.getLink().execute(bPlayer, player, this);
    }
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
