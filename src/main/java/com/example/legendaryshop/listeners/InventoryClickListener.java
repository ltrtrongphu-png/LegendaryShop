package com.example.legendaryshop.listeners;

import com.example.legendaryshop.LegendaryShop;
import com.example.legendaryshop.gui.BuyGUI;
import com.example.legendaryshop.gui.CategoryShopGUI;
import com.example.legendaryshop.gui.GuiUtil;
import com.example.legendaryshop.gui.MainShopGUI;
import com.example.legendaryshop.gui.ShardShopGUI;
import com.example.legendaryshop.managers.EconomyManager;
import com.example.legendaryshop.managers.ShardsManager;
import com.example.legendaryshop.model.CurrencyType;
import com.example.legendaryshop.model.PurchaseContext;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryClickListener implements Listener {

    private final LegendaryShop plugin;
    private static final List<String> CATEGORIES = List.of("end", "nether", "gear", "food");

    public InventoryClickListener(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) return;
        if (clicked == player.getInventory()) return;

        String title = event.getView().getTitle();

        if (title.equals(mainTitle())) {
            event.setCancelled(true);
            handleMainShop(event, player);
            return;
        }

        for (String category : CATEGORIES) {
            if (title.equals(categoryTitle(category))) {
                event.setCancelled(true);
                handleCategoryShop(event, player, category);
                return;
            }
        }

        if (title.equals(shardTitle())) {
            event.setCancelled(true);
            handleShardShop(event, player);
            return;
        }

        if (ChatColor.stripColor(title).startsWith(ChatColor.stripColor(buyPrefix()))) {
            event.setCancelled(true);
            handleBuyGUI(event, player);
        }
    }

    // ---------------------------------------------------------------
    // MAIN SHOP
    // ---------------------------------------------------------------
    private void handleMainShop(InventoryClickEvent event, Player player) {
        playSound(player, "click");
        switch (event.getSlot()) {
            case 11 -> new CategoryShopGUI(plugin).open(player, "end");
            case 12 -> new CategoryShopGUI(plugin).open(player, "nether");
            case 13 -> new CategoryShopGUI(plugin).open(player, "gear");
            case 14 -> new CategoryShopGUI(plugin).open(player, "food");
            case 15 -> new ShardShopGUI(plugin).open(player);
            default -> {
            }
        }
    }

    // ---------------------------------------------------------------
    // CATEGORY SHOP (money)
    // ---------------------------------------------------------------
    private void handleCategoryShop(InventoryClickEvent event, Player player, String category) {
        playSound(player, "click");
        int slot = event.getSlot();
        int size = event.getInventory().getSize();
        int backSlot = CategoryShopGUI.backButtonSlot(size);

        if (slot == backSlot) {
            new MainShopGUI(plugin).open(player);
            return;
        }

        List<String> itemIds = CategoryShopGUI.getItemIds(plugin, category);
        int index = slot - 9;
        if (index < 0 || index >= itemIds.size()) return;
        String itemId = itemIds.get(index);

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("categories." + category + ".items." + itemId);
        if (section == null) return;

        Material material = Material.matchMaterial(section.getString("material", "STONE"));
        if (material == null) material = Material.STONE;
        double price = section.getDouble("price");
        int defaultQty = section.getInt("default-quantity", 1);

        PurchaseContext context = new PurchaseContext(category, itemId, material, null, price,
                CurrencyType.MONEY, false, null, material);
        new BuyGUI(plugin).open(player, context, defaultQty);
    }

    // ---------------------------------------------------------------
    // SHARD SHOP
    // ---------------------------------------------------------------
    private void handleShardShop(InventoryClickEvent event, Player player) {
        playSound(player, "click");
        int slot = event.getSlot();
        int size = event.getInventory().getSize();
        int backSlot = size - 5;

        if (slot == backSlot) {
            new MainShopGUI(plugin).open(player);
            return;
        }

        List<String> spawnerIds = ShardShopGUI.getSpawnerIds(plugin);
        int spawnerStart = 9;

        int spawnerIndex = slot - spawnerStart;
        if (spawnerIndex >= 0 && spawnerIndex < spawnerIds.size()) {
            openSpawnerBuyGui(player, spawnerIds.get(spawnerIndex));
        }
    }

    private void openSpawnerBuyGui(Player player, String spawnerId) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("shard-shop.spawners." + spawnerId);
        if (section == null) return;
        String entityTypeName = section.getString("entity-type", "PIG");
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(entityTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Loai mob khong hop le trong config: " + entityTypeName);
            return;
        }
        double price = section.getDouble("price");
        String displayName = "&b" + GuiUtil.properCase(entityTypeName) + " Spawner";

        PurchaseContext context = new PurchaseContext("shard", spawnerId, Material.SPAWNER, displayName, price,
                CurrencyType.SHARDS, true, entityType, Material.SPAWNER);
        new BuyGUI(plugin).open(player, context, 1);
    }

    // ---------------------------------------------------------------
    // BUY GUI (quantity + confirm) - dung chung cho ca MONEY va SHARDS
    // ---------------------------------------------------------------
    private void handleBuyGUI(InventoryClickEvent event, Player player) {
        PurchaseContext context = plugin.getShopManager().getPurchaseContext(player);
        if (context == null) return;

        int slot = event.getSlot();
        int currentQty = plugin.getShopManager().getQuantity(player);
        int maxStack = context.getDisplayMaterial().getMaxStackSize();
        if (maxStack <= 0) maxStack = 64;

        if (slot >= 9 && slot <= 17 && slot != 13 && slot != 12 && slot != 14) {
            int newQty = currentQty;
            switch (slot) {
                case 9 -> newQty = 1;
                case 10 -> newQty = currentQty - 10;
                case 11 -> newQty = currentQty - 1;
                case 15 -> newQty = currentQty + 1;
                case 16 -> newQty = currentQty + 10;
                case 17 -> newQty = maxStack;
                default -> {
                }
            }
            if (newQty != currentQty) {
                newQty = Math.max(1, Math.min(newQty, maxStack));
                playSound(player, "click");
                new BuyGUI(plugin).updateQuantity(player, newQty);
            }
            return;
        }

        if (slot == 23) {
            handlePurchase(player, context, currentQty);
        } else if (slot == 21) {
            playSound(player, "click");
            plugin.getShopManager().clear(player);
            if ("shard".equals(context.getCategory())) {
                new ShardShopGUI(plugin).open(player);
            } else {
                new CategoryShopGUI(plugin).open(player, context.getCategory());
            }
        }
    }

    private void handlePurchase(Player player, PurchaseContext context, int quantity) {
        double totalPrice = context.getPrice() * quantity;
        String niceName = context.getDisplayName() != null
                ? ChatColor.stripColor(GuiUtil.colorize(context.getDisplayName()))
                : GuiUtil.properCase(context.getItemId());

        if (context.getCurrency() == CurrencyType.MONEY) {
            EconomyManager eco = plugin.getEconomyManager();
            if (!eco.isAvailable()) {
                player.sendMessage(plugin.msg("missing-dependency").replace("%plugin%", "Vault"));
                playSound(player, "purchase-unsuccessful");
                return;
            }
            if (!eco.has(player, totalPrice)) {
                player.sendMessage(plugin.msg("not-enough-money"));
                playSound(player, "purchase-unsuccessful");
                return;
            }
            if (!hasInventorySpace(player, context.getGiveMaterial(), quantity)) {
                player.sendMessage(plugin.msg("inventory-full"));
                playSound(player, "purchase-unsuccessful");
                return;
            }

            eco.withdraw(player, totalPrice);
            player.getInventory().addItem(new ItemStack(context.getGiveMaterial(), quantity));

            player.sendMessage(plugin.msg("purchase-success-money")
                    .replace("%amount%", String.valueOf(quantity))
                    .replace("%item%", niceName)
                    .replace("%price%", formatNumber(totalPrice)));
            playSound(player, "purchase-successful");
        } else {
            ShardsManager shards = plugin.getShardsManager();
            if (!shards.isAvailable()) {
                player.sendMessage(plugin.msg("missing-dependency").replace("%plugin%", "XShards"));
                playSound(player, "purchase-unsuccessful");
                return;
            }

            boolean checkBalance = plugin.getConfig().getBoolean("integrations.xshards.check-balance-before-purchase", true);
            if (checkBalance && shards.isBalanceCheckAvailable()) {
                double balance = shards.getBalance(player);
                if (balance >= 0 && balance < totalPrice) {
                    player.sendMessage(plugin.msg("not-enough-shards").replace("%price%", formatNumber(totalPrice)));
                    playSound(player, "purchase-unsuccessful");
                    return;
                }
            }

            if (context.isSpawner()) {
                // SmartSpawner tu xu ly khi tui do day (roi vat pham xuong dat), nen khong can kiem tra truoc.
                shards.withdraw(player, totalPrice);
                plugin.getSpawnerManager().giveSpawner(player, context.getEntityType(), quantity);
            } else {
                if (!hasInventorySpace(player, context.getGiveMaterial(), quantity)) {
                    player.sendMessage(plugin.msg("inventory-full"));
                    playSound(player, "purchase-unsuccessful");
                    return;
                }
                shards.withdraw(player, totalPrice);
                String name = context.getDisplayName() != null ? context.getDisplayName() : niceName;
                ItemStack item = GuiUtil.item(context.getGiveMaterial(), name);
                item.setAmount(quantity);
                player.getInventory().addItem(item);
            }

            player.sendMessage(plugin.msg("purchase-success-shards")
                    .replace("%amount%", String.valueOf(quantity))
                    .replace("%item%", niceName)
                    .replace("%price%", formatNumber(totalPrice)));
            playSound(player, "purchase-successful");
        }

        // Giu nguyen man hinh Mua + giu nguyen so luong da chon, khong quay ve danh sach shop,
        // de nguoi choi bam "XAC NHAN MUA" lai la mua tiep ngay lap tuc (vd mua 64 end rod lien tuc).
        new BuyGUI(plugin).updateQuantity(player, quantity);
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------
    private boolean hasInventorySpace(Player player, Material material, int amount) {
        int maxStack = material.getMaxStackSize();
        if (maxStack <= 0) maxStack = 64;
        int remaining = amount;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                remaining -= maxStack;
            } else if (item.getType() == material) {
                remaining -= (maxStack - item.getAmount());
            }
            if (remaining <= 0) return true;
        }
        return remaining <= 0;
    }

    private void playSound(Player player, String soundType) {
        String soundName = plugin.getConfig().getString("sounds." + soundType, "");
        float volume = (float) plugin.getConfig().getDouble("sounds." + soundType + "-volume", 0.5);
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, 1.0f);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.format("%,d", (long) value);
        }
        return String.format("%,.2f", value);
    }

    private String mainTitle() {
        return GuiUtil.colorize(plugin.getConfig().getString("gui.main-shop-title", "Legendary Shop"));
    }

    private String categoryTitle(String category) {
        String prefix = plugin.getConfig().getString("gui.category-title-prefix", "Shop - ");
        String name = plugin.getConfig().getString("categories." + category + ".display-name", category);
        return GuiUtil.colorize(prefix + name);
    }

    private String shardTitle() {
        return GuiUtil.colorize(plugin.getConfig().getString("gui.shard-shop-title", "Shop - Shard"));
    }

    private String buyPrefix() {
        return GuiUtil.colorize(plugin.getConfig().getString("gui.buy-gui-title-prefix", "Mua - "));
    }
}
