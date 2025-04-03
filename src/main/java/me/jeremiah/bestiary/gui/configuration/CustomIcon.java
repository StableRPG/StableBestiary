package me.jeremiah.bestiary.gui.configuration;

import lombok.Getter;
import lombok.Setter;
import me.jeremiah.bestiary.data.BestiaryPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CustomIcon implements BestiaryIcon {

  private int[] slots;
  private Link link;

  private Material iconMaterial;
  private String displayName;
  private List<String> lore;
  private final ClickAction clickAction;

  public CustomIcon(int[] slots, Link link, Material iconMaterial, String displayName, List<String> lore, ClickAction clickAction) {
    this.slots = slots;
    this.link = link == null ? new Link(Link.Type.NONE) : link;
    this.iconMaterial = iconMaterial;
    this.displayName = displayName;
    this.lore = lore;
    this.clickAction = clickAction == null ? new ClickAction(ClickAction.Type.NONE) : clickAction;
  }

  public CustomIcon(int[] slots, Link link, Material iconMaterial, String displayName, List<String> lore) {
    this(slots, link, iconMaterial, displayName, lore, null);
  }

  public CustomIcon(int[] slots, Material iconMaterial, String displayName, List<String> lore, ClickAction clickAction) {
    this(slots, null, iconMaterial, displayName, lore, clickAction);
  }

  public CustomIcon(int[] slots, Material iconMaterial, String displayName, List<String> lore) {
    this(slots, null, iconMaterial, displayName, lore, null);
  }

  public void setLore(List<String> lore) {
    this.lore = new ArrayList<>(lore);
  }

  @Override
  public ItemStack getIcon(BestiaryPlayer bPlayer, Player player) {
    ItemStack itemStack = ItemStack.of(iconMaterial, 1);

    itemStack.editMeta(meta -> {
      meta.displayName(format(player, displayName));
      List<String> lore = new ArrayList<>(this.lore);
      lore.addAll(link.getLore(bPlayer));
      meta.lore(lore.stream().map(s -> format(player, s)).toList());
    });

    return itemStack;
  }

}
