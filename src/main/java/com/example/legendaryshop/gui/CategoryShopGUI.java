package com.example.legendaryshop.gui;

import com.example.legendaryshop.LegendaryShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CategoryShopGUI {

    private final LegendaryShop plugin;

    public CategoryShopGUI(LegendaryShop plugin) {
        this.plugin = plugin;
    }

    /** Danh sach id vat pham theo dung thu tu trong config (dung de map slot -> item o listener). */
    public static List<String> getItemIds(LegendaryShop plugin, String category) {
        List<String> ids = new ArrayList<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("categories." + category + ".items");
        if (section != null) {
            ids.addAll(section.getKeys(false));
        }
        return ids;
    }

    public static int computeSize(int itemCount) {
        int rowsForItems = Math.max(1, (int) Math.ceil(itemCount / 9.0));
        int size = (rowsForItems + 2) * 9;
        return Math.min(size, 54);
    }

    public static int backButtonSlot(int size) {
        return size - 5;
    }

    public void open(Player player, String category) {
        List<String> itemIds = getItemIds(plugin, category);
        int size = computeSize(itemIds.size());

        String prefix = plugin.getConfig().getString("gui.category-title-prefix", "Shop - ");
        String catName = plugin.getConfig().getString("categories." + category + ".display-name", category);
        Inventory inv = Bukkit.createInventory(null, size, GuiUtil.colorize(prefix + catName));

        ItemStack filler = GuiUtil.filler(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }

        int slot = 9;
        for (String id : itemIds) {
            ConfigurationSection itemSection = plugin.getConfig().getConfigurationSection("categories." + category + ".items." + id);
            if (itemSection == null) {
                slot++;
                continue;
            }
            Material material = Material.matchMaterial(itemSection.getString("material", "STONE"));
            if (material == null) material = Material.STONE;
            double price = itemSection.getDouble("price");
            String niceName = GuiUtil.properCase(id);

            inv.setItem(slot, GuiUtil.item(material,
                    "&f" + niceName,
                    "&7Gia: &a$" + formatPrice(price),
                    "",
                    "&eNhan de mua"));
            slot++;
        }

        int backSlot = backButtonSlot(size);
        inv.setItem(backSlot, GuiUtil.item(Material.ARROW, "&cQuay lai", "&7Tro ve shop chinh"));

        player.openInventory(inv);
    }

    private String formatPrice(double price) {
        if (price == Math.floor(price)) {
            return String.valueOf((long) price);
        }
        return String.valueOf(price);
    }
}
