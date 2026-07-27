package com.example.legendaryshop.gui;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShardShopGUI {

    private final LegendaryShop plugin;

    public ShardShopGUI(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    /** Danh sach id theo dung thu tu: tat ca "keys" truoc, roi den tat ca "spawners". */
    public static List<String> getKeyIds(LegendaryShop plugin) {
        List<String> ids = new ArrayList<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("shard-shop.keys");
        if (section != null) ids.addAll(section.getKeys(false));
        return ids;
    }

    public static List<String> getSpawnerIds(LegendaryShop plugin) {
        List<String> ids = new ArrayList<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("shard-shop.spawners");
        if (section != null) ids.addAll(section.getKeys(false));
        return ids;
    }

    public void open(Player player) {
        List<String> keyIds = getKeyIds(plugin);
        List<String> spawnerIds = getSpawnerIds(plugin);

        int keyRows = Math.max(1, (int) Math.ceil(keyIds.size() / 9.0));
        int spawnerRows = Math.max(1, (int) Math.ceil(spawnerIds.size() / 9.0));
        int size = Math.min(54, (1 + keyRows + spawnerRows + 1) * 9);

        String title = plugin.getConfig().getString("gui.shard-shop-title", "Shop - Shard");
        Inventory inv = Bukkit.createInventory(null, size, GuiUtil.colorize(title));

        ItemStack filler = GuiUtil.filler(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }

        int slot = 9;
        for (String id : keyIds) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("shard-shop.keys." + id);
            if (section == null) {
                slot++;
                continue;
            }
            Material material = Material.matchMaterial(section.getString("material", "TRIPWIRE_HOOK"));
            if (material == null) material = Material.TRIPWIRE_HOOK;
            String displayName = section.getString("display-name", GuiUtil.properCase(id));
            double price = section.getDouble("price");

            inv.setItem(slot, GuiUtil.item(material,
                    displayName,
                    "&7Gia: &d" + formatPrice(price) + " Shards",
                    "",
                    "&eNhan de mua"));
            slot++;
        }

        slot = 9 + keyRows * 9;
        for (String id : spawnerIds) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("shard-shop.spawners." + id);
            if (section == null) {
                slot++;
                continue;
            }
            String entityTypeName = section.getString("entity-type", "PIG");
            double price = section.getDouble("price");
            String niceName = GuiUtil.properCase(entityTypeName) + " Spawner";

            inv.setItem(slot, GuiUtil.item(Material.SPAWNER,
                    "&b" + niceName,
                    "&7Gia: &d" + formatPrice(price) + " Shards",
                    "&7(Dua qua SmartSpawner)",
                    "",
                    "&eNhan de mua"));
            slot++;
        }

        int backSlot = size - 5;
        inv.setItem(backSlot, GuiUtil.item(Material.ARROW, "&cQuay lai", "&7Tro ve shop chinh"));

        player.openInventory(inv);
    }

    private String formatPrice(double price) {
        if (price == Math.floor(price)) {
            return String.valueOf((long) price);
        }
        return String.valueOf(price);
    }
}
