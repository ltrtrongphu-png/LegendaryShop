package com.example.legendaryshop.commands;

import com.example.legendaryshop.LegendaryShop;
import com.example.legendaryshop.gui.MainShopGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final LegendaryShop plugin;

    public ShopCommand(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Lenh nay chi danh cho nguoi choi.");
            return true;
        }
        if (!player.hasPermission("legendaryshop.use")) {
            player.sendMessage(ChatColor.RED + "Ban khong co quyen dung lenh nay.");
            return true;
        }
        new MainShopGUI(plugin).open(player);
        return true;
    }
}
