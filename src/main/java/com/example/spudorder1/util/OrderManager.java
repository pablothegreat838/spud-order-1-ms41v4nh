package com.example.spudorder1.util;

import com.example.spudorder1.SpudOrder1;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderManager {

    private final SpudOrder1 plugin;
    private final List<Order> orders;
    private final Map<UUID, Integer> playerOpenGUIs; // Player UUID -> current page

    private final ItemStack previousPageItem;
    private final ItemStack nextPageItem;

    public OrderManager(SpudOrder1 plugin) {
        this.plugin = plugin;
        this.orders = new ArrayList<>();
        this.playerOpenGUIs = new HashMap<>();

        // Initialize navigation items
        previousPageItem = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = previousPageItem.getItemMeta();
        prevMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
        previousPageItem.setItemMeta(prevMeta);

        nextPageItem = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPageItem.getItemMeta();
        nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
        nextPageItem.setItemMeta(nextMeta);
    }

    public boolean addOrder(Order order) {
        if (orders.size() >= 6 * 45) { // Max 6 pages * 45 slots per page
            return false;
        }
        orders.add(order);
        return true;
    }

    public void removeOrder(int index) {
        if (index >= 0 && index < orders.size()) {
            orders.remove(index);
        }
    }

    public Order getOrderAtSlot(int index) {
        if (index >= 0 && index < orders.size()) {
            return orders.get(index);
        }
        return null;
    }

    public void openOrderGUI(Player player, int page) {
        int totalPages = (int) Math.ceil((double) orders.size() / 45);
        if (totalPages == 0) totalPages = 1; // At least one page even if empty

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "ORDERS (Page " + (page + 1) + "/" + totalPages + ")");

        // Fill order items
        int startIndex = page * 45;
        for (int i = 0; i < 45; i++) {
            int orderIndex = startIndex + i;
            if (orderIndex < orders.size()) {
                Order order = orders.get(orderIndex);
                ItemStack displayItem = order.getItemStack().clone();
                ItemMeta meta = displayItem.getItemMeta();
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(ChatColor.GRAY + "Price: " + ChatColor.GOLD + "$" + String.format("%.2f", order.getPrice()));
                lore.add(ChatColor.GRAY + "Seller: " + ChatColor.YELLOW + Bukkit.getOfflinePlayer(order.getSellerUUID()).getName());
                lore.add("");
                lore.add(ChatColor.GREEN + "Click to buy!");
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
                gui.setItem(i, displayItem);
            }
        }

        // Add navigation buttons to the bottom row
        for (int i = 45; i < 54; i++) {
            gui.setItem(i, createFillerItem());
        }

        if (page > 0) {
            gui.setItem(45, previousPageItem);
        }
        if (page < totalPages - 1) {
            gui.setItem(53, nextPageItem);
        }

        player.openInventory(gui);
        playerOpenGUIs.put(player.getUniqueId(), page);
    }

    public void updateOrderGUI() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (playerOpenGUIs.containsKey(onlinePlayer.getUniqueId())) {
                openOrderGUI(onlinePlayer, playerOpenGUIs.get(onlinePlayer.getUniqueId()));
            }
        }
    }

    public int getCurrentPage(Player player) {
        return playerOpenGUIs.getOrDefault(player.getUniqueId(), -1);
    }

    public ItemStack getPreviousPageItem() {
        return previousPageItem;
    }

    public ItemStack getNextPageItem() {
        return nextPageItem;
    }

    private ItemStack createFillerItem() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7); // Gray stained glass pane
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
}