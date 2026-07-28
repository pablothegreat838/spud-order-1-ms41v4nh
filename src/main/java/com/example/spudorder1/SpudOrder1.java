package com.example.spudorder1;

import com.example.spudorder1.commands.GiveOrderCommand;
import com.example.spudorder1.commands.OrderCommand;
import com.example.spudorder1.commands.OrdersCommand;
import com.example.spudorder1.listeners.OrderGUIListener;
import com.example.spudorder1.util.OrderManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpudOrder1 extends JavaPlugin {

    private static Economy econ = null;
    private OrderManager orderManager;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        orderManager = new OrderManager(this);

        getCommand("order").setExecutor(new OrderCommand(orderManager));
        getCommand("orders").setExecutor(new OrdersCommand(orderManager));
        getCommand("giveorder").setExecutor(new GiveOrderCommand(orderManager));

        getServer().getPluginManager().registerEvents(new OrderGUIListener(orderManager), this);

        getLogger().info("Spud-order-1 has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Spud-order-1 has been disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }
}