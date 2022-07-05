package com.division.game.gambles;

import com.division.Gamble;
import com.division.data.DataManager;
import com.division.data.GameData;
import com.division.data.IndianData;
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

public class Indian implements Game {

    private UUID player, target;
    private int playerBet, targetBet;
    private int MAX_BET;
    private int playerDeck, targetDeck;
    private Gamble Plugin;
    private String header;
    private boolean turn; //true인경우 player차례, false인경우 target차례

    public Indian(UUID player, UUID target, Gamble Plugin) {
        this.player = player;
        this.target = target;
        this.Plugin = Plugin;
        MAX_BET = IndianData.getInstance().getMoney(player);
        playerBet = MAX_BET / 20;
        targetBet = MAX_BET / 20;
        playerDeck = -1;
        targetDeck = -1;
        GameData.getInstance().playGame(player, this, playerBet);
        GameData.getInstance().playGame(target, this, targetBet);
        header = DataManager.getInstance().getHeader();
        turn = true;
        setGUI();
    }

    @Override
    public void setGUI() {
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        Inventory inv = InventoryUtil.createInventory(header + "§0인디언포커", 54);
        Inventory invClone = InventoryUtil.createInventory(header + "§0인디언포커", 54);
        ItemStack edge = InventoryUtil.createItemStack(Material.IRON_FENCE, " ");
        ItemStack deck = InventoryUtil.createItemStack(Material.CHEST, header + "§b패 확인", " ", " §8-  §f클릭해서 패를 확인하세요.", " ");
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, edge);
            inv.setItem(i + 45, edge);
            invClone.setItem(i, edge);
            invClone.setItem(i + 45, edge);
        }
        for (int i = 1; i <= 4; i++) {
            inv.setItem(9 * i, edge);
            inv.setItem(9 * (i + 1) - 1, edge);
            invClone.setItem(9 * i, edge);
            invClone.setItem(9 * (i + 1) - 1, edge);
        }
        inv.setItem(31, deck);
        invClone.setItem(31, deck);
        p.openInventory(inv);
        t.openInventory(invClone);
        //다른 GUI띄우게 하기
        setTurnByValue();
    }


    @Override
    public void play() {
        //NOT USE
    }

    public void setTurnByValue() {
        ItemStack lampON;
        ItemStack betON;
        ItemStack dieON;
        ItemStack lampOFF;
        ItemStack betOFF;
        ItemStack dieOFF;
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        if (turn) {
            lampON = InventoryUtil.createItemStack(Material.REDSTONE_BLOCK, header + "§b당신의 차례 입니다.", " ", " §8-  §f베팅 혹은 다이를 선택해 주세요.", " §8-  §f상대방의 현재 베팅액 §7: §6" + targetBet + "§f원", " ");
            betON = InventoryUtil.createItemStack(Material.WOOD_BUTTON, header + "§c베팅(Bet)", " ", " §8-  §c현재 당신의 베팅액 §7: §6" + playerBet + "§f원", " §8-  §b현재 상대의 베팅액 §7: §6" + targetBet + "§f원", " §8-  §c좌클릭시 §6" + MAX_BET / 20 + "§f원을 베팅합니다.", " §8-  §a우클릭시 베팅을 종료합니다. 단 상대방의 베팅액보다 크거나 같아야 합니다.", " ");
            dieON = InventoryUtil.createItemStack(Material.STONE_BUTTON, header + "§b다이(Die)", " ", " §8-  §f클릭시 게임을 포기합니다.", " ");
            lampOFF = InventoryUtil.createItemStack(Material.REDSTONE_LAMP_OFF, header + "§c상대방의 차례 입니다.", " ", " §8-  §f상대방의 현재 베팅액 §7: §6" + playerBet + "§f원", " ");
            betOFF = InventoryUtil.createItemStack(Material.WOOD_BUTTON, header + "§c베팅(Bet)", " ", " §8-  §c현재 당신의 베팅액 §7: §6" + targetBet + "§f원", " §8-  §b현재 상대의 베팅액 §7: §6" + playerBet + "§f원", " ");
            dieOFF = InventoryUtil.createItemStack(Material.STONE_BUTTON, header + "§b다이(Die)", " ", " §8-  §f클릭시 게임을 포기합니다. (현재 사용불가)", " ");
            p.getOpenInventory().setItem(13, lampON);
            p.getOpenInventory().setItem(29, betON);
            p.getOpenInventory().setItem(33, dieON);
            t.getOpenInventory().setItem(13, lampOFF);
            t.getOpenInventory().setItem(29, betOFF);
            t.getOpenInventory().setItem(33, dieOFF);
        }
        else {
            lampON = InventoryUtil.createItemStack(Material.REDSTONE_BLOCK, header + "§b당신의 차례 입니다.", " ", " §8-  §f베팅 혹은 다이를 선택해 주세요.", " §8-  §f상대방의 현재 베팅액 §7: §6" + playerBet + "§f원", " ");
            betON = InventoryUtil.createItemStack(Material.WOOD_BUTTON, header + "§c베팅(Bet)", " ", " §8-  §c현재 당신의 베팅액 §7: §6" + targetBet + "§f원", " §8-  §b현재 상대의 베팅액 §7: §6" + playerBet + "§f원", " §8-  §c좌클릭시 §6" + MAX_BET / 20 + "§f원을 베팅합니다.", " §8-  §a우클릭시 베팅을 종료합니다. 단 상대방의 베팅액보다 크거나 같아야 합니다.", " ");
            dieON = InventoryUtil.createItemStack(Material.STONE_BUTTON, header + "§b다이(Die)", " ", " §8-  §f클릭시 게임을 포기합니다.", " ");
            lampOFF = InventoryUtil.createItemStack(Material.REDSTONE_LAMP_OFF, header + "§c상대방의 차례 입니다.", " ", " §8-  §f상대방의 현재 베팅액 §7: §6" + targetBet + "§f원", " ");
            betOFF = InventoryUtil.createItemStack(Material.WOOD_BUTTON, header + "§c베팅(Bet)", " ", " §8-  §c현재 당신의 베팅액 §7: §6" + playerBet + "§f원", " §8-  §b현재 상대의 베팅액 §7: §6" + targetBet + "§f원", " ");
            dieOFF = InventoryUtil.createItemStack(Material.STONE_BUTTON, header + "§b다이(Die)", " ", " §8-  §f클릭시 게임을 포기합니다. (현재 사용불가)", " ");
            t.getOpenInventory().setItem(13, lampON);
            t.getOpenInventory().setItem(29, betON);
            t.getOpenInventory().setItem(33, dieON);
            p.getOpenInventory().setItem(13, lampOFF);
            p.getOpenInventory().setItem(29, betOFF);
            p.getOpenInventory().setItem(33, dieOFF);
        }
    }

    public void checkDeck(ItemStack stack, UUID data) {
        int value = ThreadLocalRandom.current().nextInt(1, 11);
        if (data.equals(player))
            //플레이어가 체크한 경우
            targetDeck = value;
        else
            playerDeck = value;
        stack.setType(Material.PAPER);
        InventoryUtil.setDisplayName(stack, header + "§6" + value);
        InventoryUtil.setLore(stack, " ", " §8-  §f상대방의 패 입니다.", " §8-  §f상대방의 숫자가 낮을수록 유리하며 숫자가 같을경우 무승부입니다.", " ");
    }

    public boolean checkTurn(UUID data) {
        return (turn && player.equals(data)) || (!turn && target.equals(data));
    }

    public void quitGame(UUID data) {
        if (data.equals(player)) {
            Player t = Bukkit.getPlayer(target);
            t.sendTitle(header, "§f인디언 포커 도중 상대방이 GUI를 닫아 게임이 종료되고 돈이 환급되었습니다.", 5, 50, 5);
            t.playSound(t.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
            EconomyAPI.getInstance().giveMoney(t, playerBet + targetBet);
            IndianData.getInstance().remove(player);
            GameData.getInstance().stopGame(player);
            GameData.getInstance().stopGame(target);
            t.closeInventory();
        }
        else {
            Player t = Bukkit.getPlayer(player);
            t.sendTitle(header, "§f인디언 포커 도중 상대방이 GUI를 닫아 게임이 종료되고 돈이 환급되었습니다.", 5, 50, 5);
            t.playSound(t.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
            EconomyAPI.getInstance().giveMoney(t, playerBet + targetBet);
            IndianData.getInstance().remove(player);
            GameData.getInstance().stopGame(player);
            GameData.getInstance().stopGame(target);
            t.closeInventory();
        }
    }

    public void betMoney(UUID data) {
        Player p = Bukkit.getPlayer(data);
        if (checkTurn(data)) {
            if (turn) {
                //player
                if (playerBet == MAX_BET) {
                    p.sendMessage(header + "§c더이상 베팅 할 수 없습니다. 베팅을 종료해주세요");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1.0f, 1.0f);
                }
                else if (playerBet + MAX_BET / 20 >= MAX_BET) {
                    EconomyAPI.getInstance().steelMoney(p, MAX_BET - playerBet);
                    playerBet = MAX_BET;
                    GameData.getInstance().getData(player).setBetMoney(playerBet);
                    setTurnByValue();
                }
                else {
                    EconomyAPI.getInstance().steelMoney(p, MAX_BET / 20.0);
                    playerBet += MAX_BET / 20;
                    GameData.getInstance().getData(player).setBetMoney(playerBet);
                    setTurnByValue();
                }

            }
            else {
                //target
                if (targetBet == MAX_BET) {
                    p.sendMessage(header + "§c더이상 베팅 할 수 없습니다. 베팅을 종료해주세요");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1.0f, 1.0f);
                }
                else if (targetBet + MAX_BET / 20 >= MAX_BET) {
                    EconomyAPI.getInstance().steelMoney(p, MAX_BET - targetBet);
                    targetBet = MAX_BET;
                    GameData.getInstance().getData(target).setBetMoney(targetBet);
                    setTurnByValue();
                }
                else {
                    EconomyAPI.getInstance().steelMoney(p, MAX_BET / 20.0);
                    targetBet += MAX_BET / 20;
                    GameData.getInstance().getData(target).setBetMoney(targetBet);
                    setTurnByValue();
                }
            }
        }
        else
            p.sendMessage(header + "§c당신의 차례가 아닙니다.");
    }

    public void giveUp(UUID data) {
        Player p = Bukkit.getPlayer(data);
        if (checkTurn(data)) {
            if (turn) {
                //player가 포기
                Player t = Bukkit.getPlayer(target);
                int val = playerBet + targetBet;
                p.sendTitle(header, "§f게임을 포기하셨습니다.", 5, 50, 5);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 3.0f, 1.0f);
                t.sendTitle(header, "§f상대방이 게임을 포기하여 승리하셨습니다!", 5, 50, 5);
                t.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                EconomyAPI.getInstance().giveMoney(t, val);
                GameData.getInstance().stopGame(player);
                GameData.getInstance().stopGame(target);
                IndianData.getInstance().remove(player);
                p.closeInventory();
                t.closeInventory();
                GambleLogger.getInstance().addLog(p.getName() + "님이 인디언 포커 도중 포기해 " + t.getName() + "님이 " + val + "원 흭득");
            }
            else {
                Player t = Bukkit.getPlayer(player);
                int val = playerBet + targetBet;
                p.sendTitle(header, "§f게임을 포기하셨습니다.", 5, 50, 5);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 3.0f, 1.0f);
                t.sendTitle(header, "§f상대방이 게임을 포기하여 승리하셨습니다!", 5, 50, 5);
                t.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                EconomyAPI.getInstance().giveMoney(t, val);
                GameData.getInstance().stopGame(player);
                GameData.getInstance().stopGame(target);
                IndianData.getInstance().remove(player);
                p.closeInventory();
                t.closeInventory();
                GambleLogger.getInstance().addLog(p.getName() + "님이 인디언 포커 도중 포기해 " + t.getName() + "님이 " + val + "원 흭득");
            }
        }
        else
            p.sendMessage(header + "§c당신의 차례가 아닙니다.");
    }

    public void stop() {
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        ItemStack playerResult = InventoryUtil.createItemStack(Material.REDSTONE_BLOCK, header + "§c" + p.getName() + "§f님의 패 §7: §6" + playerDeck);
        ItemStack targetResult = InventoryUtil.createItemStack(Material.GLOWSTONE, header + "§c" + t.getName() + "§f님의 패 §7: §6" + targetDeck);
        p.getOpenInventory().setItem(13, new ItemStack(Material.AIR));
        p.getOpenInventory().setItem(29, new ItemStack(Material.AIR));
        p.getOpenInventory().setItem(31, new ItemStack(Material.AIR));
        p.getOpenInventory().setItem(33, new ItemStack(Material.AIR));
        t.getOpenInventory().setItem(13, new ItemStack(Material.AIR));
        t.getOpenInventory().setItem(29, new ItemStack(Material.AIR));
        t.getOpenInventory().setItem(31, new ItemStack(Material.AIR));
        t.getOpenInventory().setItem(33, new ItemStack(Material.AIR));
        p.getOpenInventory().setItem(29, playerResult);
        p.getOpenInventory().setItem(33, targetResult);
        t.getOpenInventory().setItem(29, targetResult);
        t.getOpenInventory().setItem(33, playerResult);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Plugin, () -> {
            if (GameData.getInstance().isPlaying(player) && GameData.getInstance().isPlaying(target)) {
                if (playerDeck > targetDeck) {
                    p.sendTitle(header, "§f승리! 총 베팅한 금액을 받습니다!", 5, 50, 5);
                    t.sendTitle(header, "§f패배 하였습니다 ㅠㅠㅠㅠ", 5, 50, 5);
                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                    t.playSound(t.getLocation(), Sound.BLOCK_NOTE_BELL, 3.0f, 1.0f);
                    EconomyAPI.getInstance().giveMoney(p, playerBet + targetBet);
                    GambleLogger.getInstance().addLog(p.getName() + "님이 인디언포커에서 승리하여 " + (playerBet + targetBet) + "원 흭득");
                }
                else if (playerDeck == targetDeck) {
                    p.sendTitle(header, "§f무승부! 베팅한 돈을 그대로 돌려받습니다.", 5, 50, 5);
                    t.sendTitle(header, "§f무승부! 베팅한 돈을 그대로 돌려받습니다.", 5, 50, 5);
                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                    t.playSound(t.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                    EconomyAPI.getInstance().giveMoney(p, playerBet);
                    EconomyAPI.getInstance().giveMoney(t, targetBet);
                    GambleLogger.getInstance().addLog(p.getName() + "님과 " + t.getName() + "의 인디언 포커가 무승부로 끝났습니다.");
                }
                else {
                    p.sendTitle(header, "§f패배 하였습니다 ㅠㅠㅠㅠ", 5, 50, 5);
                    t.sendTitle(header, "§f승리! 총 베팅한 금액을 받습니다!", 5, 50, 5);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 3.0f, 1.0f);
                    t.playSound(t.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
                    EconomyAPI.getInstance().giveMoney(t, playerBet + targetBet);
                    GambleLogger.getInstance().addLog(t.getName() + "님이 인디언 포커에서 승리하여 " + (playerBet + targetBet) + "원 흭득");
                }
            }
            GameData.getInstance().stopGame(player);
            GameData.getInstance().stopGame(target);
            p.closeInventory();
            t.closeInventory();
            IndianData.getInstance().remove(player);
        }, 40L);
    }

    public void giveTurn(UUID data) {
        Player p = Bukkit.getPlayer(data);
        if (checkTurn(data)) {
            if (turn) {
                if (playerBet == MAX_BET / 20)
                    p.sendMessage(header + "§4베팅을 진행하고 종료해주세요..!");
                else if (playerBet == targetBet)
                    stop();
                else if (playerBet < targetBet)
                    p.sendMessage(header + "§4상대보다 같거나 더 많이 베팅하셔야 합니다.");
                else {
                    turn = false;
                    setTurnByValue();
                }
            }
            else {
                if (targetBet == MAX_BET / 20)
                    p.sendMessage(header + "§4베팅을 진행하고 종료해주세요..!");
                else if (targetBet == playerBet)
                    stop();
                else if (targetBet < playerBet)
                    p.sendMessage(header + "§4상대보다 같거나 더 많이 베팅하셔야 합니다.");
                else {
                    turn = true;
                    setTurnByValue();
                }
            }
        }
        else
            p.sendMessage(header + "§c당신의 차례가 아닙니다.");
    }
}

