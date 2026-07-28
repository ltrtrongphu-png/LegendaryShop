package com.example.legendaryshop.gui;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SellGui {

    private final LegendaryShop plugin;

    public SellGui(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        int rows = plugin.getConfig().getInt("sell.gui-rows", 6);
        int size = Math.max(18, Math.min(54, rows * 9));
        String title = plugin.getConfig().getString("sell.gui-title", "Ban Vat Pham");

        Inventory inv = Bukkit.createInventory(null, size, GuiUtil.colorize(title));

        int lastRowStart = size - 9;
        int confirmSlot = plugin.getConfig().getInt("sell.confirm-slot", lastRowStart + 4);
        int cancelSlot = plugin.getConfig().getInt("sell.cancel-slot", lastRowStart);

        // Fill toan bo hang cuoi bang filler, roi dat 2 nut vao (bao ve khoi bi keo item vao)
        for (int i = lastRowStart; i < size; i++) {
            inv.setItem(i, GuiUtil.filler(Material.GRAY_STAINED_GLASS_PANE));
        }
        inv.setItem(confirmSlot, GuiUtil.item(Material.EMERALD_BLOCK, "&aXAC NHAN BAN",
                "&7Ban tat ca vat pham hop le", "&7dang dat trong cac o phia tren"));
        inv.setItem(cancelSlot, GuiUtil.item(Material.BARRIER, "&cHUY",
                "&7Tra lai toan bo vat pham"));

        player.openInventory(inv);
    }

    public static int lastRowStart(int size) {
        return size - 9;
    }

    public static boolean isSellGuiTitle(LegendaryShop plugin, String title) {
        String expected = GuiUtil.colorize(plugin.getConfig().getString("sell.gui-title", "Ban Vat Pham"));
        return title.equals(expected);
    }
}
