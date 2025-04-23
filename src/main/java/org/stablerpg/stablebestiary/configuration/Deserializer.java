package org.stablerpg.stablebestiary.configuration;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.stablerpg.stablebestiary.gui.configuration.ClickAction;
import org.stablerpg.stablebestiary.gui.configuration.CustomIcon;
import org.stablerpg.stablebestiary.gui.configuration.GUITemplate;
import org.stablerpg.stablebestiary.gui.configuration.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Deserializer {

  public static GUITemplate deserializeGUITemplate(ConfigurationSection section) {
    int size = section.getInt("rows", 3) * 9;
    String title = section.getString("title", "<red>Undefined</red>");
    Material backgroundMaterial = Material.matchMaterial(section.getString("background-material", "gray_stained_glass_pane").toUpperCase());
    int[] backgroundSlots = section.getIntegerList("background-slots").stream()
      .mapToInt(Integer::intValue)
      .toArray();
    ConfigurationSection customIconsSection = section.getConfigurationSection("icons");

    if (customIconsSection == null)
      return new GUITemplate(size, title, backgroundMaterial, backgroundSlots, Collections.emptyList());

    Collection<String> customIconKeys = customIconsSection.getKeys(false);
    List<CustomIcon> customIcons = new ArrayList<>();

    for (String key : customIconKeys)
      customIcons.add(deserializeCustomIcon(customIconsSection.getConfigurationSection(key)));

    return new GUITemplate(size, title, backgroundMaterial, backgroundSlots, customIcons);
  }

  private static final Function<int[], CustomIcon> DEFAULT_CUSTOM_ICON = slots -> new CustomIcon(
    slots,
    Material.BARRIER,
    "<red>Undefined</red>",
    Collections.emptyList()
  );

  public static CustomIcon deserializeCustomIcon(ConfigurationSection section) {
    int[] slots = section.getIntegerList("slots").stream()
      .mapToInt(Integer::intValue)
      .toArray();

    String linkType = section.getString("link.type", "NONE").toUpperCase();
    Link link = new Link(Link.Type.valueOf(linkType));
    if (linkType.equals("ENTRY") || linkType.equals("CATEGORY")) {
      String target = section.getString("link.target", null);
      link.setTarget(target);
    }

    String rawMaterial = section.getString("material");
    if (rawMaterial == null)
      return DEFAULT_CUSTOM_ICON.apply(slots);
    Material material = Material.matchMaterial(rawMaterial.toUpperCase());
    if (material == null)
      return DEFAULT_CUSTOM_ICON.apply(slots);
    String displayName = section.getString("display-name", "<red>Undefined</red>");
    List<String> lore = section.getStringList("lore");
    String clickActionType = section.getString("click-action.type", "NONE").toUpperCase();
    return new CustomIcon(slots, link, material, displayName, lore, new ClickAction(ClickAction.Type.valueOf(clickActionType)));
  }

}
