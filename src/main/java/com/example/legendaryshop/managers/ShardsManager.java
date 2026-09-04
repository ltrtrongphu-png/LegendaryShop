package com.example.legendaryshop.managers;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ShardsManager {

    private final LegendaryShop plugin;
    private final boolean placeholderApiAvailable;
    private final boolean xShardsAvailable;

    public ShardsManager(LegendaryShop plugin) {
        this.plugin = plugin;
        this.placeholderApiAvailable = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        this.xShardsAvailable = plugin.getServer().getPluginManager().getPlugin("XShards") != null;
    }

    public boolean isAvailable() {
        return xShardsAvailable;
    }

    public boolean isBalanceCheckAvailable() {
        return xShardsAvailable && placeholderApiAvailable;
    }

    /**
     * Doc so du Shards hien tai cua nguoi choi qua PlaceholderAPI (%xshards_playershards%).
     * Tra ve -1 neu khong doc duoc (thieu PlaceholderAPI hoac chua co gia tri hop le).
     * Luu y: XShards khong cung cap API Java cong khai, nen day la cach duy nhat
     * de DOC so du tu ben ngoai; viec TRU/CONG van dung lenh console ben duoi.
     */
    public double getBalance(Player player) {
        if (!isBalanceCheckAvailable()) {
            return -1;
        }
        String placeholder = plugin.getConfig().getString("integrations.xshards.balance-placeholder", "%xshards_playershards%");
        try {
            String parsed = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, placeholder);
            if (parsed == null) return -1;
            String numeric = parsed.replaceAll("[^0-9.\\-]", "");
            if (numeric.isEmpty()) return 0;
            return Double.parseDouble(numeric);
        } catch (Exception e) {
            plugin.getLogger().warning("Khong doc duoc so du Shards qua PlaceholderAPI: " + e.getMessage());
            return -1;
        }
    }

    public void withdraw(Player player, double amount) {
        // "remove-command" la ten key chuan. Van chap nhan "take-command" de tuong thich
        // nguoc voi config cu/go nham, nhung se canh bao ro trong console de biet ma sua.
        String path = "integrations.xshards.remove-command";
        if (plugin.getConfig().getString(path) == null
                && plugin.getConfig().getString("integrations.xshards.take-command") != null) {
            plugin.getLogger().warning("Config dang dung key 'take-command' (khong duoc code doc toi) thay vi 'remove-command'. "
                    + "Da tu dong dung tam 'take-command' de Shards van bi tru, nhung HAY DOI TEN KEY trong config.yml "
                    + "thanh 'remove-command' de tranh loi kho hieu ve sau.");
            path = "integrations.xshards.take-command";
        }
        runCommand(path, player, amount);
    }

    public void give(Player player, double amount) {
        runCommand("integrations.xshards.give-command", player, amount);
    }

    private void runCommand(String path, Player player, double amount) {
        if (!xShardsAvailable) {
            plugin.getLogger().warning("XShards chua duoc cai, khong the tru/cong Shards!");
            return;
        }
        String template = plugin.getConfig().getString(path);
        if (template == null || template.isEmpty()) {
            // Truoc day cho nay "return" am tham, khien viec mua hang bao thanh cong
            // nhung khong tru duoc Shards nao ma khong ai biet. Gio bat buoc phai canh bao to.
            plugin.getLogger().severe("[LegendaryShop] KHONG THE chay lenh Shards: thieu config key '" + path
                    + "' trong config.yml. Nguoi choi se KHONG bi tru/cong Shards cho den khi ban them dong nay vao config!");
            return;
        }
        if (!template.contains("%player%") || !template.contains("%amount%")) {
            plugin.getLogger().warning("[LegendaryShop] Lenh '" + path + "' trong config.yml thieu placeholder %player% hoac %amount% - "
                    + "lenh se chay sai vi khong the dien ten nguoi choi/so luong vao dung cho.");
        }
        long amt = Math.round(amount);
        String command = template.replace("%player%", player.getName()).replace("%amount%", String.valueOf(amt));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
