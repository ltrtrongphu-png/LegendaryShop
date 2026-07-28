package com.example.legendaryshop.listeners;

import com.example.legendaryshop.LegendaryShop;
import com.example.legendaryshop.gui.SellGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SellGuiListener implements Listener {

    private final LegendaryShop plugin;

    public SellGuiListener(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!SellGui.isSellGuiTitle(plugin, title)) return;

        Inventory top = event.getView().getTopInventory();
        int size = top.getSize();
        int lastRowStart = SellGui.lastRowStart(size);

        int confirmSlot = plugin.getConfig().getInt("sell.confirm-slot", lastRowStart + 4);
        int cancelSlot = plugin.getConfig().getInt("sell.cancel-slot", lastRowStart);

        int rawSlot = event.getRawSlot();

        // Click vao hang nut (hang cuoi cua GUI ban) -> luon huy thao tac mac dinh, chi xu ly rieng
        if (rawSlot >= lastRowStart && rawSlot < size) {
            event.setCancelled(true);
            if (rawSlot == confirmSlot) {
                confirmSell(player, top, lastRowStart);
            } else if (rawSlot == cancelSlot) {
                cancelSell(player, top, lastRowStart);
            }
        }
        // Cac o con lai (vung tha item) cho phep thao tac binh thuong (khong cancel)
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!SellGui.isSellGuiTitle(plugin, title)) return;

        int size = event.getView().getTopInventory().getSize();
        int lastRowStart = SellGui.lastRowStart(size);

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot >= lastRowStart && rawSlot < size) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!SellGui.isSellGuiTitle(plugin, title)) return;

        Inventory top = event.getView().getTopInventory();
        int size = top.getSize();
        int lastRowStart = SellGui.lastRowStart(size);

        // Tra lai vat pham con sot (vd nguoi choi bam ESC thay vi bam Xac nhan/Huy)
        returnItems(player, top, lastRowStart);
    }

    private void confirmSell(Player player, Inventory top, int lastRowStart) {
        double total = 0;
        int itemTypesSold = 0;

        for (int i = 0; i < lastRowStart; i++) {
            ItemStack item = top.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;

            Material type = item.getType();
            if (plugin.getSellManager().isSellable(type)) {
                total += plugin.getSellManager().getUnitPrice(type) * item.getAmount();
                itemTypesSold++;
                top.setItem(i, null);
            } else {
                giveOrDrop(player, item);
                top.setItem(i, null);
            }
        }

        if (total <= 0) {
            player.sendMessage(plugin.sellMsg("nothing-to-sell"));
            return;
        }

        plugin.getEconomyManager().deposit(player, total);
        player.sendMessage(plugin.sellMsg("gui-sold").replace("%price%", formatNumber(total)));
        plugin.playSellSound(player);
        player.closeInventory();
    }

    private void cancelSell(Player player, Inventory top, int lastRowStart) {
        returnItems(player, top, lastRowStart);
        player.sendMessage(plugin.sellMsg("gui-cancelled"));
        player.closeInventory();
    }

    private void returnItems(Player player, Inventory top, int lastRowStart) {
        for (int i = 0; i < lastRowStart; i++) {
            ItemStack item = top.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            giveOrDrop(player, item);
            top.setItem(i, null);
        }
    }

    private void giveOrDrop(Player player, ItemStack item) {
        for (ItemStack extra : player.getInventory().addItem(item).values()) {
            player.getWorld().dropItem(player.getLocation(), extra);
        }
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.format("%,d", (long) value);
        }
        return String.format("%,.2f", value);
    }
}
