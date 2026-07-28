package com.example.legendaryshop.commands;

import com.example.legendaryshop.LegendaryShop;
import com.example.legendaryshop.gui.GuiUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellCommand implements CommandExecutor {

    private final LegendaryShop plugin;

    public SellCommand(LegendaryShop plugin) {
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

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) {
            player.sendMessage(plugin.sellMsg("nothing-in-hand"));
            return true;
        }

        Material type = hand.getType();
        if (!plugin.getSellManager().isSellable(type)) {
            player.sendMessage(plugin.sellMsg("not-sellable"));
            return true;
        }

        int amount = hand.getAmount();
        double unitPrice = plugin.getSellManager().getUnitPrice(type);
        double total = unitPrice * amount;

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        plugin.getEconomyManager().deposit(player, total);

        player.sendMessage(plugin.sellMsg("sold-hand")
                .replace("%amount%", String.valueOf(amount))
                .replace("%item%", GuiUtil.properCase(type.name()))
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
