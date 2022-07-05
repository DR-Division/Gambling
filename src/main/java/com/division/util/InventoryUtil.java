package com.division.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class InventoryUtil {

    public static Inventory createInventory(String name, int size){
        return Bukkit.createInventory(null, size, name);
    }

    public static ItemStack createItemStack(Material data, String name, String... lore){
        ItemStack stack = new ItemStack(data);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(createLore(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createItemStack(Material data, String name, short color, String... lore){
        ItemStack stack = new ItemStack(data);
        ItemMeta meta = stack.getItemMeta();
        stack.setDurability(color);
        meta.setDisplayName(name);
        meta.setLore(createLore(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createSkullStack(String name, Player target, String... lore){
        OfflinePlayer p = Bukkit.getOfflinePlayer(target.getUniqueId());
        ItemStack stack = new ItemStack(Material.SKULL_ITEM,1, (short)3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(createLore(lore));
        meta.setOwningPlayer(p);
        stack.setItemMeta(meta);
        return stack;
    }

    public static void setLore(ItemStack stack, String... lore){
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(createLore(lore));
        stack.setItemMeta(meta);
    }

    public static void setDisplayName(ItemStack stack, String name){
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
    }

    public static void clearGUI(Player p){
        for (int i = 0; i < p.getOpenInventory().getTopInventory().getSize(); i++)
            p.getOpenInventory().setItem(i, new ItemStack(Material.AIR));
    }

    public static List<String> createLore(String[] arr){
        if (arr == null)
            return null;
        else
            return Arrays.asList(arr);
    }
}
