package com.example.legendaryshop.managers;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnerManager {

    private final LegendaryShop plugin;
    private final boolean smartSpawnerAvailable;

    public SpawnerManager(LegendaryShop plugin) {
        this.plugin = plugin;
        this.smartSpawnerAvailable = plugin.getServer().getPluginManager().getPlugin("SmartSpawner") != null;
    }

    public boolean isAvailable() {
        return smartSpawnerAvailable;
    }

    /** Dua spawner dung loai mob cho nguoi choi thong qua lenh console cua SmartSpawner (/ss give). */
    public void giveSpawner(Player player, EntityType entityType, int amount) {
        if (!smartSpawnerAvailable) {
            plugin.getLogger().warning("SmartSpawner chua duoc cai, khong the dua spawner!");
            return;
        }
        String template = plugin.getConfig().getString("integrations.smartspawner.give-command", "ss give %player% %entitytype% %amount%");
        String command = template
                .replace("%player%", player.getName())
                .replace("%entitytype%", entityType.name().toLowerCase())
                .replace("%amount%", String.valueOf(amount));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
