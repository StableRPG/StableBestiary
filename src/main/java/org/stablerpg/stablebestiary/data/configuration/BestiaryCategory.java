package org.stablerpg.stablebestiary.data.configuration;

import lombok.Getter;
import org.stablerpg.stablebestiary.data.configuration.entry.BestiaryEntry;
import org.stablerpg.stablebestiary.gui.configuration.GUITemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Getter
public class BestiaryCategory {

  private final String id;
  private final Map<String, BestiarySubcategory> subcategoriesMap;
  private final Map<String, BestiaryEntry> entriesMap;
  private final GUITemplate GUITemplate;
  
  public BestiaryCategory(String id, GUITemplate GUITemplate) {
    this.id = id;
    this.subcategoriesMap = new HashMap<>();
    this.entriesMap = new HashMap<>();
    this.GUITemplate = GUITemplate;
  }

  public Collection<BestiarySubcategory> getSubcategories() {
    return subcategoriesMap.values();
  }

  public void addSubcategory(BestiarySubcategory category) {
    subcategoriesMap.put(category.getId(), category);
  }

  public Collection<BestiaryEntry> getEntries() {
    return entriesMap.values();
  }

  public Collection<BestiaryEntry> getAllEntries() {
    Collection<BestiaryEntry> entries = new HashSet<>(entriesMap.values());
    for (BestiaryCategory category : subcategoriesMap.values())
      entries.addAll(category.getAllEntries());
    return entries;
  }

  public void addEntry(BestiaryEntry entry) {
    entriesMap.put(entry.getId(), entry);
  }
  
}
