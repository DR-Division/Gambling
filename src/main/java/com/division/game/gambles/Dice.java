package com.division.game.gambles;

import com.division.Gamble;
import com.division.data.DataManager;
import com.division.data.GameData;
import com.division.file.GambleLogger;
import com.division.game.Game;
import com.division.util.EconomyAPI;
import com.division.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Dice implements Game {

    private String header;
    private UUID target;
    private Role role;
    private Gamble Plugin;
    private int mine, other, value;

    public Dice(UUID target, int value, Gamble Plugin) {
        header = DataManager.getInstance().getHeader();
        this.target = target;
        this.Plugin = Plugin;
        setGUI();
        GameData.getInstance().playGame(target, this, value);
        role = null;
    }

    public enum Role {
        LOW,
        MID,
        HIGH
    }

    @Override
    public void setGUI() {
        Player p = Bukkit.getPlayer(target);
        Inventory dice = Bukkit.createInventory(null, 54, header + "§0주사위");
        ItemStack help = InventoryUtil.createItemStack(Material.NETHER_STAR, header + "§c주사위 룰 선택", " ", " §8-  " + "§f게임에 사용될 룰을 선택해주세요.", " ");
        ItemStack low = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§e로우", " ", " §8-  " + "§f낮은 숫자를 가진 사람이 승리합니다.", " ");
        ItemStack mid = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§6미드", " ", " §8-  " + "§f50에 가까운 숫자를 가진 사람이 승리합니다.", " ");
        ItemStack high = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§c하이", " ", " §8-  " + "§f높은 숫자를 가진 사람이 승리합니다.", " ");
        dice.setItem(4, help);
        dice.setItem(19, low);
        dice.setItem(22, mid);
        dice.setItem(25, high);
        if (p != null)
            p.openInventory(dice);
    }

    public void setRole(Role role) {
        Player p = Bukkit.getPlayer(target);
        if (p != null) {
            EconomyAPI.getInstance().steelMoney(p, GameData.getInstance().getData(target).getBetMoney());
            GambleLogger.getInstance().addLog(p.getName() + "님이 주사위 도박에 " + GameData.getInstance().getData(target).getBetMoney() + "원 베팅");
            ItemStack help = new ItemStack(Material.AIR);
            int value = GameData.getInstance().getData(target).getBetMoney() + (int) ((Math.random() + 0.5) * GameData.getInstance().getData(target).getBetMoney()); //0.5~1.5
            InventoryUtil.clearGUI(p);
            this.role = role;
            ItemStack edge = InventoryUtil.createItemStack(Material.IRON_FENCE, " ");
            for (int i = 0; i < 9; i++) {
                p.getOpenInventory().setItem(i, edge);
                p.getOpenInventory().setItem(i + 45, edge);
            }
            for (int i = 0; i < 4; i++) {
                p.getOpenInventory().setItem((i + 1) * 9, edge);
                p.getOpenInventory().setItem((i + 2) * 9 - 1, edge);
            }
            switch (role) {
                case LOW:
                    help = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§b주사위 도박 §7[ §e로우 §7]", " ", " §8-  §f숫자가 낮을수록 유리합니다", " §8-  §f현재 총 베팅금액 §7: §6" + value + "§f원", " ");
                    break;
                case MID:
                    help = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§b주사위 도박 §7[ §6미드 §7]", " ", " §8-  §f숫자가 50에 가까울수록 유리합니다", " §8-  §f현재 총 베팅금액 §7: §6" + value + "§f원", " ");
                    break;
                case HIGH:
                    help = InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + "§b주사위 도박 §7[ §6하이 §7]", " ", " §8-  §f숫자가 높을수록 유리합니다", " §8-  §f현재 총 베팅금액 §7: §6" + value + "§f원", " ");
                    break;
            }
            ItemStack diceMine = InventoryUtil.createItemStack(Material.GOLD_BLOCK, header + "§b" + p.getName() + "§f의 §6주사위", " §8-  §f클릭시 주사위를 던집니다.");
            ItemStack diceOther = InventoryUtil.createItemStack(Material.GOLD_BLOCK, header + "§c컴퓨터§f의 §6주사위", " §8-  §f클릭시 주사위를 던집니다.");
            p.getOpenInventory().setItem(13, help);
            p.getOpenInventory().setItem(29, diceMine);
            p.getOpenInventory().setItem(33, diceOther);
        }
    }

    public void checkDice(ItemStack stack) {
        Player p = Bukkit.getPlayer(target);
        if (p != null) {
            int random = ThreadLocalRandom.current().nextInt(1, 100);
            stack.setType(Material.PAPER);
            InventoryUtil.setLore(stack, " ", "§8-  §6주사위 §f눈이 §b" + random + "§f이(가) 나왔습니다!", " ");
            checkWin(p);
        }
    }

    public void checkWin(Player p) {
        if (p.getOpenInventory().getItem(29).getType() == Material.PAPER && p.getOpenInventory().getItem(33).getType() == Material.PAPER) {
            value = getMoney(p.getOpenInventory().getItem(13));
            mine = getValue(p.getOpenInventory().getItem(29));
            other = getValue(p.getOpenInventory().getItem(33));
            Bukkit.getScheduler().runTaskLater(Plugin, () -> {
                if (GameData.getInstance().isPlaying(target)) {
                    switch (role) {
                        case LOW:
                            if (mine < other) {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임§f에서 승리하여 §6" + value + "§f원을 흭득 하셨습니다!", 5, 50, 5);
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.0f);
                                EconomyAPI.getInstance().giveMoney(p, value);
                                GambleLogger.getInstance().addLog(p.getName() + "님이 주사위 도박 - 로우 에서 승리하여 " + value + "원 흭득.");
                            }
                            else if (mine == other) {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임§f에서 비겨서 §6" + value / 2 + "§f원을 흭득 하셨습니다!", 5, 50, 5);
                                p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 2.0f, 1.0f);
                                EconomyAPI.getInstance().giveMoney(p, (double) value / 2);
                                GambleLogger.getInstance().addLog(p.getName() + "님이 주사위 도박 - 로우 에서 비겨서 " + value / 2 + "원 흭득.");
                            }
                            else {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임에서 패배 하셨습니다..ㅜㅜ", 5, 50, 5);
                            }
                            break;
                        case MID:
                            if (Math.abs(50 - mine) < Math.abs(50 - other)) {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임§f에서 승리하여 §6" + value + "§f원을 흭득 하셨습니다!", 5, 50, 5);
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.0f);
                                EconomyAPI.getInstance().giveMoney(p, value);
                                GambleLogger.getInstance().addLog(p.getName() + "님이 주사위 도박 - 미드 에서 승리하여 " + value + "원 흭득.");
                            }
                            else if (Math.abs(50 - mine) == Math.abs(50 - other)) {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임§f에서 비겨서 §6" + value / 2 + "§f원을 흭득 하셨습니다!", 5, 50, 5);
                                p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 2.0f, 1.0f);
                                EconomyAPI.getInstance().giveMoney(p, (double) value / 2);
                                GambleLogger.getInstance().addLog(p.getName() + "님이 주사위 도박 - 미드 에서 비겨서 " + value / 2 + "원 흭득.");
                            }
                            else {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임에서 패배 하셨습니다..ㅜㅜ", 5, 50, 5);
                            }
                            break;
                        case HIGH:
                            if (mine > other) {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임§f에서 승리하여 §6" + value + "§f원을 흭득 하셨습니다!", 5, 50, 5);
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.0f);
                                EconomyAPI.getInstance().giveMoney(p, value);
                                GambleLogger.getInstance().addLog(p.getName() + "님이 주사위 도박 - 하이 에서 승리하여 " + value + "원 흭득.");
                            }
                            else if (mine == other) {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임§f에서 비겨서 §6" + value / 2 + "§f원을 흭득 하셨습니다!", 5, 50, 5);
                                p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 2.0f, 1.0f);
                                EconomyAPI.getInstance().giveMoney(p, (double) value / 2);
                                GambleLogger.getInstance().addLog(p.getName() + "님이 주사위 도박 - 하이 에서 비겨서 " + value / 2 + "원 흭득.");
                            }
                            else {
                                GameData.getInstance().stopGame(target);
                                p.closeInventory();
                                p.sendTitle(header, "§c게임에서 패배 하셨습니다..ㅜㅜ", 5, 50, 5);
                            }
                            break;
                    }
                }
            }, 30L);

        }
    }

    @Override
    public void play() {
        //not used
    }

    public boolean isPlaying() {
        return role != null;
    }

    public int getValue(ItemStack item) {
        List<String> lore = item.getItemMeta().getLore();
        String[] split = Objects.requireNonNull(lore).get(1).split("눈이");
        split[1] = ChatColor.stripColor(split[1]).replace("이(가) 나왔습니다!", "").replace(" ", "");
        return Integer.parseInt(split[1]);
    }

    public int getMoney(ItemStack item) {
        List<String> lore = item.getItemMeta().getLore();
        String[] split = Objects.requireNonNull(lore).get(2).split(":");
        split[1] = ChatColor.stripColor(split[1]).replace(" ", "").replace("원", "");
        return Integer.parseInt(split[1]);
    }
}
