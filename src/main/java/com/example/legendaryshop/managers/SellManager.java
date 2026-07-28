package com.example.legendaryshop.managers;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class SellManager {

    private final LegendaryShop plugin;
    private final Map<Material, Double> prices = new HashMap<>();

    public SellManager(LegendaryShop plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        prices.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("sell.prices");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key.toUpperCase());
            if (material == null) {
                plugin.getLogger().warning("Khong tim thay Material: " + key + " trong sell.prices, da bo qua.");
                continue;
            }
            prices.put(material, section.getDouble(key));
        }
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("sell.enabled", true);
    }

    public boolean isSellable(Material material) {
        return prices.containsKey(material);
    }

    public double getUnitPrice(Material material) {
        return prices.getOrDefault(material, 0.0);
    }
}
