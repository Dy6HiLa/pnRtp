package ru.privatenull.rtp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.privatenull.pnrtpPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RTPManager {

    private final pnrtpPlugin plugin;
    private final Map<UUID, Long> cooldowns;
    private final Random random;

    public RTPManager(pnrtpPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
        this.random = new Random();
    }

    public void reloadConfig() {
        // Конфиг автоматически перезагружается через plugin.reloadConfig()
    }

    public boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }

        long cooldownEnd = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();

        if (currentTime >= cooldownEnd) {
            cooldowns.remove(playerId);
            return false;
        }

        return true;
    }

    public long getRemainingCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return 0;
        }

        long cooldownEnd = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        long remaining = cooldownEnd - currentTime;

        return Math.max(0, remaining / 1000);
    }

    public void setCooldown(Player player, String mode) {
        UUID playerId = player.getUniqueId();
        int cooldownSeconds = plugin.getCooldown(mode);
        cooldowns.put(playerId, System.currentTimeMillis() + (cooldownSeconds * 1000L));
    }

    public void teleportPlayer(Player player, String mode) {
        World world = player.getWorld();

        // Проверяем worldborder
        if (plugin.isWorldBorderCheck()) {
            if (world.getWorldBorder() == null) {
                plugin.sendMessage(player, "invalid-world");
                return;
            }
        }

        plugin.sendMessage(player, "teleporting");

        // Асинхронный поиск локации
        new BukkitRunnable() {
            @Override
            public void run() {
                Location safeLocation = findSafeLocation(player.getLocation(), mode);

                if (safeLocation == null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.sendMessage(player, "no-safe-location");
                        }
                    }.runTask(plugin);
                    return;
                }

                // Меняем кд
                setCooldown(player, mode);

                // Телепортируем игрока
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        safeLocation.getChunk().load();
                        player.teleportAsync(safeLocation).thenAccept(success -> {
                            if (success) {
                                plugin.sendMessage(player, "teleported");
                                plugin.sendTitle(player);
                            } else {
                                plugin.sendMessage(player, "error");
                            }
                        });
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    private Location findSafeLocation(Location origin, String mode) {
        int radius = plugin.getRadius(mode);
        int maxAttempts = 1000;
        World world = origin.getWorld();

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            double offsetX = (random.nextDouble() * 2 - 1) * radius;
            double offsetZ = (random.nextDouble() * 2 - 1) * radius;

            double x = origin.getX() + offsetX;
            double z = origin.getZ() + offsetZ;

            Location location = findHighestSafeY(world, x, z);

            if (location != null && isLocationSafe(location)) {
                return location;
            }
        }

        return null;
    }

    private Location findHighestSafeY(World world, double x, double z) {
        int maxY = world.getMaxHeight();
        int minY = world.getMinHeight();

        // Начинаем с максимальной высоты и спускаемся вниз
        for (int y = maxY - 1; y > minY; y--) {
            Block block = world.getBlockAt((int) Math.floor(x), y, (int) Math.floor(z));
            Material type = block.getType();

            // Ищем твердую поверхность
            if (isSolidBlock(type) && !isLeaves(block.getType())) {
                Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
                return loc;
            }
        }

        return null;
    }

    private boolean isLocationSafe(Location location) {
        World world = location.getWorld();
        double checkRadius = plugin.getSafetyCheckRadius();

        // Проверяем центральный блок
        Block centerBlock = world.getBlockAt(location);
        Material centerType = centerBlock.getType();

        // Избегаем воды и лавы
        if (plugin.avoidWater() && isWater(centerType)) {
            return false;
        }
        if (plugin.avoidLava() && isLava(centerType)) {
            return false;
        }

        // Проверяем область вокруг (чтобы не оказаться в дереве)
        int checkBlocks = (int) Math.ceil(checkRadius);

        for (int x = -checkBlocks; x <= checkBlocks; x++) {
            for (int y = 0; y <= checkBlocks + 2; y++) {
                for (int z = -checkBlocks; z <= checkBlocks; z++) {
                    Block block = world.getBlockAt(
                            (int) (location.getX() + x),
                            (int) (location.getY() + y),
                            (int) (location.getZ() + z)
                    );

                    Material type = block.getType();

                    // Проверяем опасные блоки
                    if (plugin.avoidWater() && isWater(type)) {
                        return false;
                    }
                    if (plugin.avoidLava() && isLava(type)) {
                        return false;
                    }

                    // Избегаем деревьев
                    if (plugin.avoidTrees() && (isLeaves(type) || isLog(type))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean isSolidBlock(Material type) {
        return type.isSolid() && !type.isAir();
    }

    private boolean isWater(Material type) {
        return type == Material.WATER || type == Material.LAVA;
    }

    private boolean isLava(Material type) {
        return type == Material.LAVA;
    }

    private boolean isLeaves(Material type) {
        return type.name().endsWith("LEAVES") || type.name().endsWith("LEAF");
    }

    private boolean isLog(Material type) {
        return type.name().endsWith("LOG") || type.name().endsWith("WOOD");
    }

}