package me.jeremiah.bestiary.data;

import lombok.Getter;
import lombok.Setter;
import me.jeremiah.bestiary.data.configuration.entry.BestiaryEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class BestiaryPlayer implements Updatable {

  private final UUID uniqueId;
  private String username;

  private final Map<String, Integer> entries;
  private final ConcurrentHashMap<String, Integer> entryChanges;

  public BestiaryPlayer(UUID uniqueId, String username, Map<String, Integer> entries) {
    this.uniqueId = uniqueId;
    this.username = username;
    this.entries = entries;
    this.entryChanges = new ConcurrentHashMap<>();
  }

  public BestiaryPlayer(UUID uniqueId, String username) {
    this(uniqueId, username, new HashMap<>());
  }

  public int getEntryKills(BestiaryEntry entry) {
    return getEntryKills(entry.getId());
  }

  public int getEntryKills(String entryId) {
    return entries.getOrDefault(entryId, 0) + entryChanges.getOrDefault(entryId, 0);
  }

  public Integer getUnsavedEntryKills(BestiaryEntry entry) {
    return getUnsavedEntryKills(entry.getId());
  }

  public Integer getUnsavedEntryKills(String entryId) {
    return entryChanges.get(entryId);
  }

  public void updateEntry(BestiaryEntry entry) {
    updateEntry(entry, 1);
  }

  public void updateEntry(BestiaryEntry entry, int amount) {
    updateEntry(entry.getId(), amount);
  }

  public void updateEntry(String entryId, int amount) {
    entryChanges.compute(entryId, (key, currentValue) -> amount);
  }

  public void incrementEntry(BestiaryEntry entry) {
    incrementEntry(entry.getId(), 1);
  }

  public void incrementEntry(BestiaryEntry entry, int amount) {
    incrementEntry(entry.getId(), amount);
  }

  public void incrementEntry(String entryId, int amount) {
    entryChanges.compute(entryId, (key, currentValue) -> (currentValue == null ? 0 : currentValue) + amount);
  }

  public void decrementEntry(BestiaryEntry entry) {
    decrementEntry(entry.getId(), 1);
  }

  public void decrementEntry(BestiaryEntry entry, int amount) {
    decrementEntry(entry.getId(), amount);
  }

  public void decrementEntry(String entryId, int amount) {
    entryChanges.compute(entryId, (key, currentValue) -> currentValue == null ? 0 : Math.max(currentValue - amount, 0));
  }

  public void resetEntry(BestiaryEntry entry) {
    resetEntry(entry.getId());
  }

  public void resetEntry(String entryId) {
    entries.remove(entryId);
  }

  @Override
  public boolean hasBeenUpdated() {
    return !entryChanges.isEmpty();
  }

  @Override
  public void saveUpdates() {
    entryChanges.forEach((entryId, change) -> entries.merge(entryId, change, Integer::sum));
    entryChanges.clear();
  }

}
