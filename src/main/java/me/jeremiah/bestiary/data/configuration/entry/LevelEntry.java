package me.jeremiah.bestiary.data.configuration.entry;

import lombok.Getter;
import me.jeremiah.bestiary.data.configuration.entry.rewards.LevelReward;

@Getter
public class LevelEntry {

  private final int level;
  private final int requiredKills;

  private final LevelReward[] rewards;

  LevelEntry(int level, int requiredKills, LevelReward[] rewards) {
    this.level = level;
    this.requiredKills = requiredKills;
    this.rewards = rewards;
  }

}
