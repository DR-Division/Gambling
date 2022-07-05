package com.division.listener;

import com.division.data.DataManager;
import com.division.data.GameData;
import com.division.file.GambleLogger;
import com.division.game.Game;
import com.division.game.gambles.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    private String header;

    public InventoryCloseListener() {
        header = DataManager.getInstance().getHeader();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        String title = event.getInventory().getTitle();
        if (title.contains("슬롯머신")) {
            if (GameData.getInstance().isPlaying(p.getUniqueId())) {
                //해당 if에 걸릴경우 자신이 스스로 종료한것.
                Game current = GameData.getInstance().getData(p.getUniqueId()).getCurrent();
                if (current instanceof SlotMachine) {
                    SlotMachine machine = (SlotMachine) current;
                    if (machine.isRunning() && GameData.getInstance().isPlaying(p.getUniqueId())) {
                        p.sendTitle(header, ChatColor.WHITE + "슬롯머신 사용도중 GUI를 닫아 슬롯머신이 강제종료 되었습니다.", 5, 50, 5);
                        GambleLogger.getInstance().addLog(p.getName() + "님이 슬롯머신 사용 도중 GUI를 닫음");
                        p.playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 0.5f, 1.0f);
                        machine.stop();
                    }
                }
                GameData.getInstance().stopGame(p.getUniqueId());
            }
        }
        else if (title.contains("주사위")) {
            if (GameData.getInstance().isPlaying(p.getUniqueId())) {
                Game current = GameData.getInstance().getData(p.getUniqueId()).getCurrent();
                if (current instanceof Dice) {
                    Dice dice = (Dice) current;
                    if (dice.isPlaying() && GameData.getInstance().isPlaying(p.getUniqueId())) {
                        p.sendTitle(header, "§f주사위 도박 도중 GUI창을 닫아 주사위 도박이 중단 되었습니다.", 5, 50, 5);
                        GambleLogger.getInstance().addLog(p.getName() + "님이 주사위 도박 도중 GUI를 닫음");
                        p.playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 3.0f, 1.0f);
                    }
                }
                GameData.getInstance().stopGame(p.getUniqueId());
            }
        }
        else if (title.contains("블랙잭")) {
            if (GameData.getInstance().isPlaying(p.getUniqueId())) {
                Game current = GameData.getInstance().getData(p.getUniqueId()).getCurrent();
                if (current instanceof Blackjack) {
                    p.sendTitle(header, "§f블랙잭 도중 GUI창을 닫아 블랙잭이 중단 되었습니다.", 5, 50, 5);
                    GambleLogger.getInstance().addLog(p.getName() + "님이 블랙잭 도중 GUI를 닫음");
                    p.playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 3.0f, 1.0f);
                }
                GameData.getInstance().stopGame(p.getUniqueId());
            }
        }
        else if (title.contains("룰렛")) {
            if (GameData.getInstance().isPlaying(p.getUniqueId())) {
                Game current = GameData.getInstance().getData(p.getUniqueId()).getCurrent();
                if (current instanceof Roulette) {
                    p.sendTitle(header, "§f룰렛 도중 GUI창을 닫아 룰렛이 중단 되었습니다.", 5, 50, 5);
                    p.playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 3.0f, 1.0f);
                    GambleLogger.getInstance().addLog(p.getName() + "님이 룰렛 도중 GUI를 닫음");
                }
                GameData.getInstance().stopGame(p.getUniqueId());
            }
        }
        else if (title.contains("동전")) {
            if (GameData.getInstance().isPlaying(p.getUniqueId())) {
                Game current = GameData.getInstance().getData(p.getUniqueId()).getCurrent();
                if (current instanceof Coin) {
                    Coin coin = (Coin) current;
                    if (coin.isPlaying()) {
                        p.sendTitle(header, "§f동전 도박 도중 GUI창을 닫아 도박이 중단 되었습니다.", 5, 50, 5);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 3.0f, 1.0f);
                        GambleLogger.getInstance().addLog(p.getName() + "님이 동전 도박 도중 GUI를 닫음");
                    }
                }
                GameData.getInstance().stopGame(p.getUniqueId());
            }
        }
        else if (title.contains("인디언포커")) {
            if (GameData.getInstance().isPlaying(p.getUniqueId())) {
                Game current = GameData.getInstance().getData(p.getUniqueId()).getCurrent();
                if (current instanceof Indian) {
                    Indian indian = (Indian) current;
                    p.sendTitle(header, "§f인디언 포커 도중 GUI창을 닫아 도박이 중단 되었습니다.", 5, 50, 5);
                    p.playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 3.0f, 1.0f);
                    indian.quitGame(p.getUniqueId());
                    GambleLogger.getInstance().addLog(p.getName() + "님이 인디언 포커 도중 GUI를 닫음");
                }
            }
            GameData.getInstance().stopGame(p.getUniqueId());
        }
        else if (title.contains("카드 도박")) {
            if (GameData.getInstance().isPlaying(p.getUniqueId())) {
                Game current = GameData.getInstance().getData(p.getUniqueId()).getCurrent();
                if (current instanceof CardGamble) {
                    CardGamble cardGamble = (CardGamble) current;
                    p.sendTitle(header, "§f카드 도박 도중 GUI창을 닫아 도박이 중단 되었습니다.", 5, 50, 5);
                    p.playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 3.0f, 1.0f);
                    cardGamble.quitGame(p.getUniqueId());
                    GambleLogger.getInstance().addLog(p.getName() + "님이 카드 도박 도중 GUI를 닫음");
                }
            }
            GameData.getInstance().stopGame(p.getUniqueId());
        }
        else if (title.contains("포커")) {
            if (GameData.getInstance().isPlaying(p.getUniqueId())) {
                Game current = GameData.getInstance().getData(p.getUniqueId()).getCurrent();
                if (current instanceof Poker) {
                    Poker poker = (Poker) current;
                    p.sendTitle(header, "§f포커 도중 GUI창을 닫아 도박이 중단 되었습니다.", 5, 50, 5);
                    p.playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 3.0f, 1.0f);
                    poker.quitGame(p.getUniqueId());
                    GambleLogger.getInstance().addLog(p.getName() + "님이 포커 도중 GUI를 닫음");
                }
            }
            GameData.getInstance().stopGame(p.getUniqueId());
        }
        else if (title.contains("주식")) {
            GameData.getInstance().stopGame(p.getUniqueId());
        }
    }
}
