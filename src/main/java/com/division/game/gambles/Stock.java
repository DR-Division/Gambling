package com.division.game.gambles;

import com.division.data.DataManager;
import com.division.data.GameData;
import com.division.data.StockManager;
import com.division.data.UserStock;
import com.division.game.Game;
import com.division.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Stock implements Game {

    private UUID target;
    private String header;
    private SimpleDateFormat format;

    public Stock(UUID data) {
        header = DataManager.getInstance().getHeader();
        this.target = data;
        GameData.getInstance().playGame(data, this, -5);
        format = new SimpleDateFormat("kk:mm:ss");
        setGUI();
    }

    @Override
    public void setGUI() {
        Player p = Bukkit.getPlayer(target);
        if (p != null) {
            Inventory inv = InventoryUtil.createInventory(header + "§0주식", 54);
            p.openInventory(inv);
            refreshGUI();
        }
    }

    @Override
    public void play() {
        //not use
    }

    public void refreshGUI() {
        int i = 0;
        Player p = Bukkit.getPlayer(target);
        InventoryUtil.clearGUI(p);
        StockManager manager = StockManager.getInstance();
        long nextTime = manager.getLastRenewal() + manager.getRefreshRate() * 1000L;
        UserStock data = manager.getUser(target);
        ItemStack paper = InventoryUtil.createItemStack(Material.PAPER, header + "§8" + p.getName() + "§f님의 정보", " ", " §8-  §f다음 갱신 시간 §7: §f" + format.format(new Date(nextTime)), " §8-  §f총매수 §7: §6" + getStockAccumulate() + "§f원", " §8-  §f총평가 §7: §c" + getMarketPrice() + "§f원", " §8-  §f평가 손익 §7: " + getResult(), " §8-  §f수익률 §7: " + getPercent(), " ");
        p.getOpenInventory().setItem(49, paper);
        for (String stock : manager.getStockMap().keySet()) {
            ItemStack stack;
            if (data.hasStock(stock)) {
                if (StockManager.getInstance().getStock(stock).isWarning())
                    stack = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§5" + stock, " ", " §8-  §b현재 보유중인 주식입니다.", " §8-  §f보유랑 §7: §6" + data.getAmount(stock) + "§f주", " §8-  §f상장가 §7: §b" + manager.getStock(stock).getInitial() + "§f원", " §8-  §f현재가 §7: §6" + manager.getStock(stock).getCurrent() + "§f원", " §8-  §f등락 §7: §f" + getPercent(manager.getStock(stock).getPercent()), " §8-  §f평균가 §7: §c" + Math.round(data.getAccumulate(stock) / data.getAmount(stock) * 10) / 10.0 + "§f원", " §8-  §f평가금액 §7: " + Math.round(manager.getStock(stock).getCurrent() * data.getAmount(stock) * 10) / 10.0 + "§f원", " §8-  §f평가손익 §7: " + getResult(data.getAccumulate(stock), Math.round(manager.getStock(stock).getCurrent() * data.getAmount(stock) * 10) / 10.0), " §8-  §f수익률 §7: " + getPercent(data.getAccumulate(stock), data.getAmount(stock) * manager.getStock(stock).getCurrent()), " §8-  §f우클릭시 1주 구매, 쉬프트 우클릭시 10주 구매", " §8-  §f좌클릭시 1주 판매, 쉬프트 좌클릭시 전체 판매", " ");
                else
                    stack = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§f" + stock, " ", " §8-  §b현재 보유중인 주식입니다.", " §8-  §f보유랑 §7: §6" + data.getAmount(stock) + "§f주", " §8-  §f상장가 §7: §b" + manager.getStock(stock).getInitial() + "§f원", " §8-  §f현재가 §7: §6" + manager.getStock(stock).getCurrent() + "§f원", " §8-  §f등락 §7: §f" + getPercent(manager.getStock(stock).getPercent()), " §8-  §f평균가 §7: §c" + Math.round(data.getAccumulate(stock) / data.getAmount(stock) * 10) / 10.0 + "§f원", " §8-  §f평가금액 §7: " + Math.round(manager.getStock(stock).getCurrent() * data.getAmount(stock) * 10) / 10.0 + "§f원", " §8-  §f평가손익 §7: " + getResult(data.getAccumulate(stock), Math.round(manager.getStock(stock).getCurrent() * data.getAmount(stock) * 10) / 10.0), " §8-  §f수익률 §7: " + getPercent(data.getAccumulate(stock), data.getAmount(stock) * manager.getStock(stock).getCurrent()), " §8-  §f우클릭시 1주 구매, 쉬프트 우클릭시 10주 구매", " §8-  §f좌클릭시 1주 판매, 쉬프트 좌클릭시 전체 판매", " ");

            }
            else {
                if (StockManager.getInstance().getStock(stock).isWarning())
                    stack = InventoryUtil.createItemStack(Material.BOOK, header + "§5" + stock, " ", " §8-  §c현재 보유하고 있지 않는 주식입니다.", " §8-  §f상장가 §7: §b" + manager.getStock(stock).getInitial() + "§f원", " §8-  §f현재가 §7: §6" + manager.getStock(stock).getCurrent() + "§f원", " §8-  §f등락 §7: §f" + getPercent(StockManager.getInstance().getStock(stock).getPercent()), " §8-  §f우클릭시 1주 구매, 쉬프트 우클릭시 10주 구매", " §8-  §f좌클릭시 1주 판매, 쉬프트 좌클릭시 전체 판매", " ");
                else
                    stack = InventoryUtil.createItemStack(Material.BOOK, header + "§f" + stock, " ", " §8-  §c현재 보유하고 있지 않는 주식입니다.", " §8-  §f상장가 §7: §b" + manager.getStock(stock).getInitial() + "§f원", " §8-  §f현재가 §7: §6" + manager.getStock(stock).getCurrent() + "§f원", " §8-  §f등락 §7: §f" + getPercent(StockManager.getInstance().getStock(stock).getPercent()), " §8-  §f우클릭시 1주 구매, 쉬프트 우클릭시 10주 구매", " §8-  §f좌클릭시 1주 판매, 쉬프트 좌클릭시 전체 판매", " ");
            }
            p.getOpenInventory().setItem(i++, stack);
        }
    }

    private double getStockAccumulate() {
        double result = 0.0;
        for (String stock : StockManager.getInstance().getStockMap().keySet()) {
            UserStock data = StockManager.getInstance().getUser(target);
            if (data.hasStock(stock))
                result += data.getAccumulate(stock);
        }
        return Math.round(result * 10) / 10.0;
    }

    private double getMarketPrice() {
        double result = 0.0;
        for (String stock : StockManager.getInstance().getStockMap().keySet()) {
            UserStock data = StockManager.getInstance().getUser(target);
            if (data.hasStock(stock))
                result += data.getAmount(stock) * StockManager.getInstance().getStock(stock).getCurrent();
        }
        return Math.round(result * 10) / 10.0;
    }

    private String getResult() {
        double stock = getStockAccumulate();
        double market = getMarketPrice();
        if (stock == market)
            return "§70.0";
        else if (stock > market)
            return "§b-" + Math.round((stock - market) * 10) / 10.0;
        else
            return "§c+" + Math.round(Math.abs(stock - market) * 10.0) / 10.0;
    }

    private String getResult(double stock, double market) {
        if (stock == market)
            return "§70.0";
        else if (stock > market)
            return "§b-" + Math.round(stock - market);
        else
            return "§c+" + Math.round(Math.abs(stock - market));
    }

    private String getPercent() {
        double have = getStockAccumulate() == 0 ? 1 : getStockAccumulate();
        double market = getMarketPrice();
        int result = (int) Math.round(Math.abs(market - have) / have * 100);
        if (have == market || have == 1)
            return "§7- 0.0%";
        else if (have < market)
            return "§c▲ +" + result + "%";
        else
            return "§b▼ -" + result + "%";

    }

    private String getPercent(double percent) {
        if (percent == 0.0)
            return "§7- 0.0%";
        else if (percent < 0.0)
            return "§b▼ " + percent + "%";
        else
            return "§c▲ +" + percent + "%";
    }

    private String getPercent(double have, double market) {
        if (have == 0)
            have = 1;
        int result = (int) Math.round(Math.abs(market - have) / have * 100);
        if (have < market)
            return "§c▲ +" + result + "%";
        else if (have == market || have == 1)
            return "§7- 0.0%";
        else
            return "§b▼ -" + result + "%";

    }
}
