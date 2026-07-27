package com.example.legendaryshop.model;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class PurchaseContext {

    private final String category;      // "end", "nether", "gear", "food", "shard"
    private final String itemId;        // key trong config, vd "enderchest", "pig-spawner"
    private final Material displayMaterial; // icon hien thi trong GUI (vd SPAWNER cho spawner)
    private final String displayName;   // ten hien thi (co the null -> dung ten material)
    private final double price;         // gia cho 1 don vi
    private final CurrencyType currency;
    private final boolean isSpawner;
    private final EntityType entityType; // chi dung neu isSpawner = true
    private final Material giveMaterial; // material thuc te se dua cho nguoi choi (neu khong phai spawner)

    public PurchaseContext(String category, String itemId, Material displayMaterial, String displayName,
                            double price, CurrencyType currency, boolean isSpawner,
                            EntityType entityType, Material giveMaterial) {
        this.category = category;
        this.itemId = itemId;
        this.displayMaterial = displayMaterial;
        this.displayName = displayName;
        this.price = price;
        this.currency = currency;
        this.isSpawner = isSpawner;
        this.entityType = entityType;
        this.giveMaterial = giveMaterial;
    }

    public String getCategory() {
        return category;
    }

    public String getItemId() {
        return itemId;
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getPrice() {
        return price;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public boolean isSpawner() {
        return isSpawner;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Material getGiveMaterial() {
        return giveMaterial;
    }
}
