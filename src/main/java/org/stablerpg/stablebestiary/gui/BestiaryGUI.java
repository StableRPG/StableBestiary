package org.stablerpg.stablebestiary.gui;

import fr.mrmicky.fastinv.FastInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.stablerpg.stablebestiary.data.BestiaryPlayer;
import org.stablerpg.stablebestiary.data.configuration.BestiaryCategory;
import org.stablerpg.stablebestiary.gui.configuration.CustomIcon;

import java.util.function.Consumer;

public class BestiaryGUI extends FastInv {

  public static BestiaryGUI open(BestiaryPlayer bPlayer, Player player, BestiaryCategory category) {
    return new BestiaryGUI(bPlayer, player, category);
  }

  public static BestiaryGUI open(BestiaryPlayer bPlayer, Player player, BestiaryCategory category, BestiaryGUI prev) {
    return new BestiaryGUI(bPlayer, player, category, prev);
  }

  private final BestiaryPlayer bPlayer;
  private final Player player;
  private @Nullable BestiaryGUI prev;

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
    this.bPlayer = bPlayer;
    this.player = player;

    ItemStack backgroundItem = ItemStack.of(category.getGUITemplate().getBackgroundMaterial());
    backgroundItem.editMeta(meta -> meta.displayName(Component.space()));

    setItems(category.getGUITemplate().getBackgroundSlots(), backgroundItem);

    for (CustomIcon icon : category.getGUITemplate().getCustomIcons())
      loadCustomIcon(icon);
  }

  private void loadCustomIcon(CustomIcon icon) {
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
