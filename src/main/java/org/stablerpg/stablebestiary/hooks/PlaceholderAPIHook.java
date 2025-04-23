package org.stablerpg.stablebestiary.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stablerpg.stablebestiary.BestiaryPlatform;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PlaceholderAPIHook extends PlaceholderExpansion {

  public static void load(BestiaryPlatform platform) {
    new PlaceholderAPIHook(platform);
  }

  private BestiaryPlatform platform;

  private PlaceholderAPIHook(BestiaryPlatform platform) {
    this.platform = platform;
    register();
  }

  @Override
  public @NotNull String getName() {
    return platform.getPlugin().getName();
  }

  @Override
  public @NotNull String getIdentifier() {
    return platform.getPlugin().getName().toLowerCase();
  }

  @Override
  public @NotNull String getAuthor() {
    return String.join(", ", platform.getPlugin().getPluginMeta().getAuthors());
  }

  @Override
  public @NotNull String getVersion() {
    return platform.getPlugin().getPluginMeta().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @NotNull List<String> getPlaceholders() {
    return List.of(
    );
  }

  @Override
  public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {
    return onRequest(player, params);
  }

  @Override
  public @Nullable String onRequest(final OfflinePlayer player, @NotNull final String params) {
    String[] args = params.split("_");
    if (args.length == 0)
      return null;
    return null;
  }

}
