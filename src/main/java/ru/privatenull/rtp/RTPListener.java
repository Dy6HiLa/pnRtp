package ru.privatenull.rtp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.privatenull.pnrtpPlugin;

public class RTPListener implements Listener {

    private final RTPManager rtpManager;
    private final pnrtpPlugin plugin;

    public RTPListener(RTPManager rtpManager, pnrtpPlugin plugin) {
        this.rtpManager = rtpManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Можно добавить автоматическую телепортацию при входе в определенных мирах
        // или другие события
    }
}