package com.division.file;

import com.division.Gamble;
import com.division.data.StockData;
import com.division.data.StockManager;
import com.division.data.UserStock;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class StockFileManager {

    private static StockFileManager instance;
    private Gamble Plugin;
    private File file;
    private YamlConfiguration config;

    static {
        instance = new StockFileManager();
    }

    private StockFileManager() {
        Plugin = JavaPlugin.getPlugin(Gamble.class);
        File folder = new File("plugins/" + Plugin.getDescription().getName() + "/stock");
        if (!folder.exists())
            folder.mkdir();
        file = new File("plugins/" + Plugin.getDescription().getName() + "/stock/data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static StockFileManager getInstance() {
        return instance;
    }

    public void load() {
        try {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if (config.getKeys(false) != null) {
            for (String key : config.getKeys(false)) {
                UUID keyValue = UUID.fromString(key);
                for (String innerKey : config.getConfigurationSection(key).getKeys(false)) {
                    if (StockManager.getInstance().isExist(innerKey)) {
                        UserStock data = StockManager.getInstance().getUser(keyValue);
                        data.buyStock(innerKey, config.getInt(key + "." + innerKey + ".amount", 1), config.getDouble(key + "." + innerKey + ".accumulate", 500.0));
                    }
                }
            }
        }
    }

    public void save() {
        if (config.getKeys(false) != null) {
            for (String val : config.getKeys(false))
                config.set(val, null);
        }
        for (UUID key : StockManager.getInstance().getUserStockData().keySet()) {
            UserStock data = StockManager.getInstance().getUser(key);
            for (String stock : StockManager.getInstance().getStockMap().keySet()) {
                if (data.hasStock(stock)) {
                    config.set(key.toString() + "." + stock + ".amount", data.getAmount(stock));
                    config.set(key.toString() + "." + stock + ".accumulate", data.getAccumulate(stock));
                }
            }
        }
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
