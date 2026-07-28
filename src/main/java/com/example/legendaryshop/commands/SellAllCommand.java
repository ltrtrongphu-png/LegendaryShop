package com.example.legendaryshop.commands;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SellAllCommand implements CommandExecutor {

    private final LegendaryShop plugin;

    public SellAllCommand(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Lenh nay chi danh cho nguoi choi.");
            return true;
        }
        if (!player.hasPermission("legendaryshop.sell")) {
            player.sendMessage(ChatColor.RED + "Ban khong co quyen dung lenh nay.");
            return true;
        }
        if (!plugin.getSellManager().isEnabled()) {
            player.sendMessage(plugin.sellMsg("disabled"));
            return true;
        }

        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getStorageContents();

        double total = 0;
        int itemTypesSold = 0;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() == Material.AIR) continue;

            Material type = item.getType();
            if (!plugin.getSellManager().isSellable(type)) continue;

            double unitPrice = plugin.getSellManager().getUnitPrice(type);
            total += unitPrice * item.getAmount();
            itemTypesSold++;
            contents[i] = null;
        }

        if (total <= 0) {
            player.sendMessage(plugin.sellMsg("nothing-to-sell"));
            return true;
        }

        inv.setStorageContents(contents);
        plugin.getEconomyManager().deposit(player, total);

        player.sendMessage(plugin.sellMsg("sold-all")
                .replace("%count%", String.valueOf(itemTypesSold))
                .replace("%price%", formatNumber(total)));
        plugin.playSellSound(player);
        return true;
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.format("%,d", (long) value);
        }
        return String.format("%,.2f", value);
    }
}
