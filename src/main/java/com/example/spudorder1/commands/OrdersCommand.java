package com.example.spudorder1.commands;

import com.example.spudorder1.util.OrderManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrdersCommand implements CommandExecutor {

    private final OrderManager orderManager;

    public OrdersCommand(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can view orders.");
            return true;
        }

        if (!sender.hasPermission("spudorder1.vieworders")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to view orders.");
            return true;
        }

        Player player = (Player) sender;
        orderManager.openOrderGUI(player, 0); // Open the first page

        return true;
    }
}