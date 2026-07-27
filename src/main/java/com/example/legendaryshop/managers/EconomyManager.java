package com.example.legendaryshop.managers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.example.legendaryshop.LegendaryShop;

public class EconomyManager {

    private final LegendaryShop plugin;
    private Economy economy;

    public EconomyManager(LegendaryShop plugin) {
        this.plugin = plugin;
        setup();
    }

    private boolean setup() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Khong tim thay Vault! Cac danh muc mua bang tien se khong hoat dong.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("Khong tim thay plugin economy nao dang ky voi Vault!");
            return false;
        }
        this.economy = rsp.getProvider();
        return true;
    }

    public boolean isAvailable() {
        return economy != null;
    }

    public double getBalance(Player player) {
        if (!isAvailable()) return 0;
        return economy.getBalance((OfflinePlayer) player);
    }

    public boolean has(Player player, double amount) {
        if (!isAvailable()) return false;
        return economy.has((OfflinePlayer) player, amount);
    }

    public void withdraw(Player player, double amount) {
        if (!isAvailable()) return;
        economy.withdrawPlayer((OfflinePlayer) player, amount);
    }

    public void deposit(Player player, double amount) {
        if (!isAvailable()) return;
        economy.depositPlayer((OfflinePlayer) player, amount);
    }
}
