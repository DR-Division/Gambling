package com.division;

import com.division.command.GambleCommand;
import com.division.file.ConfigManager;
import com.division.file.GambleLogger;
import com.division.file.StockFileManager;
import com.division.listener.InventoryClickListener;
import com.division.listener.InventoryCloseListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Gamble extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Gamble Enabled");
        getCommand("도박").setExecutor(new GambleCommand(this));
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);
        ConfigManager.getInstance().loadData();
        GambleLogger.getInstance().logging();
        StockFileManager.getInstance().load();
    }

    @Override
    public void onDisable() {
        getLogger().info("Gamble Disabled");
        ConfigManager.getInstance().saveData();
        GambleLogger.getInstance().forceSaveLog();
        StockFileManager.getInstance().save();
    }


}
