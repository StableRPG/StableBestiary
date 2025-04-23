package org.stablerpg.stablebestiary;

import fr.mrmicky.fastinv.FastInvManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class StableBestiary extends JavaPlugin {

  @Getter
  private static BestiaryPlatform platform;

  @Override
  public void onEnable() {
    FastInvManager.register(this);
    platform = new BestiaryPlatform(this);
    platform.load();
  }

  @Override
  public void onDisable() {
    platform.close();
  }

}
