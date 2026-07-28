package com.example.spudorder1.listeners;

import com.example.spudorder1.SpudOrder1;
import com.example.spudorder1.util.Order;
import com.example.spudorder1.util.OrderManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OrderGUIListener implements Listener {

    private final OrderManager orderManager;

    public OrderGUIListener(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith(ChatColor.DARK_GRAY + "ORDERS (Page ")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        int currentPage = orderManager.getCurrentPage(player);
        if (currentPage == -1) { // Player not in GUI map, likely opened it before reload or error
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Error: Your orders session expired. Please reopen the GUI.");
            return;
        }

        // Handle navigation buttons
        if (clickedItem.isSimilar(orderManager.getPreviousPageItem())) {
            orderManager.openOrderGUI(player, currentPage - 1);
            return;
        }
        if (clickedItem.isSimilar(orderManager.getNextPageItem())) {
            orderManager.openOrderGUI(player, currentPage + 1);
            return;
        }

        // Handle order clicks
        int slot = event.getSlot();
        if (slot >= 0 && slot < 45) { // Only clickable slots for orders
            int orderIndex = (currentPage * 45) + slot;
            Order order = orderManager.getOrderAtSlot(orderIndex);

            if (order != null) {
                if (player.getUniqueId().equals(order.getSellerUUID())) {
                    player.sendMessage(ChatColor.RED + "You cannot buy your own order!");
                    return;
                }

                Economy econ = SpudOrder1.getEconomy();
                if (econ == null) {
                    player.sendMessage(ChatColor.RED + "Economy is not available. Cannot complete purchase.");
                    return;
                }

                if (econ.getBalance(player) < order.getPrice()) {
                    player.sendMessage(ChatColor.RED + "You do not have enough money to buy this item!");
                    return;
                }

                ItemStack itemToGive = order.getItemStack();
                if (itemToGive == null) {
                    player.sendMessage(ChatColor.RED + "This order's item is no longer valid. It has been removed.");
                    orderManager.removeOrder(orderIndex);
                    orderManager.updateOrderGUI();
                    return;
                }

                if (player.getInventory().addItem(itemToGive).isEmpty()) {
                    econ.withdrawPlayer(player, order.getPrice());
                    econ.depositPlayer(order.getSellerUUID(), order.getPrice());
                    player.sendMessage(ChatColor.GREEN + "You have purchased " + itemToGive.getAmount() + " " + itemToGive.getType().name() + " for $" + order.getPrice() + "!");
                    orderManager.removeOrder(orderIndex);
                    orderManager.updateOrderGUI(); // Update GUI for all viewers
                } else {
                    player.sendMessage(ChatColor.RED + "Your inventory is full. Please clear some space.");
                }
            }
        }
    }
}