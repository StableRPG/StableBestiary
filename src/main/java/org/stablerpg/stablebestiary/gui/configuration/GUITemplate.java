package org.stablerpg.stablebestiary.gui.configuration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;

@Setter
@Getter
public class GUITemplate {

  private int size;
  private String title;

  private Material backgroundMaterial;
  private int[] backgroundSlots;

  private List<CustomIcon> customIcons;

  public GUITemplate(int size, String title, Material backgroundMaterial, int[] backgroundSlots, List<CustomIcon> customIcons) {
    this.size = size;
    this.title = title;
    this.backgroundMaterial = backgroundMaterial;
    this.backgroundSlots = backgroundSlots;
    this.customIcons = customIcons;
  }

}
