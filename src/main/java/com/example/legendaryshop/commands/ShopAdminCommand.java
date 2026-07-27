package com.example.legendaryshop.commands;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopAdminCommand implements CommandExecutor {

    private final LegendaryShop plugin;

    public ShopAdminCommand(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("legendaryshop.admin")) {
            sender.sendMessage(ChatColor.RED + "Ban khong co quyen dung lenh nay.");
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.GOLD + "Dung: /legendaryshopadmin reload");
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Da reload config.yml cua LegendaryShop!");
        return true;
    }
}
