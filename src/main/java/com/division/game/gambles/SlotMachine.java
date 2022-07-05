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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class SlotMachine implements Game {

    public ItemStack[] slotItem;
    private String header;
    private UUID target;
    private int taskID;
    private Gamble Plugin;
    private int first, second, third, count;

    public SlotMachine(UUID data, Gamble Plugin) {
        header = DataManager.getInstance().getHeader();
        slotItem = new ItemStack[10];
        target = data;
        GameData.getInstance().playGame(target, this, -5);
        taskID = 0;
        count = 0;
        this.Plugin = Plugin;
        setGUI();
    }

    private void slotItemSet() {
        slotItem[0] = InventoryUtil.createItemStack(Material.NETHER_STAR, header + "§f네더의 별");
        slotItem[1] = InventoryUtil.createItemStack(Material.APPLE, header + "§c사과");
        slotItem[2] = InventoryUtil.createItemStack(Material.PORK, header + "§e돼지고기");
        slotItem[3] = InventoryUtil.createItemStack(Material.GOLDEN_APPLE, header + "§6황금사과");
        slotItem[4] = InventoryUtil.createItemStack(Material.CAKE, header + "§f케이크");
        slotItem[5] = InventoryUtil.createItemStack(Material.COOKIE, header + "§6쿠키");
        slotItem[6] = InventoryUtil.createItemStack(Material.MELON, header + "§a멜론");
        slotItem[7] = InventoryUtil.createItemStack(Material.BEETROOT, header + "§4사탕무");
        slotItem[8] = InventoryUtil.createItemStack(Material.POTATO_ITEM, header + "§6감자");
        slotItem[9] = InventoryUtil.createItemStack(Material.EGG, header + "§e달걀");

    }

    private int generateRandomItem() {
        ThreadLocalRandom instance = ThreadLocalRandom.current();
        return instance.nextInt(0, 10);
    }

    public int getValue(ItemStack item) {
        for (int i = 0; i < 10; i++)
            if (slotItem[i].getType() == item.getType())
                return i;
        return 0;
    }

    public void check(int a, int b, int c) {
        Player p = Bukkit.getPlayer(target);
        if (a == b && b == c) {
            double result = Double.parseDouble(String.format("%.2f", DataManager.getInstance().getSlotCount() * 7 / 10));
            double last = Math.round((DataManager.getInstance().getSlotCount() - result) * 100) / 100.0;
            double give = Math.round((result - result * (int)(result / 5000) * 0.05) * 100) / 100.0;
            if (give <= 0)
                give = 0;
            GameData.getInstance().stopGame(target);
            p.closeInventory();
            Bukkit.getServer().broadcastMessage(" ");
            Bukkit.getServer().broadcastMessage(header + "§b" + p.getName() + "§f님이 §6잭팟§f을 터트렸습니다!!! §e흭득 금액 §7: §6" + give + "§f원");
            Bukkit.getServer().broadcastMessage(" ");
            GambleLogger.getInstance().addLog(p.getName() + "님이 잭팟으로 " + give + "원 흭득");
            EconomyAPI.getInstance().giveMoney(p, give);
            DataManager.getInstance().setSlotCount(last);
        }
        else {
            p.sendTitle(header,  "§b아쉽게도 잭팟이 터지지 않았습니다.", 5, 50, 5);
            GameData.getInstance().getData(target).setBetMoney(-5);
            count = 0;
        }
    }

    public int flow(int a) {
        return a + 1 == 10 ? 0 : a + 1;
    }

    @Override
    public void setGUI() {
        Player p = Bukkit.getPlayer(target);
        if (p != null) {
            String count = String.format("%.2f", DataManager.getInstance().getSlotCount());
            Inventory slot = InventoryUtil.createInventory(header + "§0슬롯머신", 27);
            ItemStack wool = InventoryUtil.createItemStack(Material.WOOL, header + "§b현재 누적금액", (short) 15, " ", " §8-  §f현재 §c슬롯머신§f에 누적된 §6금액§f은 §b" + count + "§f원 입니다.", " ");
            ItemStack edge = InventoryUtil.createItemStack(Material.IRON_FENCE, " ");
            ItemStack lever = InventoryUtil.createItemStack(Material.LEVER, header + "§b슬롯머신 레버", " ", " §8-  §f클릭시 슬롯머신을 돌립니다. §7( §f1회당 §6" + DataManager.getInstance().getSlotMin() + "§f원 §7)", " ");
            for (int i = 0; i < 3; ++i) {
                slot.setItem(i, edge);
                slot.setItem(i + 3, wool);
                slot.setItem(i + 6, edge);
                slot.setItem(i + 9, edge);
                slot.setItem(i + 15, edge);
                slot.setItem(i + 18, edge);
                slot.setItem(i + 21, wool);
                slot.setItem(i + 24, edge);
            }
            slotItemSet();
            slot.setItem(12, slotItem[generateRandomItem()]);
            slot.setItem(13, slotItem[generateRandomItem()]);
            slot.setItem(14, slotItem[generateRandomItem()]);
            slot.setItem(16, lever);
            p.openInventory(slot);
        }
    }

    public boolean isRunning() {
        return GameData.getInstance().getData(target).getBetMoney() != -5;
    }

    public void stop(){
        Bukkit.getScheduler().cancelTask(taskID);
    }

    @Override
    public void play() {
        Player p = Bukkit.getPlayer(target);
        if (EconomyAPI.getInstance().getMoney(p) < DataManager.getInstance().getSlotMin()) {
            p.sendMessage(header + "§c금액이 부족합니다.." + " §7( §6" + DataManager.getInstance().getSlotMin() + "§f원 " + "§7)");
        }
        else {
            first = getValue(p.getOpenInventory().getItem(12));
            second = getValue(p.getOpenInventory().getItem(13));
            third = getValue(p.getOpenInventory().getItem(14));
            ThreadLocalRandom random = ThreadLocalRandom.current();
            final int firstEnd = random.nextInt(40, 71);  //0~30 + 40 -> 40 ~ 70
            final int secondEnd = random.nextInt(70, 101); //0~30 + 70 -> 70 ~ 100
            final int lastEnd = random.nextInt(100, 131); //0~30 + 100 -> 100 ~ 130
            GameData.getInstance().getData(target).setBetMoney(DataManager.getInstance().getSlotMin());
            EconomyAPI.getInstance().steelMoney(p, DataManager.getInstance().getSlotMin());
            DataManager.getInstance().setSlotCount(DataManager.getInstance().getSlotMin() + DataManager.getInstance().getSlotCount());
            String value = String.format("%.2f", DataManager.getInstance().getSlotCount());
            ItemStack wool = InventoryUtil.createItemStack(Material.WOOL, header + "§b현재 누적금액", (short) 15, " ", " §8-  §f현재 §c슬롯머신§f에 누적된 §6금액§f은 §b" + value + "§f원 입니다.", " ");
            for (int i = 0; i < 3; i++) {
                p.getOpenInventory().setItem(i + 3, wool);
                p.getOpenInventory().setItem(i + 21, wool);
            }
            taskID = Bukkit.getScheduler().runTaskTimer(Plugin, () -> {
                if (!p.getOpenInventory().getTitle().contains("슬롯머신")) {
                    Bukkit.getScheduler().cancelTask(taskID);
                }
                else if (count < firstEnd) {
                    first = flow(first);
                    second = flow(second);
                    third = flow(third);
                }
                else if (count == firstEnd) {
                    second = flow(second);
                    third = flow(third);
                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                }
                else if (count < secondEnd) {
                    second = flow(second);
                    third = flow(third);
                }
                else if (count == secondEnd) {
                    third = flow(third);
                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                }
                else if (count < lastEnd) {
                    third = flow(third);
                }
                else {
                    Bukkit.getScheduler().cancelTask(taskID);
                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                    check(first, second, third);
                }
                if (p.getOpenInventory().getTitle().contains("슬롯머신")) {
                    p.getOpenInventory().setItem(12, slotItem[first]);
                    p.getOpenInventory().setItem(13, slotItem[second]);
                    p.getOpenInventory().setItem(14, slotItem[third]);
                    count++;
                }
            }, 0L, 1L).getTaskId();

        }
    }

}
