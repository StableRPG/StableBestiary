package org.stablerpg.stablebestiary;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.stablerpg.stablebestiary.data.BestiaryPlayer;
import org.stablerpg.stablebestiary.gui.BestiaryGUI;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class BestiaryCommand implements BasicCommand {

  private final BestiaryPlatform platform;

  public BestiaryCommand(BestiaryPlatform platform) {
    this.platform = platform;
  }

  @Override
  public void execute(@NotNull CommandSourceStack source, String @NotNull [] args) {
    CommandSender sender = source.getSender();
    if (args.length == 0) {
      if (!(sender instanceof Player player)) {
        sender.sendRichMessage("<red>This command can only be executed by a player.");
        return;
      }
      BestiaryPlayer bPlayer = platform.getPlayer(player.getUniqueId());
      BestiaryGUI.open(bPlayer, player, platform.getMainCategory()).open(player);
    }

    if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
      sender.sendRichMessage("<gray>Reloading Bestiary...");
      platform.close();
      platform.load();
      sender.sendRichMessage("<green>Reloaded Bestiary!");
      return;
    }
  }

  @Override
  public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
    CommandSender sender = commandSourceStack.getSender();
    if (args.length == 0 && sender.hasPermission("bestiary.admin")) {
      return List.of("reload");
    }
    return Collections.emptyList();
  }

}
