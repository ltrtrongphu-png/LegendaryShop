package com.example.legendaryshop;

import com.example.legendaryshop.commands.SellAllCommand;
import com.example.legendaryshop.commands.SellCommand;
import com.example.legendaryshop.commands.SellGuiCommand;
import com.example.legendaryshop.commands.ShopAdminCommand;
import com.example.legendaryshop.commands.ShopCommand;
import com.example.legendaryshop.listeners.InventoryClickListener;
import com.example.legendaryshop.listeners.SellGuiListener;
import com.example.legendaryshop.managers.EconomyManager;
import com.example.legendaryshop.managers.SellManager;
import com.example.legendaryshop.managers.ShardsManager;
import com.example.legendaryshop.managers.ShopManager;
import com.example.legendaryshop.managers.SpawnerManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LegendaryShop extends JavaPlugin {

    private static LegendaryShop instance;

    private EconomyManager economyManager;
    private ShardsManager shardsManager;
    private SpawnerManager spawnerManager;
    private ShopManager shopManager;
    private SellManager sellManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.economyManager = new EconomyManager(this);
        this.shardsManager = new ShardsManager(this);
        this.spawnerManager = new SpawnerManager(this);
        this.shopManager = new ShopManager();
        this.sellManager = new SellManager(this);

        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("legendaryshopadmin").setExecutor(new ShopAdminCommand(this));
        getCommand("sell").setExecutor(new SellCommand(this));
        getCommand("sellall").setExecutor(new SellAllCommand(this));
        getCommand("sellgui").setExecutor(new SellGuiCommand(this));

        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new SellGuiListener(this), this);

        getLogger().info("LegendaryShop da duoc kich hoat!");
        if (!economyManager.isAvailable()) {
            getLogger().warning("Vault/Economy khong san sang -> cac danh muc End/Nether/Gear/Food se khong mua duoc.");
        }
        if (!shardsManager.isAvailable()) {
            getLogger().warning("XShards khong duoc cai -> Shard Shop se khong mua duoc.");
        }
        if (!shardsManager.isBalanceCheckAvailable()) {
            getLogger().warning("PlaceholderAPI khong san sang -> khong the kiem tra so du Shards truoc khi mua (van co the tru am neu XShards khong tu chan).");
        }
        if (!spawnerManager.isAvailable()) {
            getLogger().warning("SmartSpawner khong duoc cai -> khong the dua spawner cho nguoi choi.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("LegendaryShop da tat.");
    }

    public static LegendaryShop getInstance() {
        return instance;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public ShardsManager getShardsManager() {
        return shardsManager;
    }

    public SpawnerManager getSpawnerManager() {
        return spawnerManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public SellManager getSellManager() {
        return sellManager;
    }

    public String msg(String path) {
        String m = getConfig().getString("messages." + path, "");
        return ChatColor.translateAlternateColorCodes('&', m);
    }

    public String sellMsg(String path) {
        String m = getConfig().getString("sell.messages." + path, "");
        return ChatColor.translateAlternateColorCodes('&', m);
    }

    public void playSellSound(Player player) {
        String soundName = getConfig().getString("sounds.purchase-successful", "");
        float volume = (float) getConfig().getDouble("sounds.purchase-successful-volume", 0.5);
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, 1.0f);
        } catch (IllegalArgumentException ignored) {
        }
    }

    public String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
