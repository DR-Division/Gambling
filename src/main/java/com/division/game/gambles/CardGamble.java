package com.division.game.gambles;

import com.division.Gamble;
import com.division.data.CardData;
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

public class CardGamble implements Game {

    private int MAX_BET;
    private UUID player, target;
    private char playerCard, targetCard;
    private Gamble Plugin;
    private String header;
    private boolean turn;

    public CardGamble(UUID player, UUID target, int value, Gamble Plugin) {
        MAX_BET = value;
        this.player = player;
        this.target = target;
        this.Plugin = Plugin;
        header = DataManager.getInstance().getHeader();
        GameData.getInstance().playGame(player, this, value);
        GameData.getInstance().playGame(target, this, value);
        playerCard = 'K';
        targetCard = 'K';
        turn = true; //player차례
        setGUI();
    }

    public enum Type {
        KING,
        CITIZEN,
        SLAVE
    }

    @Override
    public void setGUI() {
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        Inventory inv = InventoryUtil.createInventory(header + "§0카드 도박", 54);
        Inventory invClone = InventoryUtil.createInventory(header + "§0카드 도박", 54);
        ItemStack edge = InventoryUtil.createItemStack(Material.IRON_FENCE, " ");
        ItemStack sign = InventoryUtil.createItemStack(Material.SIGN, header + "§b배팅액", " ", " §8-  §f현재 베팅액은 §c" + MAX_BET + "§f원 입니다.", " ");
        ItemStack note = InventoryUtil.createItemStack(Material.NOTE_BLOCK, header + "§b소리내기", " ", " §8-  §f소리로 주의를 끌어봅시다.", " §8-  §c과도한 어그로는 손절을 유발합니다.", " ");
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, edge);
            inv.setItem(45 + i, edge);
            invClone.setItem(i, edge);
            invClone.setItem(45 + i, edge);
        }
        for (int i = 1; i <= 4; i++) {
            inv.setItem(9 * i, edge);
            inv.setItem(9 * (i + 1) - 1, edge);
            invClone.setItem(9 * i, edge);
            invClone.setItem(9 * (i + 1) - 1, edge);
        }
        inv.setItem(4, sign);
        invClone.setItem(4, sign);
        inv.setItem(49, note);
        invClone.setItem(49, note);
        p.openInventory(inv);
        t.openInventory(invClone);
        setTurnByValue();
    }

    public boolean checkTurn(UUID data) {
        return (turn && player.equals(data)) || (!turn && target.equals(data));
    }

    public void setTurnByValue() {
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        ItemStack chest = InventoryUtil.createItemStack(Material.RED_SHULKER_BOX, header + "§c현재 상대의 차례입니다.");
        ItemStack allowChest = InventoryUtil.createItemStack(Material.GREEN_SHULKER_BOX, header + "§c현재 당신의 차례입니다.");
        ItemStack king = InventoryUtil.createItemStack(Material.BOOK, header + "§cKing(왕)", " ", " §8-  §f클릭시 King(왕) 카드를 선택합니다.", " ");
        ItemStack citizen = InventoryUtil.createItemStack(Material.BOOK, header + "§bCitizen(시민)", " ", " §8-  §f클릭시 Citizen(시민) 카드를 선택합니다.", " ");
        ItemStack slave = InventoryUtil.createItemStack(Material.BOOK, header + "§7Slave(노예)", " ", " §8-  §f클릭시 Slave(노예) 카드를 선택합니다.", " ");
        if (turn) {
            //사용가능
            t.getOpenInventory().setItem(22, chest);
            t.getOpenInventory().setItem(38, new ItemStack(Material.AIR));
            t.getOpenInventory().setItem(40, new ItemStack(Material.AIR));
            t.getOpenInventory().setItem(42, new ItemStack(Material.AIR));
            p.getOpenInventory().setItem(22, allowChest);
            p.getOpenInventory().setItem(38, king);
            p.getOpenInventory().setItem(40, citizen);
            p.getOpenInventory().setItem(42, slave);

        }
        else {
            //사용불가능
            p.getOpenInventory().setItem(22, chest);
            p.getOpenInventory().setItem(38, new ItemStack(Material.AIR));
            p.getOpenInventory().setItem(40, new ItemStack(Material.AIR));
            p.getOpenInventory().setItem(42, new ItemStack(Material.AIR));
            t.getOpenInventory().setItem(22, allowChest);
            t.getOpenInventory().setItem(38, king);
            t.getOpenInventory().setItem(40, citizen);
            t.getOpenInventory().setItem(42, slave);
        }
    }

    public void changeTurn() {
        if (turn) {
            turn = false;
            setTurnByValue();
        }
        else
            result();
    }

    public void setCard(Type card, UUID data) {
        if (checkTurn(data)) {
            if (turn) {
                switch (card) {
                    case KING:
                        playerCard = 'K';
                        break;
                    case CITIZEN:
                        playerCard = 'C';
                        break;
                    case SLAVE:
                        playerCard = 'S';
                        break;
                }
            }
            else {
                switch (card) {
                    case KING:
                        targetCard = 'K';
                        break;
                    case CITIZEN:
                        targetCard = 'C';
                        break;
                    case SLAVE:
                        targetCard = 'S';
                        break;
                }
            }
            changeTurn();
        }
    }

    public void result() {
        ItemStack pDeck, tDeck;
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        t.getOpenInventory().setItem(22, new ItemStack(Material.AIR));
        t.getOpenInventory().setItem(38, new ItemStack(Material.AIR));
        t.getOpenInventory().setItem(40, new ItemStack(Material.AIR));
        t.getOpenInventory().setItem(42, new ItemStack(Material.AIR));
        p.getOpenInventory().setItem(22, new ItemStack(Material.AIR));
        switch (playerCard) {
            case 'K':
                pDeck = InventoryUtil.createItemStack(Material.BOOK, header + "§cKing(왕)", " ", " §8-  §c" + p.getName() + "§f의 카드입니다.", " ");
                break;
            case 'C':
                pDeck = InventoryUtil.createItemStack(Material.BOOK, header + "§bCitizen(시민)", " ", " §8-  §c" + p.getName() + "§f의 카드입니다.", " ");
                break;
            default:
            case 'S':
                pDeck = InventoryUtil.createItemStack(Material.BOOK, header + "§7Slave(노예)", " ", " §8-  §c" + p.getName() + "§f의 카드입니다.", " ");
                break;
        }
        switch (targetCard) {
            case 'K':
                tDeck = InventoryUtil.createItemStack(Material.BOOK, header + "§cKing(왕)", " ", " §8-  §c" + t.getName() + "§f의 카드입니다.", " ");
                break;
            case 'C':
                tDeck = InventoryUtil.createItemStack(Material.BOOK, header + "§bCitizen(시민)", " ", " §8-  §c" + t.getName() + "§f의 카드입니다.", " ");
                break;
            default:
            case 'S':
                tDeck = InventoryUtil.createItemStack(Material.BOOK, header + "§7Slave(노예)", " ", " §8-  §c" + t.getName() + "§f의 카드입니다.", " ");
                break;
        }
        p.getOpenInventory().setItem(29, pDeck);
        p.getOpenInventory().setItem(33, tDeck);
        t.getOpenInventory().setItem(29, tDeck);
        t.getOpenInventory().setItem(33, pDeck);
        Bukkit.getServer().getScheduler().runTaskLater(Plugin, () -> {
            if (GameData.getInstance().isPlaying(player) && GameData.getInstance().isPlaying(target)) {
                if (playerCard == targetCard) {
                    p.sendTitle(header, "§f비겼습니다!", 5, 50, 5);
                    t.sendTitle(header, "§f비겼습니다!", 5, 50, 5);
                    EconomyAPI.getInstance().giveMoney(p, MAX_BET);
                    EconomyAPI.getInstance().giveMoney(t, MAX_BET);
                    GameData.getInstance().stopGame(player);
                    GameData.getInstance().stopGame(target);
                    CardData.getInstance().remove(player);
                    GambleLogger.getInstance().addLog(p.getName() + "님이 " + t.getName() + "와 카드 도박에서 무승부");
                    p.closeInventory();
                    t.closeInventory();
                }
                else if ((playerCard == 'K' && targetCard == 'C') || (playerCard == 'C' && targetCard == 'S') || (playerCard == 'S' && targetCard == 'K')) {
                    p.sendTitle(header, "§f승리하셨습니다! §b흭득 금액 §7: §6" + MAX_BET * 2 + "§f원", 5, 50, 5);
                    t.sendTitle(header, "§f패배하셨습니다.. ", 5, 50, 5);
                    EconomyAPI.getInstance().giveMoney(p, MAX_BET * 2);
                    GameData.getInstance().stopGame(player);
                    GameData.getInstance().stopGame(target);
                    CardData.getInstance().remove(player);
                    GambleLogger.getInstance().addLog(p.getName() + "님이 카드도박 승리" + t.getName() + "님이 패배하여 " + MAX_BET * 2 + "원 흭득");
                    p.closeInventory();
                    t.closeInventory();
                }
                else {
                    t.sendTitle(header, "§f승리하셨습니다! §b흭득 금액 §7: §6" + MAX_BET * 2 + "§f원", 5, 50, 5);
                    p.sendTitle(header, "§f패배하셨습니다.. ", 5, 50, 5);
                    EconomyAPI.getInstance().giveMoney(t, MAX_BET * 2);
                    GameData.getInstance().stopGame(player);
                    GameData.getInstance().stopGame(target);
                    CardData.getInstance().remove(player);
                    GambleLogger.getInstance().addLog(t.getName() + "님이 카드도박 승리" + p.getName() + "님이 패배하여 " + MAX_BET * 2 + "원 흭득");
                    p.closeInventory();
                    t.closeInventory();
                }
            }


        }, 40L);

    }

    public void quitGame(UUID data) {
        if (data.equals(player)) {
            Player t = Bukkit.getPlayer(target);
            t.sendTitle(header, "§f카드 도박 도중 상대방이 GUI를 닫아 도박이 중단 되었습니다.", 5, 50, 5);
            t.playSound(t.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
            EconomyAPI.getInstance().giveMoney(t, MAX_BET * 2);
            CardData.getInstance().remove(player);
            GameData.getInstance().stopGame(player);
            GameData.getInstance().stopGame(target);
            t.closeInventory();
        }
        else {
            Player t = Bukkit.getPlayer(player);
            t.sendTitle(header, "§f카드 도박 도중 상대방이 GUI를 닫아 도박이 중단 되었습니다", 5, 50, 5);
            t.playSound(t.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
            EconomyAPI.getInstance().giveMoney(t, MAX_BET * 2);
            CardData.getInstance().remove(player);
            GameData.getInstance().stopGame(player);
            GameData.getInstance().stopGame(target);
            t.closeInventory();
        }
    }

    @Override
    public void play() {
        //NOT USE
    }
}
