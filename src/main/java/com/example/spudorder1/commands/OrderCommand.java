package com.example.spudorder1.commands;

import com.example.spudorder1.util.Order;
import com.example.spudorder1.util.OrderManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OrderCommand implements CommandExecutor {

    private final OrderManager orderManager;

    public OrderCommand(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create orders.");
            return true;
        }

        if (!sender.hasPermission("spudorder1.order")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to create orders.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem == null || heldItem.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You must be holding an item to create an order.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /order <price>");
            return true;
        }

        double price;
        try {
            price = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Price must be a number.");
            return true;
        }

        if (price < 30 || price > 1000000) {
            player.sendMessage(ChatColor.RED + "Price must be between 30 and 1,000,000.");
            return true;
        }

        Order newOrder = new Order(player.getUniqueId(), heldItem.clone(), price);

        if (orderManager.addOrder(newOrder)) {
            player.getInventory().setItemInMainHand(null); // Remove item from player's hand
            player.sendMessage(ChatColor.GREEN + "Your order for " + heldItem.getAmount() + " " + heldItem.getType().name() + " at $" + price + " each has been created!");
            orderManager.updateOrderGUI(); // Update GUI for all viewers
        } else {
            player.sendMessage(ChatColor.RED + "Could not create order. The orders list might be full.");
        }

        return true;
    }
}