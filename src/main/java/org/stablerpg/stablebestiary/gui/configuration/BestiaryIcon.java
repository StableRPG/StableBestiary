package org.stablerpg.stablebestiary.gui.configuration;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.stablerpg.stablebestiary.data.BestiaryPlayer;

import java.util.List;

public interface BestiaryIcon {

  String getDisplayName();

  void setDisplayName(String displayName);

  Material getIconMaterial();

  void setIconMaterial(Material iconMaterial);

  List<String> getLore();

  default void setLore(String... lore) {
    setLore(List.of(lore));
  }

  void setLore(List<String> lore);

  default void addLore(String lore) {
    List<String> currentLore = getLore();
    currentLore.add(lore);
    setLore(currentLore);
  }

  default void addLore(String... lore) {
    addLore(List.of(lore));
  }

  default void addLore(List<String> lore) {
    List<String> currentLore = getLore();
    currentLore.addAll(lore);
    setLore(currentLore);
  }

  default void addLore(int index, String lore) {
    List<String> currentLore = getLore();
    if (index < 0 || index > currentLore.size())
      currentLore.add(lore);
    else
      currentLore.add(index, lore);
    setLore(currentLore);
  }

  default void addLore(int index, String... lore) {
    addLore(index, List.of(lore));
  }

  default void addLore(int index, List<String> lore) {
    List<String> currentLore = getLore();
    if (index < 0 || index > currentLore.size())
      currentLore.addAll(lore);
    else
      for (String s : lore)
        currentLore.add(index++, s);
    setLore(currentLore);
  }

  default void removeLore(String lore) {
    List<String> currentLore = getLore();
    currentLore.remove(lore);
    setLore(currentLore);
  }

  default void removeLore(String... lore) {
    removeLore(List.of(lore));
  }

  default void removeLore(List<String> lore) {
    List<String> currentLore = getLore();
    currentLore.removeAll(lore);
    setLore(currentLore);
  }

  default void removeLore(int index) {
    List<String> currentLore = getLore();
    if (index >= 0 && index < currentLore.size()) {
      currentLore.remove(index);
      setLore(currentLore);
    }
  }

  default void removeLore(int startIndex, int endIndex) {
    List<String> currentLore = getLore();
    if (startIndex < 0 || endIndex > currentLore.size() || startIndex >= endIndex)
      return;
    currentLore.subList(startIndex, endIndex).clear();
    setLore(currentLore);
  }

  ItemStack getIcon(BestiaryPlayer bPlayer, Player player);

  default Component format(Player player, String str, TagResolver... tags) {
    str = PlaceholderAPI.setPlaceholders(player, str);
    return Component.empty().decoration(TextDecoration.ITALIC, false)
      .append(MiniMessage.miniMessage().deserialize(str, tags));
  }

}
