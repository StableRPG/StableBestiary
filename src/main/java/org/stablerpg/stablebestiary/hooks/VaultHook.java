package org.stablerpg.stablebestiary.hooks;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.stablerpg.stablebestiary.BestiaryPlatform;

public class VaultHook {

  private static VaultHook hook;

  public static void load(BestiaryPlatform platform) {
    if (Bukkit.getPluginManager().getPlugin("Vault") == null)
      return;
    RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
    if (rsp == null)
      return;
    Economy economy = rsp.getProvider();
    hook = new VaultHook(platform, economy);
  }

  public static VaultHook get() {
    return hook;
  }

  private final BestiaryPlatform platform;
  @Getter
  private final Economy economy;

  private VaultHook(BestiaryPlatform platform, Economy economy) {
    this.platform = platform;
    this.economy = economy;
  }

}
