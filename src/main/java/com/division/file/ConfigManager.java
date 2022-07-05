package com.division.file;

import com.division.Gamble;
import com.division.data.DataManager;
import com.division.data.StockData;
import com.division.data.StockManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ConfigManager {

    private Gamble Plugin;
    private File file;
    private FileConfiguration config;
    private static ConfigManager instance;

    static {
        instance = new ConfigManager();
    }

    private ConfigManager() {
        this.Plugin = JavaPlugin.getPlugin(Gamble.class);
        this.file = new File("plugins/" + this.Plugin.getDescription().getName() + "/config.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    private void load() {
        if (!file.exists())
            Plugin.saveDefaultConfig();
        try {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.config.save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        load();
        DataManager manager = DataManager.getInstance();
        manager.setSlotMin(config.getInt("slot.min", 100));
        manager.setSlotCount(config.getDouble("slot.count", 1.0));
        manager.setDiceMin(config.getInt("dice.min", 500));
        manager.setDiceMax(config.getInt("dice.max", 5000));
        manager.setBlackjackMin(config.getInt("blackjack.min", 500));
        manager.setBlackjackMax(config.getInt("blackjack.max", 5000));
        manager.setRouletteMin(config.getInt("roulette.min", 500));
        manager.setRouletteMax(config.getInt("roulette.max", 5000));
        manager.setCoinMin(config.getInt("coin.min", 100));
        manager.setIndianMin(config.getInt("indian.min", 1000));
        manager.setIndianMax(config.getInt("indian.max", 4000));
        manager.setCardMin(config.getInt("card.min", 1000));
        manager.setCardMax(config.getInt("card.max", 4000));
        manager.setPokerMin(config.getInt("poker.min", 1000));
        manager.setPokerMax(config.getInt("poker.max", 4000));
        manager.getBlacklist().clear();
        config.getStringList("blacklist").forEach(value -> {
            try {
                manager.addBlackList(UUID.fromString(value));
            }
            catch (IllegalArgumentException e) {
                Plugin.getLogger().info("error add data to blacklist : " + value);
            }
        });
        StockManager.getInstance().setRefreshRate(config.getInt("stock-refresh", 1800));
        StockManager.getInstance().getStockMap().clear();
        if (config.getConfigurationSection("stock") != null) {
            for (String key : config.getConfigurationSection("stock").getKeys(false))
                StockManager.getInstance().addStock(key, config.getInt("stock." + key + ".initial", 500), config.getInt("stock." + key + ".width", 100), config.getInt("stock." + key + ".current", 500), config.getBoolean("stock." + key + ".warning", false));
        }
        StockManager.getInstance().startTask();
    }

    public void saveData() {
        DataManager manager = DataManager.getInstance();
        ArrayList<String> arr = new ArrayList<>();
        config.set("slot.min", manager.getSlotMin());
        config.set("slot.count", manager.getSlotCount());
        config.set("dice.min", manager.getDiceMin());
        config.set("dice.max", manager.getDiceMax());
        config.set("blackjack.min", manager.getBlackjackMin());
        config.set("blackjack.max", manager.getBlackjackMax());
        config.set("roulette.min", manager.getRouletteMin());
        config.set("roulette.max", manager.getRouletteMax());
        config.set("coin.min", manager.getCoinMin());
        config.set("indian.min", manager.getIndianMin());
        config.set("indian.max", manager.getIndianMax());
        config.set("card.min", manager.getCardMin());
        config.set("card.max", manager.getCardMax());
        config.set("poker.min", manager.getPokerMin());
        config.set("poker.max", manager.getPokerMax());
        if (manager.getBlacklist().size() != 0) {
            manager.getBlacklist().forEach(data -> arr.add(data.toString()));
            config.set("blacklist", arr);
        }
        else
            config.set("blacklist", new ArrayList<>());
        config.set("stock-refresh", StockManager.getInstance().getRefreshRate());
        config.set("stock", null);
        for (String key : StockManager.getInstance().getStockMap().keySet()) {
            StockData data = StockManager.getInstance().getStock(key);
            config.set("stock." + key + ".initial", data.getInitial());
            config.set("stock." + key + ".width", data.getWidth());
            config.set("stock." + key + ".current", data.getCurrent());
            config.set("stock." + key + ".warning", data.isWarning());
        }
        save();
    }

}

