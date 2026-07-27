package com.example.legendaryshop.gui;

import com.example.legendaryshop.LegendaryShop;
import com.example.legendaryshop.model.CurrencyType;
import com.example.legendaryshop.model.PurchaseContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BuyGUI {

    private final LegendaryShop plugin;

    public BuyGUI(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, PurchaseContext context, int initialQuantity) {
        plugin.getShopManager().setPurchaseContext(player, context);
        plugin.getShopManager().setQuantity(player, Math.max(1, initialQuantity));
        render(player);
    }

    public void updateQuantity(Player player, int newQuantity) {
        plugin.getShopManager().setQuantity(player, newQuantity);
        render(player);
    }

    private void render(Player player) {
        PurchaseContext context = plugin.getShopManager().getPurchaseContext(player);
        if (context == null) return;
        int quantity = plugin.getShopManager().getQuantity(player);

        String prefix = plugin.getConfig().getString("gui.buy-gui-title-prefix", "Mua - ");
        String name = context.getDisplayName() != null ? context.getDisplayName() : GuiUtil.properCase(context.getItemId());
        Inventory inv = Bukkit.createInventory(null, 27, GuiUtil.colorize(prefix + ChatColorStrip(name)));

        ItemStack filler = GuiUtil.filler(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        int maxStack = context.getDisplayMaterial().getMaxStackSize();
        if (maxStack <= 0) maxStack = 64;

        // Cac nut dieu chinh so luong (chi hien neu item stack duoc > 1, vd spawner/tripwire cung stack 64 nen van cho dieu chinh)
        inv.setItem(9, GuiUtil.item(Material.LIME_DYE, "&a1", "&7Dat so luong = 1"));
        inv.setItem(10, GuiUtil.item(Material.RED_DYE, "&c-10", "&7Giam 10"));
        inv.setItem(11, GuiUtil.item(Material.RED_DYE, "&c-1", "&7Giam 1"));

        double totalPrice = context.getPrice() * quantity;
        String currencyLabel = context.getCurrency() == CurrencyType.MONEY ? ("$" + formatNumber(totalPrice)) : (formatNumber(totalPrice) + " Shards");

        inv.setItem(13, GuiUtil.item(context.getDisplayMaterial(),
                "&f" + name + " &7x" + quantity,
                "&7Tong gia: &e" + currencyLabel,
                "",
                "&7Dung cac nut hai ben de doi so luong"));

        inv.setItem(15, GuiUtil.item(Material.LIME_DYE, "&a+1", "&7Tang 1"));
        inv.setItem(16, GuiUtil.item(Material.LIME_DYE, "&a+10", "&7Tang 10"));
        inv.setItem(17, GuiUtil.item(Material.LIME_DYE, "&aMAX", "&7Dat so luong toi da (" + maxStack + ")"));

        inv.setItem(21, GuiUtil.item(Material.ARROW, "&cQuay lai", "&7Tro ve danh muc"));
        inv.setItem(23, GuiUtil.item(Material.EMERALD_BLOCK, "&aXAC NHAN MUA", "&7Nhan de mua &f" + quantity + "x " + name, "&7Gia: &e" + currencyLabel));

        player.openInventory(inv);
    }

    private String ChatColorStrip(String name) {
        return org.bukkit.ChatColor.stripColor(GuiUtil.colorize(name));
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.format("%,d", (long) value);
        }
        return String.format("%,.2f", value);
    }
}
