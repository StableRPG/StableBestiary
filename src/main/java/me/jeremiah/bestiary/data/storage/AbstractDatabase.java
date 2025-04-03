package me.jeremiah.bestiary.data.storage;

import me.jeremiah.bestiary.BestiaryPlatform;
import me.jeremiah.bestiary.data.BestiaryPlayer;
import me.jeremiah.bestiary.data.configuration.DatabaseInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class AbstractDatabase implements Listener, Closeable {

  private final ScheduledExecutorService scheduler;
  private final BestiaryPlatform platform;

  private ScheduledFuture<?> autoSaveTask;

  protected Set<BestiaryPlayer> entries;
  protected Map<UUID, BestiaryPlayer> uuidMap;
  protected Map<String, BestiaryPlayer> nameMap;

  protected AbstractDatabase(BestiaryPlatform platform) {
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
    this.platform = platform;
  }

  protected BestiaryPlatform getPlatform() {
    return platform;
  }

  protected abstract int lookupEntryCount();

  protected void setup() {
    int initialCapacity = lookupEntryCount() * 2;
    entries = ConcurrentHashMap.newKeySet(initialCapacity);
    uuidMap = new ConcurrentHashMap<>(initialCapacity);
    nameMap = new ConcurrentHashMap<>(initialCapacity);
    load();
    DatabaseInfo dbInfo = platform.getDatabaseInfo();
    autoSaveTask = scheduler.scheduleAtFixedRate(this::save, dbInfo.getAutoSaveInterval(), dbInfo.getAutoSaveInterval(), TimeUnit.MINUTES);
    Bukkit.getPluginManager().registerEvents(this, platform.getPlugin());
  }

  protected void add(BestiaryPlayer bPlayer) {
    entries.add(bPlayer);
    uuidMap.put(bPlayer.getUniqueId(), bPlayer);
    nameMap.put(bPlayer.getUsername(), bPlayer);
  }

  public @Nullable BestiaryPlayer getBestiaryPlayer(Player player) {
    return getBestiaryPlayer(player.getUniqueId());
  }

  public @Nullable BestiaryPlayer getBestiaryPlayer(UUID uniqueId) {
    return uuidMap.get(uniqueId);
  }

  public @Nullable BestiaryPlayer getBestiaryPlayer(String name) {
    return nameMap.get(name);
  }

  protected abstract void load();

  protected abstract void save();

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
    if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED))
      return;
    BestiaryPlayer bPlayer = getBestiaryPlayer(event.getUniqueId());
    if (bPlayer != null) {
      bPlayer.setUsername(event.getName());
      return;
    }
    bPlayer = new BestiaryPlayer(event.getUniqueId(), event.getName());
    add(bPlayer);
  }

  @Override
  public void close() {
    autoSaveTask.cancel(false);
    save();
    entries.clear();
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(5, TimeUnit.SECONDS))
        scheduler.shutdownNow();
    } catch (InterruptedException exception) {
      platform.getLogger().log(Level.SEVERE, "Failed to shutdown scheduler", exception);
      scheduler.shutdownNow();
    }
  }

}
