package ru.privatenull.rtp;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RTPTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender instanceof Player && sender.hasPermission("pnrtp.admin")) {
                completions.add("reload");
                completions.add("resetcd");
            }
            if (sender.hasPermission("pnrtp.near")) {
                completions.add("near");
            }
            if (sender.hasPermission("pnrtp.default")) {
                completions.add("default");
            }
            if (sender.hasPermission("pnrtp.far")) {
                completions.add("far");
            }
        }

        // Автодополнение имён игроков для resetcd
        if (args.length == 2 && args[0].equalsIgnoreCase("resetcd")) {
            if (sender.hasPermission("pnrtp.admin")) {
                String prefix = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
}