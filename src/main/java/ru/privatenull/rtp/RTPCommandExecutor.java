package ru.privatenull.rtp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.privatenull.pnrtpPlugin;

public class RTPCommandExecutor implements CommandExecutor {

    private final pnrtpPlugin plugin;
    private final RTPManager rtpManager;

    public RTPCommandExecutor(pnrtpPlugin plugin, RTPManager rtpManager) {
        this.plugin = plugin;
        this.rtpManager = rtpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        // Админ команда reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("pnrtp.admin")) {
                plugin.sendMessage(player, "no-permission");
                return true;
            }
            plugin.reloadPlugin();
            player.sendMessage(plugin.colorize("&aКонфигурация перезагружена!"));
            return true;
        }

        String mode = "far"; // default mode
        if (args.length >= 1) {
            String arg = args[0].toLowerCase();
            if (arg.equals("near") || arg.equals("far") || arg.equals("long")) {
                mode = arg;
            } else {
                plugin.sendMessage(player, "usage");
                return true;
            }
        } else {
            plugin.sendMessage(player, "usage");
            return true;
        }

        if (!player.hasPermission("pnrtp." + mode)) {
            plugin.sendMessage(player, "no-permission");
            return true;
        }

        if (!player.hasPermission("pnrtp.bypass") && rtpManager.isOnCooldown(player)) {
            long remaining = rtpManager.getRemainingCooldown(player);
            plugin.sendMessage(player, "cooldown-active", "{time}", String.valueOf(remaining));
            return true;
        }

        rtpManager.teleportPlayer(player, mode);
        return true;
    }

}