package com.example.legendaryshop.managers;

import com.example.legendaryshop.model.PurchaseContext;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopManager {

    private final Map<UUID, Integer> quantities = new HashMap<>();
    private final Map<UUID, PurchaseContext> purchaseContexts = new HashMap<>();

    public int getQuantity(Player player) {
        return quantities.getOrDefault(player.getUniqueId(), 1);
    }

    public void setQuantity(Player player, int quantity) {
        quantities.put(player.getUniqueId(), quantity);
    }

    public void setPurchaseContext(Player player, PurchaseContext context) {
        purchaseContexts.put(player.getUniqueId(), context);
    }

    public PurchaseContext getPurchaseContext(Player player) {
        return purchaseContexts.get(player.getUniqueId());
    }

    public void clear(Player player) {
        quantities.remove(player.getUniqueId());
        purchaseContexts.remove(player.getUniqueId());
    }
}
