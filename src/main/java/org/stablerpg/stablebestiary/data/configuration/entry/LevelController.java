package org.stablerpg.stablebestiary.data.configuration.entry;

import org.stablerpg.stablebestiary.data.configuration.entry.rewards.LevelReward;

import java.util.ArrayList;
import java.util.List;

public class LevelController {

  private final LevelEntry[] levels;

  private LevelController(LevelEntry[] levels) {
    this.levels = levels;
  }

  public int getMaxLevel() {
    return levels.length;
  }

  public int calculateLevel(int kills) {
    for (int level = 0; level < levels.length; level++)
      if (levels[level].getRequiredKills() > kills)
        return level;
    return 0;
  }

  public int getKillsRequiredForLevel(int level) {
    if (level < 0 || level >= levels.length)
      throw new IllegalArgumentException("Level out of bounds: " + level);
    return levels[level].getRequiredKills();
  }

  public int getTotalKillsRequired() {
    return getKillsRequiredForLevel(levels.length - 1);
  }

  public static class Builder {

    private final List<LevelEntry> levels = new ArrayList<>();

    public Builder addLevel(int level, int killsRequired, LevelReward[] rewards) {
      levels.add(new LevelEntry(level, killsRequired, rewards));
      return this;
    }

    public LevelController build() {
      return new LevelController(levels.toArray(new LevelEntry[0]));
    }

  }

}
