package ru.privatenull.rtp;

import org.bukkit.command.TabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RTPTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (sender instanceof Player && sender.hasPermission("pnrtp.admin")) {
            if (args.length == 1) {
                completions.add("reload");
            }
        }

        return completions;
    }
}