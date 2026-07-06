package ru.privatenull;

import org.bukkit.plugin.java.JavaPlugin;
import ru.privatenull.rtp.RTPCommandExecutor;
import ru.privatenull.rtp.RTPListener;
import ru.privatenull.rtp.RTPManager;
import ru.privatenull.rtp.RTPTabCompleter;

public final class pnrtpPlugin extends JavaPlugin {

    public static final String SUPPORT_DISCORD = "https://discord.gg/rRbzq6cnc6";

    private RTPManager rtpManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        rtpManager = new RTPManager(this);

        getCommand("rtp").setExecutor(new RTPCommandExecutor(this, rtpManager));
        getCommand("rtp").setTabCompleter(new RTPTabCompleter());

        getServer().getPluginManager().registerEvents(new RTPListener(rtpManager, this), this);

        logStartupMessage();
    }

    @Override
    public void onDisable() {
        getLogger().info("pnrtp отключён");
    }

    public void reloadPlugin() {
        reloadConfig();
        rtpManager.reloadConfig();
        getLogger().info("pnrtp: конфигурация перезагружена.");
    }

    public void sendMessage(org.bukkit.command.CommandSender sender, String path, String... replacements) {
        if (getConfig().isList("messages." + path)) {
            for (String line : getConfig().getStringList("messages." + path)) {
                sender.sendMessage(colorize(applyReplacements(line, replacements)));
            }
        } else {
            String msg = getConfig().getString("messages." + path);
            if (msg != null && !msg.isEmpty()) {
                sender.sendMessage(colorize(applyReplacements(msg, replacements)));
            }
        }
    }

    private String applyReplacements(String text, String... replacements) {
        if (text == null) return "";
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                text = text.replace(replacements[i], replacements[i + 1]);
            }
        }
        return text;
    }

    public String colorize(String message) {
        if (message == null) return "";
        return message.replace("&", "§");
    }

    public int getRadius() {
        return getConfig().getInt("radius", 5000);
    }

    public int getCooldown() {
        return getConfig().getInt("cooldown", 30);
    }

    public boolean isWorldBorderCheck() {
        return getConfig().getBoolean("worldborder-check", true);
    }

    public boolean avoidWater() {
        return getConfig().getBoolean("avoid-water", true);
    }

    public boolean avoidLava() {
        return getConfig().getBoolean("avoid-lava", true);
    }

    public boolean avoidTrees() {
        return getConfig().getBoolean("avoid-trees", true);
    }

    public double getSafetyCheckRadius() {
        return getConfig().getDouble("safety-check-radius", 5.0);
    }

    public RTPManager getRTPManager() {
        return rtpManager;
    }

    private void logStartupMessage() {
        logBanner();
        getLogger().info("pnrtp v" + getDescription().getVersion() + " включён");
        getLogger().info("Поддержка pnRtp: " + SUPPORT_DISCORD);
    }

    private void logBanner() {
        getLogger().info("██████╗░██████╗░██████╗░██████╗░██████╗░████████╗███████╗██████╗░");
        getLogger().info("██╔══██╗██╔══██╗██╔══██╗██╔══██╗██╔══██╗╚══██╔══╝██╔════╝██╔══██╗");
        getLogger().info("██████╔╝██████╔╝██████╔╝██████╔╝██████╔╝░░░██║░░░█████╗░░██████╔╝");
        getLogger().info("██╔═══╝░██╔══██╗██╔══██╗██╔══██╗██╔══██╗░░░██║░░░██╔══╝░░██╔══██╗");
        getLogger().info("██║░░░░░██║░░██║██║░░██║██║░░██║██║░░██║░░░██║░░░███████╗██║░░██║");
        getLogger().info("╚═╝░░░░░╚═╝░░╚═╝╚═╝░░╚═╝╚═╝░░╚═╝╚═╝░░╚═╝░░░╚═╝░░░╚══════╝╚═╝░░╚═╝");
    }
}