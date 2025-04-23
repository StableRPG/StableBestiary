package org.stablerpg.stablebestiary.gui.configuration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.stablerpg.stablebestiary.StableBestiary;
import org.stablerpg.stablebestiary.data.BestiaryPlayer;
import org.stablerpg.stablebestiary.data.configuration.BestiaryCategory;
import org.stablerpg.stablebestiary.data.configuration.entry.BestiaryEntry;
import org.stablerpg.stablebestiary.data.configuration.entry.LevelController;
import org.stablerpg.stablebestiary.gui.BestiaryGUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
public class Link {

  private static final int PROGRESS_BAR_LENGTH = 25;

  private final Type type;
  private @Nullable String target;

  public Link(Type type) {
    this.type = type;
  }

  public List<String> getLore(BestiaryPlayer bPlayer) {
    List<String> lore = new ArrayList<>();
    switch (type) {
      case ENTRY -> {
        LevelController lc = StableBestiary.getPlatform().getEntry(target).getLevelController();
        int kills = bPlayer.getEntryKills(target);
        int level = lc.calculateLevel(kills);
        int killsRequired = lc.getKillsRequiredForLevel(level);
        lore.add("");
        if (kills == 0) {
          lore.add("<red><bold>LOCKED");
        } else {
          lore.add("<gray>Level:</gray> <yellow>" + level + "</yellow>");
          double progress = (double) kills / killsRequired;
          int percentage = (int) (progress * 100);
          lore.add("<gray>Progress to Level " + (level + 1) + ":</gray> <yellow>" + percentage + "%</yellow>");
          lore.add(createProgressBar(kills, killsRequired));

          lore.add("");

          int overallKillsRequired = lc.getTotalKillsRequired();
          double overallProgress = (double) kills / overallKillsRequired;
          int overallPercentage = (int) (overallProgress * 100);

          lore.add("<gray>Overall Progress:</gray> <yellow>" + overallPercentage + "%</yellow>");
          lore.add(createProgressBar(kills, overallKillsRequired));
        }
      }
      case CATEGORY -> {
        BestiaryCategory category = StableBestiary.getPlatform().getCategory(target);
        Collection<BestiaryEntry> entries = category.getAllEntries();
        int totalEntries = entries.size();
        int discoveredEntries = 0;
        int completeEntries = 0;
        for (BestiaryEntry entry : entries) {
          int kills = bPlayer.getEntryKills(entry);
          if (kills > 0) {
            discoveredEntries++;
            if (kills >= entry.getLevelController().getTotalKillsRequired())
              completeEntries++;
          }
        }
        int discoveredPercentage = (int) ((double) discoveredEntries / totalEntries * 100);
        int completePercentage = (int) ((double) completeEntries / totalEntries * 100);

        lore.add("");
        lore.add("<gray>Discovered Entries:</gray> <yellow>" + discoveredPercentage + "%</yellow>");
        lore.add(createProgressBar(discoveredEntries, totalEntries));
        lore.add("");
        lore.add("<gray>Complete Entries:</gray> <yellow>" + completePercentage + "%</yellow>");
        lore.add(createProgressBar(completeEntries, totalEntries));
        lore.add("");
        lore.add("<gray>Click to view</gray>");
      }
    }
    return lore;
  }

  private String createProgressBar(int kills, int requirement) {
    double progress = (double) kills / requirement;
    int progressInLines = (int) (progress * PROGRESS_BAR_LENGTH);
    StringBuilder progressBar = new StringBuilder("<st><green>");
    for (int i = 0; i < PROGRESS_BAR_LENGTH; i++) {
      if (progressInLines == i)
        progressBar.append("</green><white>");
      progressBar.append(" ");
    }
    progressBar
      .append("</white></st> <yellow>")
      .append(kills)
      .append("</yellow><gold>/</gold><yellow>")
      .append(requirement)
      .append("</yellow>");
    return progressBar.toString();
  }

  public void execute(BestiaryPlayer bPlayer, Player player, BestiaryGUI current) {
    switch (type) {
      case CATEGORY -> {
        BestiaryCategory category = StableBestiary.getPlatform().getCategory(target);
        BestiaryGUI gui = BestiaryGUI.open(bPlayer, player, category, current);
        player.closeInventory();
        gui.open(player);
      }
    }
  }

  public enum Type {
    NONE,
    ENTRY,
    CATEGORY
  }

}
