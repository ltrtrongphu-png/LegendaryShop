package com.example.legendaryshop.gui;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainShopGUI {

    private final LegendaryShop plugin;

    public MainShopGUI(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        String title = GuiUtil.colorize(plugin.getConfig().getString("gui.main-shop-title", "Legendary Shop"));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack filler = GuiUtil.filler(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        Material endIcon = getIcon("end", Material.ENDER_CHEST);
        Material netherIcon = getIcon("nether", Material.NETHERRACK);
        Material gearIcon = getIcon("gear", Material.DIAMOND_CHESTPLATE);
        Material foodIcon = getIcon("food", Material.COOKED_BEEF);
        Material shardIcon = Material.matchMaterial(plugin.getConfig().getString("shard-shop.icon", "AMETHYST_SHARD"));
        if (shardIcon == null) shardIcon = Material.AMETHYST_SHARD;

        inv.setItem(11, GuiUtil.item(endIcon, "&d" + getName("end", "End"), "&7Nhan de mo shop End"));
        inv.setItem(12, GuiUtil.item(netherIcon, "&c" + getName("nether", "Nether"), "&7Nhan de mo shop Nether"));
        inv.setItem(13, GuiUtil.item(gearIcon, "&b" + getName("gear", "Gear"), "&7Nhan de mo shop Gear"));
        inv.setItem(14, GuiUtil.item(foodIcon, "&a" + getName("food", "Food"), "&7Nhan de mo shop Food"));
        inv.setItem(15, GuiUtil.item(shardIcon, "&5Shard", "&7Nhan de mo Shard Shop", "&7(Mua bang Shards)"));

        player.openInventory(inv);
    }

    private Material getIcon(String category, Material fallback) {
        Material m = Material.matchMaterial(plugin.getConfig().getString("categories." + category + ".icon", fallback.name()));
        return m != null ? m : fallback;
    }

    private String getName(String category, String fallback) {
        return plugin.getConfig().getString("categories." + category + ".display-name", fallback);
    }
}
