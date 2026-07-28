package com.example.spudorder1.commands;

import com.example.spudorder1.util.Order;
import com.example.spudorder1.util.OrderManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveOrderCommand implements CommandExecutor {

    private final OrderManager orderManager;

    public GiveOrderCommand(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!sender.hasPermission("spudorder1.giveorder")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to give orders.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /giveorder <row> <slot>");
            return true;
        }

        int row;
        int slot;
        try {
            row = Integer.parseInt(args[0]);
            slot = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Row and slot must be numbers.");
            return true;
        }

        if (row < 1 || row > 6) {
            sender.sendMessage(ChatColor.RED + "Row must be between 1 and 6.");
            return true;
        }
        if (slot < 1 || slot > 9) {
            sender.sendMessage(ChatColor.RED + "Slot must be between 1 and 9.");
            return true;
        }

        Player player = (Player) sender;
        int inventorySlot = (row - 1) * 9 + (slot - 1);

        Order order = orderManager.getOrderAtSlot(inventorySlot);

        if (order == null) {
            player.sendMessage(ChatColor.RED + "No order found at that position.");
            return true;
        }

        ItemStack itemToGive = order.getItemStack();
        if (itemToGive == null) {
            player.sendMessage(ChatColor.RED + "This order's item is no longer valid.");
            orderManager.removeOrder(inventorySlot); // Clean up invalid order
            return true;
        }

        if (player.getInventory().addItem(itemToGive).isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "You have received the item from the order!");
            orderManager.removeOrder(inventorySlot);
            orderManager.updateOrderGUI(); // Update GUI for all viewers
        } else {
            player.sendMessage(ChatColor.RED + "Your inventory is full. Please clear some space.");
        }

        return true;
    }
}