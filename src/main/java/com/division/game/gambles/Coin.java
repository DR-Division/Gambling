package com.division.game.gambles;

import com.division.Gamble;
import com.division.data.DataManager;
import com.division.data.GameData;
import com.division.file.GambleLogger;
import com.division.game.Game;
import com.division.util.EconomyAPI;
import com.division.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Coin implements Game {

    private UUID target;
    private String header;
    private Gamble Plugin;

    public enum Face {
        FRONT,
        BACK
    }

    public Coin(UUID target, Gamble Plugin) {
        this.target = target;
        header = DataManager.getInstance().getHeader();
        this.Plugin = Plugin;
        GameData.getInstance().playGame(target, this, -5);
        setGUI();
    }

    @Override
    public void setGUI() {
        Player p = Bukkit.getPlayer(target);
        Inventory coin = InventoryUtil.createInventory(header + "§0동전", 27);
        ItemStack edge = InventoryUtil.createItemStack(Material.IRON_FENCE, " ");
        ItemStack desc = InventoryUtil.createItemStack(Material.PAPER, header + "§6설명", " ", " §8-  §f동전의 앞면과 뒷면을 예측하는 도박입니다.", " §8-  §f1회당 §6" + DataManager.getInstance().getCoinMin() + "§f원이 소모되며, 앞/뒷면을 선택할시 차감됩니다", " §8-  §f앞면 혹은 뒷면을 선택할 시 결과가 공개되며, 승리시 §6" + DataManager.getInstance().getCoinMin() * 1.5 + "§f원을 흭득합니다.", " ");
        ItemStack front = InventoryUtil.createItemStack(Material.RECORD_3, header + "§c앞면", " ", " §8-  §f클릭시 앞면을 예측합니다.", " ");
        ItemStack back = InventoryUtil.createItemStack(Material.RECORD_3, header + "§b뒷면", " ", " §8-  §f클릭시 뒷면을 예측합니다.", " ");
        for (int i = 0; i < 9; i++) {
            coin.setItem(i, edge);
            coin.setItem(i + 18, edge);
        }
        coin.setItem(9, edge);
        coin.setItem(11, front);
        coin.setItem(13, desc);
        coin.setItem(15, back);
        coin.setItem(17, edge);
        p.openInventory(coin);
    }

    @Override
    public void play() {
        //NOT USE
    }

    public void check(Face face) {
        Player p = Bukkit.getPlayer(target);
        if (EconomyAPI.getInstance().getMoney(p) < DataManager.getInstance().getCoinMin())
            p.sendMessage(header + "§c금액이 부족합니다.." + " §7( §6" + DataManager.getInstance().getCoinMin() + "§f원 " + "§7)");
        else {
            ItemStack result;
            int value = ThreadLocalRandom.current().nextInt(1, 3); //1~2
            ItemStack help = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§c결과가 공개 되었습니다!");
            GameData.getInstance().getData(target).setBetMoney(DataManager.getInstance().getCoinMin());
            EconomyAPI.getInstance().steelMoney(p, DataManager.getInstance().getCoinMin());
            p.getOpenInventory().setItem(13, help);
            GambleLogger.getInstance().addLog(p.getName() + "님이 동전 도박 1회 시행");
            if (value == 1) {
                result = InventoryUtil.createItemStack(Material.DIAMOND, header + "§c앞면이 나왔습니다!");
                p.getOpenInventory().setItem(15, new ItemStack(Material.AIR));
                p.getOpenInventory().setItem(11, result);
            }
            else {
                result = InventoryUtil.createItemStack(Material.DIAMOND, header + "§b뒷면이 나왔습니다!");
                p.getOpenInventory().setItem(11, new ItemStack(Material.AIR));
                p.getOpenInventory().setItem(15, result);
            }

            if ((value == 1 && face == Face.FRONT) || (value == 2 && face == Face.BACK))
                win();
            else
                lose();

        }
    }

    public boolean isPlaying() {
        return GameData.getInstance().getData(target).getBetMoney() != -5;
    }

    public void win() {
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            Player p = Bukkit.getPlayer(target);
            if (GameData.getInstance().isPlaying(target)) {
                p.sendTitle(header, "§f베팅에 성공하여 §6" + DataManager.getInstance().getCoinMin() * 1.5 + "§f원을 흭득하셨습니다.", 5, 50, 5);
                EconomyAPI.getInstance().giveMoney(p, DataManager.getInstance().getCoinMin() * 1.5);
                GameData.getInstance().stopGame(target);
                p.closeInventory();
                GambleLogger.getInstance().addLog(p.getName() + "님이 동전 도박 베팅에 성공하여 " + DataManager.getInstance().getCoinMin() * 1.5 + "원 흭득.");
            }
        }, 20);
    }

    public void lose() {
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            Player p = Bukkit.getPlayer(target);
            if (GameData.getInstance().isPlaying(target)) {
                p.sendTitle(header, "§7베팅에 실패하셨습니다..", 5, 50, 5);
                GameData.getInstance().stopGame(target);
                p.closeInventory();
            }
        }, 20);
    }
}
