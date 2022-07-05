package com.division.data;

import com.division.Gamble;
import com.division.game.gambles.Stock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StockManager {

    private static StockManager instance;
    private HashMap<String, StockData> stockMap;
    private HashMap<UUID, UserStock> userStockData;
    private int refresh; //갱신 주기 (초단위)
    private int taskID;
    private final String header;
    private long lastRenewal;

    static {
        instance = new StockManager();
    }

    private StockManager() {
        stockMap = new HashMap<>();
        refresh = 600;
        taskID = 0;
        header = DataManager.getInstance().getHeader();
        lastRenewal = System.currentTimeMillis();
        userStockData = new HashMap<>();
    }

    public static StockManager getInstance() {
        return instance;
    }

    public boolean isExist(String key) {
        return stockMap.containsKey(key);
    }

    public void addStock(String name, int initial, int width) {
        stockMap.put(name, new StockData(initial, width, initial, false));
    }

    public void addStock(String name, int initial, int width, int current, boolean warning) {
        stockMap.put(name, new StockData(initial, width, current, warning));
    }

    public StockData getStock(String key) {
        return stockMap.get(key);
    }

    public void removeStock(String key) {
        stockMap.remove(key);
    }

    public HashMap<String, StockData> getStockMap() {
        return stockMap;
    }

    public int getRefreshRate() {
        return refresh;
    }

    public void setRefreshRate(int refreshTime) {
        this.refresh = refreshTime;
    }

    public long getLastRenewal() {
        return lastRenewal;
    }

    public void setLastRenewal(long lastRenewal) {
        this.lastRenewal = lastRenewal;
    }

    public UserStock getUser(UUID value) {
        if (!userStockData.containsKey(value))
            userStockData.put(value, new UserStock());
        return userStockData.get(value);
    }

    public HashMap<UUID, UserStock> getUserStockData() {
        return userStockData;
    }

    public void startTask() {
        Bukkit.getScheduler().cancelTask(taskID);
        taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(JavaPlugin.getPlugin(Gamble.class), () -> {
            //주식 갱신 시작, width는 100이상, 첫째자리 수는 소수점
            Bukkit.getOnlinePlayers().forEach(data -> data.sendMessage(header + "§f주식이 갱신되었습니다."));
            ArrayList<String> removeStock = new ArrayList<>();
            for (String stock : stockMap.keySet()) {
                StockData data = getStock(stock);
                int rand = ThreadLocalRandom.current().nextInt(0, data.getWidth() + 1) - data.getWidth() / 2; //-면 감소, +면 증가 (100 -> 48 = -0.2%)
                double next = Math.round((data.getCurrent() + data.getCurrent() * rand / 1000.0) * 10) / 10.0;
                if (next <= data.getInitial() * 0.01)
                    removeStock.add(stock);
                else
                    data.setWarning(next <= data.getInitial() * 0.05);
                data.setPercent(rand / 10.0);
                data.setCurrent(next);
            }
            for (String remove : removeStock) {
                Bukkit.broadcastMessage(header + "§c" + remove + "§f주가 §4상장폐지 §f되었습니다.");
                removeStock(remove);
            }
            setLastRenewal(System.currentTimeMillis());
            Bukkit.getOnlinePlayers().stream().filter(data -> data.getOpenInventory().getTitle().contains("주식")).forEach(data -> {
                if (GameData.getInstance().isPlaying(data.getUniqueId()) && GameData.getInstance().getData(data.getUniqueId()).getCurrent() instanceof Stock)
                    ((Stock) GameData.getInstance().getData(data.getUniqueId()).getCurrent()).refreshGUI();
            });
        }, 0L, refresh * 20L).getTaskId();
    }

}
