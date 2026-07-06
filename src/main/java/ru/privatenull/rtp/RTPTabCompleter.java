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

        if (args.length == 1) {
            if (sender instanceof Player && sender.hasPermission("pnrtp.admin")) {
                completions.add("reload");
            }
            if (sender.hasPermission("pnrtp.use")) {
                completions.add("near");
                completions.add("far");
                completions.add("long");
            }
        }

        return completions;
    }
}