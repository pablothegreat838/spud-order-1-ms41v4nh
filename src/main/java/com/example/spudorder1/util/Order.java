package com.example.spudorder1.util;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Order {
    private final UUID sellerUUID;
    private final ItemStack itemStack;
    private final double price;

    public Order(UUID sellerUUID, ItemStack itemStack, double price) {
        this.sellerUUID = sellerUUID;
        this.itemStack = itemStack;
        this.price = price;
    }

    public UUID getSellerUUID() {
        return sellerUUID;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getPrice() {
        return price;
    }
}