package com.division.game.gambles;

import com.division.Gamble;
import com.division.data.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Poker implements Game {

    private Gamble Plugin;
    private UUID player, target;
    private boolean turn;
    private int MAX_BET;
    private ArrayList<Card> playerDeck, targetDeck, deck;
    private String header;

    public Poker(UUID player, UUID target, Gamble Plugin, int value) {
        this.player = player;
        this.target = target;
        this.Plugin = Plugin;
        MAX_BET = value;
        GameData.getInstance().playGame(player, this, value);
        GameData.getInstance().playGame(target, this, value);
        playerDeck = new ArrayList<>();
        targetDeck = new ArrayList<>();
        deck = new ArrayList<>();
        header = DataManager.getInstance().getHeader();
        turn = true;
        setDeck();
        setGUI();
    }

    public enum DeckType {

        RoyalStraightFlush(13),
        BackStraightFlush(12),
        StraightFlush(11),
        FourCard(10),
        FullHouse(9),
        Flush(8),
        Mountain(7),
        BackStraight(6),
        Straight(5),
        Triple(4),
        TwoPair(3),
        OnePair(2),
        Top(1);

        private int value;

        DeckType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum ClickSlot {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE
    }

    @Override
    public void setGUI() {
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        Inventory inv = InventoryUtil.createInventory(header + "§0포커", 36);
        Inventory invClone = InventoryUtil.createInventory(header + "§0포커", 36);
        ItemStack edge = InventoryUtil.createItemStack(Material.IRON_FENCE, " ");
        ItemStack playerHead = InventoryUtil.createSkullStack(header + "§b" + p.getName(), p, " ");
        ItemStack targetHead = InventoryUtil.createSkullStack(header + "§c" + t.getName(), t, " ");
        ItemStack offLamp = InventoryUtil.createItemStack(Material.REDSTONE_LAMP_OFF, header + "§b베팅 상태", " ", " §8-  §f아직 베팅을 완료하지 않았습니다.", " ");
        ItemStack help = InventoryUtil.createItemStack(Material.SIGN, header + "§f포커", " ", " §8-  §f인첸트된 책은 1회 변경이 가능한 패 입니다.", " §8-  §f밑의 발광석을 클릭하여 베팅을 종료 할 수 있습니다.", " §8-  §f현재 베팅액 §7: §6" + MAX_BET * 2 + "§원", " ");
        for (int i = 0; i < 2; i++) {
            inv.setItem(i + 9, edge);
            invClone.setItem(i + 9, edge);
        }
        for (int i = 0; i < 9; i++) {
            inv.setItem(i + 27, edge);
            invClone.setItem(i + 27, edge);
        }
        Bukkit.getScheduler().runTaskAsynchronously(Plugin, () -> {
            inv.setItem(0, playerHead);
            inv.setItem(18, targetHead);
            invClone.setItem(0, playerHead);
            invClone.setItem(18, targetHead);
        });

        inv.setItem(1, offLamp);
        inv.setItem(5, help);
        inv.setItem(19, offLamp);
        invClone.setItem(1, offLamp);
        invClone.setItem(5, help);
        invClone.setItem(19, offLamp);
        p.openInventory(inv);
        t.openInventory(invClone);
        createDeck();
        showDeck();

    }

    @Override
    public void play() {
        //NOT USE
    }

    public void setDeck() {
        int count = 2;
        ArrayList<Card> cards = new ArrayList<>();
        Card.Color color = Card.Color.SPADE;
        for (int i = 1; i <= 52; i++) {
            cards.add(new Card(color, count++));
            if (i % 13 == 0) {
                count = 2;
                switch (i) {
                    case 13:
                        color = Card.Color.CLOVER;
                        break;
                    case 26:
                        color = Card.Color.DIAMOND;
                        break;
                    default:
                    case 39:
                        color = Card.Color.HEART;
                        break;
                }
            }
        }
        deck = cards;
    }

    public void createDeck() {
        int random;
        for (int i = 0; i < 5; i++) {
            random = ThreadLocalRandom.current().nextInt(0, deck.size());
            playerDeck.add(deck.get(random));
            deck.remove(random);
            random = ThreadLocalRandom.current().nextInt(0, deck.size());
            targetDeck.add(deck.get(random));
            deck.remove(random);
        }
    }

    public void showDeck() {
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        for (int i = 0; i < 5; i++) {
            p.getOpenInventory().setItem(i + 21, InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + playerDeck.get(i).toString(), " "));
            t.getOpenInventory().setItem(i + 21, InventoryUtil.createItemStack(Material.ENCHANTED_BOOK, header + targetDeck.get(i).toString(), " "));
        }
        p.getOpenInventory().setItem(32, InventoryUtil.createItemStack(Material.GLOWSTONE, header + "§c베팅 완료하기", " ", " §8-  §f클릭시 베팅을 완료 합니다.", " §8-  §f현재 당신의 덱 §7: §b" + checkDeck(player).toString(), " "));
        t.getOpenInventory().setItem(32, InventoryUtil.createItemStack(Material.GLOWSTONE, header + "§c베팅 완료하기", " ", " §8-  §f클릭시 베팅을 완료 합니다.", " §8-  §f현재 당신의 덱 §7: §b" + checkDeck(target).toString(), " "));
    }

    public void changeDeck(UUID val, ClickSlot slot) {
        int random;
        Player p = Bukkit.getPlayer(val);
        ThreadLocalRandom current = ThreadLocalRandom.current();
        random = current.nextInt(0, deck.size());
        switch (slot) {
            case ONE:
                if (player.equals(val))
                    playerDeck.set(0, deck.get(random));
                else
                    targetDeck.set(0, deck.get(random));
                p.getOpenInventory().setItem(21, InventoryUtil.createItemStack(Material.BOOK, header + deck.get(random).toString(), " "));
                p.getOpenInventory().setItem(32, InventoryUtil.createItemStack(Material.GLOWSTONE, header + "§c베팅 완료하기", " ", " §8-  §f클릭시 베팅을 완료 합니다.", " §8-  §f현재 당신의 덱 §7: §b" + checkDeck(val).toString(), " "));
                deck.remove(random);
                break;
            case TWO:
                if (player.equals(val))
                    playerDeck.set(1, deck.get(random));
                else
                    targetDeck.set(1, deck.get(random));
                p.getOpenInventory().setItem(22, InventoryUtil.createItemStack(Material.BOOK, header + deck.get(random).toString(), " "));
                p.getOpenInventory().setItem(32, InventoryUtil.createItemStack(Material.GLOWSTONE, header + "§c베팅 완료하기", " ", " §8-  §f클릭시 베팅을 완료 합니다.", " §8-  §f현재 당신의 덱 §7: §b" + checkDeck(val).toString(), " "));
                deck.remove(random);
                break;
            case THREE:
                if (player.equals(val))
                    playerDeck.set(2, deck.get(random));
                else
                    targetDeck.set(2, deck.get(random));
                p.getOpenInventory().setItem(23, InventoryUtil.createItemStack(Material.BOOK, header + deck.get(random).toString(), " "));
                p.getOpenInventory().setItem(32, InventoryUtil.createItemStack(Material.GLOWSTONE, header + "§c베팅 완료하기", " ", " §8-  §f클릭시 베팅을 완료 합니다.", " §8-  §f현재 당신의 덱 §7: §b" + checkDeck(val).toString(), " "));
                deck.remove(random);
                break;
            case FOUR:
                if (player.equals(val))
                    playerDeck.set(3, deck.get(random));
                else
                    targetDeck.set(3, deck.get(random));
                p.getOpenInventory().setItem(24, InventoryUtil.createItemStack(Material.BOOK, header + deck.get(random).toString(), " "));
                p.getOpenInventory().setItem(32, InventoryUtil.createItemStack(Material.GLOWSTONE, header + "§c베팅 완료하기", " ", " §8-  §f클릭시 베팅을 완료 합니다.", " §8-  §f현재 당신의 덱 §7: §b" + checkDeck(val).toString(), " "));
                deck.remove(random);
                break;
            default:
            case FIVE:
                if (player.equals(val))
                    playerDeck.set(4, deck.get(random));
                else
                    targetDeck.set(4, deck.get(random));
                p.getOpenInventory().setItem(25, InventoryUtil.createItemStack(Material.BOOK, header + deck.get(random).toString(), " "));
                p.getOpenInventory().setItem(32, InventoryUtil.createItemStack(Material.GLOWSTONE, header + "§c베팅 완료하기", " ", " §8-  §f클릭시 베팅을 완료 합니다.", " §8-  §f현재 당신의 덱 §7: §b" + checkDeck(val).toString(), " "));
                deck.remove(random);
                break;
        }
    }

    public void changeTurn(UUID val) {
        if (checkTurn(val)) {
            Player p = Bukkit.getPlayer(player);
            Player t = Bukkit.getPlayer(target);
            ItemStack edge = InventoryUtil.createItemStack(Material.IRON_FENCE, " ");
            ItemStack on = InventoryUtil.createItemStack(Material.REDSTONE_BLOCK, header + "§b베팅 상태", " ", " §8-  §f베팅을 완료 하셨습니다.", " ");
            if (turn) {
                p.getOpenInventory().setItem(1, on);
                t.getOpenInventory().setItem(1, on);
                for (int i = 21; i < 26; i++)
                    p.getOpenInventory().getItem(i).setType(Material.BOOK);
                p.getOpenInventory().setItem(32, edge);
                turn = false;
            }
            else {
                stopGame();
            }
        }
        else {
            Bukkit.getPlayer(val).sendMessage(header + "§c상대가 베팅이 종료될때까지 기다려 주세요.");
        }
    }

    public void stopGame() {
        Player p = Bukkit.getPlayer(player);
        Player t = Bukkit.getPlayer(target);
        DeckType playerType = checkDeck(player);
        DeckType targetType = checkDeck(target);
        InventoryUtil.clearGUI(p);
        InventoryUtil.clearGUI(t);
        ItemStack playerResult = InventoryUtil.createItemStack(Material.PAPER, header + "§b" + p.getName() + "§f님의 패", " ", " §8-  §f" + playerType.toString(), " ");
        ItemStack targetResult = InventoryUtil.createItemStack(Material.PAPER, header + "§c" + t.getName() + "§f님의 패", " ", " §8-  §f" + targetType.toString(), " ");
        p.getOpenInventory().setItem(11, playerResult);
        p.getOpenInventory().setItem(15, targetResult);
        t.getOpenInventory().setItem(11, targetResult);
        t.getOpenInventory().setItem(15, playerResult);
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            if (GameData.getInstance().isPlaying(player) && GameData.getInstance().isPlaying(target)) {
                if (playerType.getValue() > targetType.getValue()) {
                    p.sendTitle(header, "§f상대보다 수치가 높아 승리하셨습니다! §b흭득 금액 §7: §6" + MAX_BET * 2 + "§f원", 5, 50, 5);
                    t.sendTitle(header, "§f패배하셨습니다.. ", 5, 50, 5);
                    EconomyAPI.getInstance().giveMoney(p, MAX_BET * 2);
                    GameData.getInstance().stopGame(player);
                    GameData.getInstance().stopGame(target);
                    PokerData.getInstance().remove(player);
                    GambleLogger.getInstance().addLog(p.getName() + "님이 포커 승리" + t.getName() + "님이 패배하여 " + MAX_BET * 2 + "원 흭득");
                    p.closeInventory();
                    t.closeInventory();
                }
                else if (playerType.getValue() == targetType.getValue()) {
                    p.sendTitle(header, "§f비겼습니다!", 5, 50, 5);
                    t.sendTitle(header, "§f비겼습니다!", 5, 50, 5);
                    EconomyAPI.getInstance().giveMoney(p, MAX_BET);
                    EconomyAPI.getInstance().giveMoney(t, MAX_BET);
                    GameData.getInstance().stopGame(player);
                    GameData.getInstance().stopGame(target);
                    PokerData.getInstance().remove(player);
                    GambleLogger.getInstance().addLog(p.getName() + "님이 " + t.getName() + "님과 포커를 하여 무승부");
                    p.closeInventory();
                    t.closeInventory();
                }
                else {
                    t.sendTitle(header, "§f상대보다 수치가 높아 승리하셨습니다! §b흭득 금액 §7: §6" + MAX_BET * 2 + "§f원", 5, 50, 5);
                    p.sendTitle(header, "§f패배하셨습니다.. ", 5, 50, 5);
                    EconomyAPI.getInstance().giveMoney(t, MAX_BET * 2);
                    GambleLogger.getInstance().addLog(t.getName() + "님이 포커 승리" + p.getName() + "님이 패배하여 " + MAX_BET * 2 + "원 흭득");
                    GameData.getInstance().stopGame(player);
                    GameData.getInstance().stopGame(target);
                    PokerData.getInstance().remove(player);
                    p.closeInventory();
                    t.closeInventory();
                }
                if (playerType == DeckType.RoyalStraightFlush || targetType == DeckType.RoyalStraightFlush)
                    Bukkit.broadcastMessage(header + "§f누군가가 포커에서 로얄 스트레이트 플러쉬를 띄우셨습니다.");
            }
        }, 40L);
    }

    public void quitGame(UUID data) {
        if (data.equals(player)) {
            Player t = Bukkit.getPlayer(target);
            t.sendTitle(header, "§f포커 도중 상대방이 GUI를 닫아 도박이 중단 되었습니다.", 5, 50, 5);
            t.playSound(t.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
            EconomyAPI.getInstance().giveMoney(t, MAX_BET * 2);
            PokerData.getInstance().remove(player);
            GameData.getInstance().stopGame(player);
            GameData.getInstance().stopGame(target);
            t.closeInventory();
        }
        else {
            Player t = Bukkit.getPlayer(player);
            t.sendTitle(header, "§f포커 도박 도중 상대방이 GUI를 닫아 도박이 중단 되었습니다", 5, 50, 5);
            t.playSound(t.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3.0f, 1.0f);
            EconomyAPI.getInstance().giveMoney(t, MAX_BET * 2);
            PokerData.getInstance().remove(player);
            GameData.getInstance().stopGame(player);
            GameData.getInstance().stopGame(target);
            t.closeInventory();
        }
    }

    public boolean checkTurn(UUID val) {
        return (player.equals(val) && turn) || (target.equals(val) && !turn);
    }

    public DeckType checkDeck(UUID data) {
        int i;
        boolean str = false;
        boolean royal = false;
        boolean flush = false;
        boolean four = false;
        boolean triple = false;
        boolean backStr = false;
        int pairCount = 0;
        int straightCount = 0;
        int[] ranks = new int[15];
        ArrayList<Card> temp;
        if (data.equals(player))
            temp = new ArrayList<>(playerDeck);
        else
            temp = new ArrayList<>(targetDeck);

        temp.sort(Comparator.comparingInt(Card::getShape));
        Collections.reverse(temp);
        for (i = 1; i < 5; ++i)
            if (temp.get(i).getShape() == temp.get(i - 1).getShape() - 1)
                straightCount++;
        if (straightCount == 4) {
            str = true;
            if (temp.get(0).getShape() == 14)
                royal = true;
        }
        else if (straightCount == 3 && temp.get(0).getShape() == 14 && temp.get(1).getShape() == 5) {
            backStr = true;
        }
        for (i = 1; i < 5; i++)
            if (temp.get(i).getColor() != temp.get(i - 1).getColor())
                break;
        if (i == 5)
            flush = true;
        for (i = 0; i < 5; i++)
            ranks[temp.get(i).getShape()] += 1;
        for (int val : ranks) {
            if (val == 2)
                pairCount++;
            else if (val == 3)
                triple = true;
            else if (val == 4)
                four = true;
        }

        if (royal && flush)
            return DeckType.RoyalStraightFlush;
        else if (backStr && flush)
            return DeckType.BackStraightFlush;
        else if (str && flush)
            return DeckType.StraightFlush;
        else if (four)
            return DeckType.FourCard;
        else if (triple && pairCount == 1)
            return DeckType.FullHouse;
        else if (flush)
            return DeckType.Flush;
        else if (royal)
            return DeckType.Mountain;
        else if (backStr)
            return DeckType.BackStraight;
        else if (str)
            return DeckType.Straight;
        else if (triple)
            return DeckType.Triple;
        else if (pairCount == 2)
            return DeckType.TwoPair;
        else if (pairCount == 1)
            return DeckType.OnePair;
        else
            return DeckType.Top;

    }
}
