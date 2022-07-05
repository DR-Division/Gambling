package com.division.command;

import com.division.Gamble;
import com.division.data.*;
import com.division.file.ConfigManager;
import com.division.file.GambleLogger;
import com.division.game.gambles.*;
import com.division.util.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GambleCommand implements CommandExecutor {

    private Gamble Plugin;
    private String header;

    public GambleCommand(Gamble Plugin) {
        this.Plugin = Plugin;
        header = DataManager.getInstance().getHeader();
    }

    public enum DenyReason {
        HAND,
        SLEEP,
        JOIN,
        NONE,
        BLACK
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] arg) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            DenyReason reason = checkPlayer(p);
            if (reason != DenyReason.NONE)
                sendDenyMessage(p, reason);
            else if (arg.length == 0) {
                if (p.isOp())
                    p.sendMessage(header + "§f/도박 <슬롯머신/주사위/블랙잭/룰렛/동전/인디언포커/카드/포커/주식/블랙리스트/저장/리로드>");
                else
                    p.sendMessage(header + "§f/도박 <슬롯머신/주사위/블랙잭/룰렛/동전/인디언포커/카드/포커/주식>");
                p.sendMessage(header + "§c게임의 GUI가 닫혀 돈이 증발하는 문제는 개인의 잘못으로 간주하여 복구해드리지 않습니다.");
            }
            else {
                if (checkParameter(arg[0])) {
                    if (arg[0].equalsIgnoreCase("슬롯머신")) {
                        new SlotMachine(p.getUniqueId(), Plugin);
                    }
                    else if (arg[0].equalsIgnoreCase("주사위")) {
                        if (arg.length == 1) {
                            p.sendMessage(header + "§f/도박 주사위 <룰/베팅> <금액>");
                        }
                        else {
                            if (arg[1].equalsIgnoreCase("룰") || arg[1].equalsIgnoreCase("베팅")) {
                                if (arg[1].equalsIgnoreCase("룰")) {
                                    p.sendMessage(" ");
                                    p.sendMessage(header + "§f/도박 주사위 베팅 <금액> 명령어로 게임을 시작합니다.");
                                    p.sendMessage(header + "§f게임은 컴퓨터와 1대1로 진행되며 룰을 직접 선택할 수 있습니다.");
                                    p.sendMessage(header + "§b컴퓨터는 베팅액의 0.5 ~ 1.5배를 추가로 베팅합니다.");
                                    p.sendMessage(header + "§f승자는 게임에 베팅된 총 금액을 가져가게 됩니다.");
                                    p.sendMessage(header + "§f서로 비긴경우 총 베팅된 금액의 절반을 가져가게 됩니다.");
                                    p.sendMessage(header + "§f룰은 하이 / 미드 / 로우로 나뉩니다.");
                                    p.sendMessage(header + "§e로우 §7: §f주사위를 던졌을때 낮은 숫자를 가진 사람이 승리합니다.");
                                    p.sendMessage(header + "§6미드 §7: §f주사위를 던졌을때 50에 가까운 숫자를 가진 사람이 승리합니다.");
                                    p.sendMessage(header + "§c하이 §7: §f주사위를 던졌을때 높은 숫자를 가진 사람이 승리합니다");
                                    p.sendMessage(header + "§f주사위는 1 ~ 99 사이의 랜덤한 숫자가 나옵니다.");
                                    p.sendMessage(" ");
                                }
                                else {
                                    if (arg.length == 2) {
                                        p.sendMessage(header + "§f베팅 금액을 적어주세요.");
                                    }
                                    else {
                                        if (checkIntParameter(arg[2])) {
                                            int value = Integer.parseInt(arg[2]);
                                            if (EconomyAPI.getInstance().getMoney(p) < value) {
                                                p.sendMessage(header + "§c돈이 부족합니다..");
                                            }
                                            else if (value < DataManager.getInstance().getDiceMin()) {
                                                p.sendMessage(header + "§c최소 베팅금액보다 적습니다. §7[ §6" + DataManager.getInstance().getDiceMin() + "§f원 §7]");
                                            }
                                            else if (value > DataManager.getInstance().getDiceMax()) {
                                                p.sendMessage(header + "§c최대 베팅금액보다 많습니다. §7[ §6" + DataManager.getInstance().getDiceMax() + "§f원 §7]");
                                            }
                                            else {
                                                GameData.getInstance().stopGame(p.getUniqueId()); //버그 방지를 위한 값 임시 제거
                                                new Dice(p.getUniqueId(), value, Plugin);
                                            }
                                        }
                                        else
                                            p.sendMessage(header + "§c숫자만 입력해주세요!");
                                    }
                                }
                            }
                        }

                    }

                    else if (arg[0].equalsIgnoreCase("블랙잭")) {
                        if (arg.length == 1) {
                            p.sendMessage(header + "§f/도박 블랙잭 <룰/베팅> <금액>");
                        }
                        else {
                            if (arg[1].equalsIgnoreCase("룰") || arg[1].equalsIgnoreCase("베팅")) {
                                if (arg[1].equalsIgnoreCase("룰")) {
                                    p.sendMessage(" ");
                                    p.sendMessage(header + "§f/도박 블랙잭 베팅 <금액> 명령어로 블랙잭 게임을 시작합니다.");
                                    p.sendMessage(header + "§f컴퓨터는 딜러로, 추가적인 베팅을 진행하지 않습니다.");
                                    p.sendMessage(header + "§b일반 승리시 베팅한 금액 만큼, 블랙잭시 베팅한 금액의 50%를 추가로 받습니다.");
                                    p.sendMessage(header + "§a기본 룰은 지급 받은 카드의 숫자의 합을 21에 가깝게 유지하면 됩니다.");
                                    p.sendMessage(header + "§cHit 버튼을 우클릭 하게 될경우 베팅액의 100%를 추가로 베팅하며, 한장만 받고 Stay가 됩니다.");
                                    p.sendMessage(header + "§4A는 1 또는 11, Q/J/K는 10으로 취급합니다.");
                                    p.sendMessage(header + "§f카드는 딜러와 플레이어 둘다 2장씩 지급되며, 원하는 만큼 뽑으실 수 있습니다.");
                                    p.sendMessage(header + "§c단, 카드의 숫자합이 21을 넘어가는 순간 패배하게 됩니다. (버스트)");
                                    p.sendMessage(header + "§f처음 지급받은 2개의 카드의 숫자합이 21이라면 블랙잭 승리가 됩니다.");
                                    p.sendMessage(header + "§f딜러가 블랙잭 승리일경우 절대 승리할 수 없습니다.");
                                    p.sendMessage(header + "§c딜러의 처음 공개된 카드가 A / Q / J / K인경우 insurance(보험)을 들수 있습니다.");
                                    p.sendMessage(header + "§f보험의 경우 베팅금의 50%를 제거 하는대신 딜러가 블랙잭인 경우");
                                    p.sendMessage(header + "§f보험 지불액의 2배 (원금)을 받게 됩니다. (딜러 A공개시 활성화)");
                                    p.sendMessage(header + "§fInsurance가 활성화 되어있을시 당신의 패 (종이)를 클릭해서 거부할 수 있습니다.");
                                    p.sendMessage(header + "§f그 외의 경우 딜러의 카드 숫자합이 플레이어 보다 높은경우 딜러 승리,");
                                    p.sendMessage(header + "§f플레이어 보다 낮은경우 플레이어 승리가 되며, 비길경우 베팅한 금액을 그대로 지급합니다.");
                                    p.sendMessage(header + "§b딜러의 카드는 플레이어의 차례가 끝날때 공개하며, 버스트가 발생할 수도 있습니다.");
                                    p.sendMessage(" ");
                                }
                                else if (arg[1].equalsIgnoreCase("베팅")) {
                                    if (arg.length == 2) {
                                        p.sendMessage(header + "§f베팅 금액을 적어주세요. ");
                                    }
                                    else {
                                        if (checkIntParameter(arg[2])) {
                                            int value = Integer.parseInt(arg[2]);
                                            if (EconomyAPI.getInstance().getMoney(p) < value) {
                                                p.sendMessage(header + "§c돈이 부족합니다..");
                                            }
                                            else if (value < DataManager.getInstance().getBlackjackMin()) {
                                                p.sendMessage(header + "§c최소 베팅금액보다 적습니다. §8[ §6" + DataManager.getInstance().getBlackjackMin() + "§f원 §8]");
                                            }
                                            else if (value > DataManager.getInstance().getBlackjackMax()) {
                                                p.sendMessage(header + "§c최대 베팅금액보다 많습니다. §8[ §6" + DataManager.getInstance().getBlackjackMax() + "§f원 §8]");
                                            }
                                            else {
                                                EconomyAPI.getInstance().steelMoney(p, value);
                                                new Blackjack(p.getUniqueId(), Plugin, value);
                                                GambleLogger.getInstance().addLog(p.getName() + "님이 블랙잭에 " + value + "원 베팅");
                                            }
                                        }
                                        else
                                            p.sendMessage(header + "§c숫자만 입력해주세요!");
                                    }
                                }
                            }
                        }
                    }
                    else if (arg[0].equalsIgnoreCase("룰렛")) {
                        if (arg.length == 1)
                            p.sendMessage(header + "§f/도박 룰렛 <설명/베팅> <금액>");
                        else {
                            if (arg[1].equalsIgnoreCase("설명") || arg[1].equalsIgnoreCase("베팅")) {
                                if (arg[1].equalsIgnoreCase("설명")) {
                                    p.sendMessage(" ");
                                    p.sendMessage(header + "§b/도박 룰렛 베팅 <금액> 명령어로 베팅 후 게임을 시작합니다.");
                                    p.sendMessage(header + "§f가운데에 위치한 버튼을 누를 시 룰렛이 돌아갑니다.");
                                    p.sendMessage(header + "§b3개는 꽝, 나머지 3개는 배율이 적혀있습니다.");
                                    p.sendMessage(header + "§c룰렛이 멈췄을때 붉게 표시된 부분의 보상을 흭득합니다.");
                                    p.sendMessage(header + "§4배율이 적힌 부분에 멈췄을때, 베팅액 x 배율의 금액을 흭득합니다!");
                                    p.sendMessage(" ");
                                }
                                else {
                                    if (arg.length == 2) {
                                        p.sendMessage(header + "§f베팅 금액을 적어주세요. ");
                                    }
                                    else {
                                        if (checkIntParameter(arg[2])) {
                                            int value = Integer.parseInt(arg[2]);
                                            if (EconomyAPI.getInstance().getMoney(p) < value) {
                                                p.sendMessage(header + "§c돈이 부족합니다..");
                                            }
                                            else if (value < DataManager.getInstance().getRouletteMin()) {
                                                p.sendMessage(header + "§c최소 베팅금액보다 적습니다. §8[ §6" + DataManager.getInstance().getRouletteMin() + "§f원 §8]");
                                            }
                                            else if (value > DataManager.getInstance().getRouletteMax()) {
                                                p.sendMessage(header + "§c최대 베팅금액보다 많습니다. §8[ §6" + DataManager.getInstance().getRouletteMax() + "§f원 §8]");
                                            }
                                            else {
                                                EconomyAPI.getInstance().steelMoney(p, value);
                                                new Roulette(p.getUniqueId(), Plugin, value);
                                                GambleLogger.getInstance().addLog(p.getName() + " 님이 룰렛에 " + value + "원 베팅");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (arg[0].equalsIgnoreCase("동전")) {
                        new Coin(p.getUniqueId(), Plugin);
                    }
                    else if (arg[0].equalsIgnoreCase("인디언포커")) {
                        if (arg.length == 1)
                            p.sendMessage(header + "§f/도박 인디언포커 <설명/수락/거부/시작>");
                        else if (arg.length == 2 && arg[1].equalsIgnoreCase("설명") || arg[1].equalsIgnoreCase("시작") || arg[1].equalsIgnoreCase("수락") || arg[1].equalsIgnoreCase("거부")) {
                            if (arg[1].equalsIgnoreCase("설명")) {
                                p.sendMessage(" ");
                                p.sendMessage(header + "§b/도박 인디언포커 시작 <대상> <최대금액> 명령어로 대상에게 1대1 인디언 포커 요청을 합니다.");
                                p.sendMessage(header + "§f두 사람 모두가 게임 불참상태 및 최대금액의 베팅액을 가지고 있어야 합니다.");
                                p.sendMessage(header + "§c게임 수락시 양측이 최대금액의 5%를 보증금으로 베팅하고 시작합니다.");
                                p.sendMessage(header + "§c중간에 GUI를 닫는등 게임이 강제 종료될경우 나간사람의 배팅액은 상대에게 들어갑니다.");
                                p.sendMessage(header + "§f게임이 시작될경우 상자 아이템을 클릭할경우 상대방의 패를 확인할 수 있습니다.");
                                p.sendMessage(header + "§c먼저 게임 요청자가 상대방의 패를 보고 베팅을 진행하게 됩니다.");
                                p.sendMessage(header + "§f레드스톤 램프인 경우 상대방의 차례, 레드스톤 블럭인경우 자신의 차례 입니다.");
                                p.sendMessage(header + "§f상대방의 배팅액은 레드스톤 램프 혹은 레드스톤 블럭 또는 베팅 버튼을 통해 확인할 수 있습니다.");
                                p.sendMessage(header + "§cBet(베팅)버튼을 누를경우 최대금액의 5%를 베팅하게 됩니다.");
                                p.sendMessage(header + "§bDie(다이)버튼을 누를경우 베팅을 포기하고 게임을 종료하게되며 베팅액은 살아있는 사람에게 돌아갑니다.");
                                p.sendMessage(header + "§f베팅 버튼을 눌러 충분히 베팅이 끝났으면 베팅 버튼을 우클릭해 차례를 끝냅니다.");
                                p.sendMessage(header + "§f베팅 버튼 1번 클릭당 최대 베팅금액의 5%씩 베팅이 됩니다. 만약 최대금액까지 남은금액이 5% 미만일경우");
                                p.sendMessage(header + "§f최대금액에 맞춰 베팅이 진행됩니다. (10%가 50원, 최대금액까지 남은 금액 30원 -> 30원 베팅)");
                                p.sendMessage(header + "§f게임 요청자가 베팅을 완료할경우 상대방의 차례가 됩니다.");
                                p.sendMessage(header + "§f상대방은 이전 차례의 사람이 베팅한 금액 만큼 배팅하고 오픈(콜) 상태로 베팅을 종료하거나");
                                p.sendMessage(header + "§f상대방의 베팅액보다 더 많은 액수를 베팅한다음 베팅을 종료하여 베팅을 이어나갈 수 있습니다.");
                                p.sendMessage(header + "§b패는 1~10사이의 숫자로 결정되며, 상대방보다 자신이 높은 숫자를 가지고 있으면 승리합니다.");
                                p.sendMessage(header + "§f만약 베팅액이 최대금액에 다다랐을경우 추가 베팅은 불가능하며 베팅 종료를 할 수 있습니다.");
                                p.sendMessage(header + "§4본 도박 시스템을 이용하여 고의적인 어뷰징등과 같은 행위 발생시 명령어 악용으로 처벌될 수 있습니다.");

                            }
                            else if (arg[1].equalsIgnoreCase("수락")) {
                                IndianData.RequestType type = IndianData.getInstance().findPlayer(p.getUniqueId());
                                switch (type) {
                                    case REQUESTER:
                                        p.sendMessage(header + "§b이미 다른사람에게 요청중인 게임이 존재합니다.");
                                        break;
                                    case TARGET:
                                        Player target = Bukkit.getPlayer(IndianData.getInstance().getTarget(p.getUniqueId()));
                                        if (target == null) {
                                            p.sendMessage(header + "§c해당 플레이어가 온라인이 아니므로 요청을 삭제합니다.");
                                            IndianData.getInstance().remove(p.getUniqueId());
                                        }
                                        else if (checkPlayer(target) != DenyReason.NONE) {
                                            p.sendMessage(header + "§c대상 플레이어가 게임에 참여할 수 있는 상태가 아닙니다.");
                                            IndianData.getInstance().remove(p.getUniqueId());
                                        }
                                        else if (EconomyAPI.getInstance().getMoney(p) < IndianData.getInstance().getMoney(target.getUniqueId()) || EconomyAPI.getInstance().getMoney(target) < IndianData.getInstance().getMoney(target.getUniqueId())) {
                                            p.sendMessage(header + "§c해당 플레이어 혹은 당신의 돈이 모자라므로 요청이 삭제됩니다.");
                                            IndianData.getInstance().remove(p.getUniqueId());
                                        }
                                        else {
                                            int val = IndianData.getInstance().getMoney(target.getUniqueId()) / 20;
                                            EconomyAPI.getInstance().steelMoney(p, val);
                                            EconomyAPI.getInstance().steelMoney(target, val);
                                            new Indian(target.getUniqueId(), p.getUniqueId(), Plugin);
                                            GambleLogger.getInstance().addLog(p.getName() + "님이 " + target.getName() + " 의 인디언포커 요청 수락");
                                        }
                                        break;
                                    case NONE:
                                        p.sendMessage(header + "§c현재 들어온 요청이 없습니다.");
                                        break;
                                }
                            }
                            else if (arg[1].equalsIgnoreCase("거부")) {
                                if (IndianData.getInstance().findPlayer(p.getUniqueId()) != IndianData.RequestType.NONE) {
                                    IndianData.getInstance().remove(p.getUniqueId());
                                    p.sendMessage(header + "§c모든 요청을 제거하였습니다.");
                                    GambleLogger.getInstance().addLog(p.getName() + "님이 인디언포커 요청 거부");
                                }
                                else
                                    p.sendMessage(header + "§c현재 들어온 요청이 없습니다.");
                            }
                            else {
                                if (arg.length == 4) {
                                    if (checkIntParameter(arg[3])) {
                                        int value = Integer.parseInt(arg[3]);
                                        if (value < DataManager.getInstance().getIndianMin())
                                            p.sendMessage(header + "§c최소금액보다 적습니다.. §7[ §f" + DataManager.getInstance().getIndianMin() + " §7]");
                                        else if (value > DataManager.getInstance().getIndianMax())
                                            p.sendMessage(header + "§c최대금액보다 많습니다.. §7[ §f" + DataManager.getInstance().getIndianMax() + " §7]");
                                        else {
                                            Player target = Bukkit.getPlayer(arg[2]);
                                            if (target == null || p.getName().equalsIgnoreCase(target.getName()))
                                                p.sendMessage(header + "§4해당 플레이어는 온라인이 아닙니다..");
                                            else {
                                                if (checkPlayer(target) != DenyReason.NONE)
                                                    p.sendMessage(header + "§c대상 플레이어가 게임에 참여할 수 있는 상태가 아닙니다.");
                                                else if (EconomyAPI.getInstance().getMoney(target) < value || EconomyAPI.getInstance().getMoney(p) < value)
                                                    p.sendMessage(header + "§c대상 플레이어나 플레이어가 최대 금액을 보유하고 있지 않습니다.");
                                                else {
                                                    if (IndianData.getInstance().findPlayer(target.getUniqueId()) != IndianData.RequestType.NONE || IndianData.getInstance().findPlayer(p.getUniqueId()) != IndianData.RequestType.NONE)
                                                        p.sendMessage(header + "§c이미 게임을 요청한 사람이 존재합니다.");
                                                    else {
                                                        IndianData.getInstance().addValue(p.getUniqueId(), target.getUniqueId(), value);
                                                        p.sendMessage(header + "§b" + target.getName() + " §f님에게 §c인디언 포커 §f요청을 넣었습니다.");
                                                        target.sendMessage(header + "§b" + p.getName() + " §f님이 당신에게 인디언 포커를 요청하였습니다. §7[ §6" + value + "§f원 §7]");
                                                        target.sendMessage(header + "§c수락을 원하시면 /도박 인디언포커 수락, §b거절을 원하시면 /도박 인디언포커 거부");
                                                        GambleLogger.getInstance().addLog(p.getName() + "님이 " + target.getName() + " 에게 인디언포커 요청");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                        p.sendMessage(header + "§c숫자만 입력하세요..");
                                }
                                else
                                    p.sendMessage(header + "§f/도박 인디언포커 시작 <대상> <금액>");
                            }
                        }
                    }
                    else if (arg[0].equalsIgnoreCase("카드")) {
                        if (arg.length == 1) {
                            p.sendMessage(header + "§f/도박 카드 <설명/수락/거부/시작> <대상> <금액>");
                        }
                        else if (arg.length == 2 && arg[1].equalsIgnoreCase("설명") || arg[1].equalsIgnoreCase("시작") || arg[1].equalsIgnoreCase("수락") || arg[1].equalsIgnoreCase("거부")) {
                            if (arg[1].equalsIgnoreCase("설명")) {
                                p.sendMessage(" ");
                                p.sendMessage(header + "§b/도박 카드 시작 <대상> <최대금액> 명령어로 대상에게 1대1 카드 도박 요청을 합니다.");
                                p.sendMessage(header + "§f게임 시작후 자신의 턴에 3장의 카드중 하나를 고를 수 있습니다.");
                                p.sendMessage(header + "§b카드는 King(왕), Citizen(시민), Slave(노예) 카드가 있습니다.");
                                p.sendMessage(header + "§f카드를 고른후 상대방의 턴으로 넘어가게 되며, 위에 셜커상자의 색이 붉은색일경우 상대 턴, 초록색인경우 자신의 턴 입니다.");
                                p.sendMessage(header + "§f양측이 카드를 모두 선택하셨을경우 카드의 우열을 가리게 됩니다.");
                                p.sendMessage(header + "§cKing(왕) 카드는 Citizen(시민) 카드를 이기며, Citizen(시민) 카드는 Slave(노예) 카드를 이깁니다.");
                                p.sendMessage(header + "§c그리고 Slave(노예) 카드는 King(왕) 카드를 이기게 됩니다. [ 왕 > 시민 > 노예 > 왕 ]");
                                p.sendMessage(header + "§cSlave(노예)는 King(왕) 카드를 이기지만, 시민(Citizen)의 카드는 이기지 못합니다.");
                                p.sendMessage(header + "§f왕은 보, 시민은 바위, 노예는 가위 인 가위바위보라고 생각하시면 됩니다.");
                                p.sendMessage(header + "§f승자는 배팅액을 전부 가져가고, 비길시 자신의 배팅액만 가져갑니다.");
                                p.sendMessage(" ");
                            }
                            else if (arg[1].equalsIgnoreCase("수락")) {
                                CardData.ResponseType type = CardData.getInstance().findPlayer(p.getUniqueId());
                                switch (type) {
                                    case REQUESTER:
                                        p.sendMessage(header + "§b이미 다른사람에게 요청중인 게임이 존재합니다.");
                                        break;
                                    case TARGET:
                                        Player target = Bukkit.getPlayer(CardData.getInstance().getTarget(p.getUniqueId()));
                                        if (target == null) {
                                            p.sendMessage(header + "§c해당 플레이어가 온라인이 아니므로 요청을 삭제합니다.");
                                            CardData.getInstance().remove(p.getUniqueId());
                                        }
                                        else if (checkPlayer(target) != DenyReason.NONE) {
                                            p.sendMessage(header + "§c대상 플레이어가 게임에 참여할 수 있는 상태가 아닙니다.");
                                            CardData.getInstance().remove(p.getUniqueId());
                                        }
                                        else if (EconomyAPI.getInstance().getMoney(p) < CardData.getInstance().getMoney(target.getUniqueId()) || EconomyAPI.getInstance().getMoney(target) < CardData.getInstance().getMoney(target.getUniqueId())) {
                                            p.sendMessage(header + "§c해당 플레이어 혹은 당신의 돈이 모자라므로 요청이 삭제됩니다.");
                                            CardData.getInstance().remove(p.getUniqueId());
                                        }
                                        else {
                                            EconomyAPI.getInstance().steelMoney(p, CardData.getInstance().getMoney(target.getUniqueId()));
                                            EconomyAPI.getInstance().steelMoney(target, CardData.getInstance().getMoney(target.getUniqueId()));
                                            new CardGamble(target.getUniqueId(), p.getUniqueId(), CardData.getInstance().getMoney(target.getUniqueId()), Plugin);
                                            GambleLogger.getInstance().addLog(p.getName() + "님이 " + target.getName() + "님의 카드도박 요청 수락");
                                        }
                                        break;
                                    case NONE:
                                        p.sendMessage(header + "§c현재 들어온 요청이 없습니다.");
                                        break;
                                }


                            }
                            else if (arg[1].equalsIgnoreCase("거부")) {
                                if (CardData.getInstance().findPlayer(p.getUniqueId()) != CardData.ResponseType.NONE) {
                                    CardData.getInstance().remove(p.getUniqueId());
                                    p.sendMessage(header + "§c모든 요청을 제거하였습니다.");
                                    GambleLogger.getInstance().addLog(p.getName() + "님이 카드도박 요청 취소");
                                }
                                else
                                    p.sendMessage(header + "§c현재 들어온 요청이 없습니다.");
                            }
                            else {
                                if (arg.length == 4) {
                                    if (checkIntParameter(arg[3])) {
                                        int value = Integer.parseInt(arg[3]);
                                        if (value < DataManager.getInstance().getCardMin())
                                            p.sendMessage(header + "§c최소금액보다 적습니다.. §7[ §f" + DataManager.getInstance().getCardMin() + " §7]");
                                        else if (value > DataManager.getInstance().getCardMax())
                                            p.sendMessage(header + "§c최대금액보다 많습니다.. §7[ §f" + DataManager.getInstance().getCardMax() + " §7]");
                                        else {
                                            Player target = Bukkit.getServer().getPlayer(arg[2]);
                                            if (target == null || p.getName().equalsIgnoreCase(target.getName()))
                                                p.sendMessage(header + "§4해당 플레이어는 온라인이 아닙니다..");
                                            else {
                                                if (checkPlayer(target) != DenyReason.NONE) {
                                                    p.sendMessage(header + "§c대상 플레이어가 게임에 참여할 수 있는 상태가 아닙니다.");
                                                }
                                                else if (EconomyAPI.getInstance().getMoney(p) < value || EconomyAPI.getInstance().getMoney(target) < value)
                                                    p.sendMessage(header + "§c대상 플레이어나 플레이어가 최대 금액을 보유하고 있지 않습니다.");
                                                else {
                                                    if (CardData.getInstance().findPlayer(p.getUniqueId()) != CardData.ResponseType.NONE || CardData.getInstance().findPlayer(target.getUniqueId()) != CardData.ResponseType.NONE)
                                                        p.sendMessage(header + "§c이미 게임을 요청한 사람이 존재합니다.");
                                                    else {
                                                        CardData.getInstance().addValue(p.getUniqueId(), target.getUniqueId(), value);
                                                        p.sendMessage(header + "§b" + target.getName() + "§f님에게 §c카드 도박 §f요청을 넣었습니다.");
                                                        target.sendMessage(header + "§b" + p.getName() + "§f님이 당신에게 카드 도박을 요청하였습니다. §7[ §6" + value + "§f원 §7]");
                                                        target.sendMessage(header + "§b수락을 원하시면 /도박 카드 수락, §b거절을 원하시면 /도박 카드 거부");
                                                        GambleLogger.getInstance().addLog(p.getName() + "님이 " + target.getName() + "에게 카드도박 요청");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                        p.sendMessage(header + "§4숫자만 입력하세요..");
                                }
                                else
                                    p.sendMessage(header + "§f/도박 카드 시작 <대상> <최대금액>");

                            }
                        }
                    }
                    else if (arg[0].equalsIgnoreCase("포커")) {
                        if (arg.length == 1) {
                            p.sendMessage(header + "§f/도박 포커 <설명/수락/거부/시작/족보> <대상> <금액>");
                        }
                        else if (arg.length == 2 && arg[1].equalsIgnoreCase("설명") || arg[1].equalsIgnoreCase("시작") || arg[1].equalsIgnoreCase("수락") || arg[1].equalsIgnoreCase("거부") || arg[1].equalsIgnoreCase("족보")) {
                            if (arg[1].equalsIgnoreCase("설명")) {
                                p.sendMessage(" ");
                                p.sendMessage(header + "§b/도박 포커 시작 <대상> <최대금액> 명령어로 대상에게 1대1 포커 요청을 합니다.");
                                p.sendMessage(header + "§f게임 시작후 카드 5장을 받게 되며 해당 카드마다 바꿀 수 있는 기회가 한번씩 주어집니다.");
                                p.sendMessage(header + "§f카드 도박과 마찬가지로 추가 베팅이 존재하지 않으며, 승자는 모든 베팅금을 가져갑니다.");
                                p.sendMessage(header + "§c규칙은 포커 족보대로 진행되며, 족보가 같은경우 카드간의 우열과 관계없이 무승부로 끝납니다.");
                                p.sendMessage(header + "§fGUI내에서 상대방 머리 옆에 블럭을 통해 상대방의 차례가 끝났는지 확인할 수 있습니다.");
                                p.sendMessage(header + "§f자세한건 /도박 포커 족보 명령어를 통해 확인할 수 있습니다.");
                                p.sendMessage(" ");
                            }
                            else if (arg[1].equalsIgnoreCase("수락")) {
                                PokerData.Result type = PokerData.getInstance().findPlayer(p.getUniqueId());
                                switch (type) {
                                    case REQUESTER:
                                        p.sendMessage(header + "§b이미 다른사람에게 요청중인 게임이 존재합니다.");
                                        break;
                                    case TARGET:
                                        Player target = Bukkit.getPlayer(PokerData.getInstance().getTarget(p.getUniqueId()));
                                        if (target == null) {
                                            p.sendMessage(header + "§c해당 플레이어가 온라인이 아니므로 요청을 삭제합니다.");
                                            PokerData.getInstance().remove(p.getUniqueId());
                                        }
                                        else if (checkPlayer(target) != DenyReason.NONE) {
                                            p.sendMessage(header + "§c대상 플레이어가 게임에 참여할 수 있는 상태가 아닙니다.");
                                            PokerData.getInstance().remove(p.getUniqueId());
                                        }
                                        else if (EconomyAPI.getInstance().getMoney(p) < PokerData.getInstance().getMoney(target.getUniqueId()) || EconomyAPI.getInstance().getMoney(target) < PokerData.getInstance().getMoney(target.getUniqueId())) {
                                            p.sendMessage(header + "§c해당 플레이어 혹은 당신의 돈이 모자라므로 요청이 삭제됩니다.");
                                            PokerData.getInstance().remove(p.getUniqueId());
                                        }
                                        else {
                                            EconomyAPI.getInstance().steelMoney(p, PokerData.getInstance().getMoney(target.getUniqueId()));
                                            EconomyAPI.getInstance().steelMoney(target, PokerData.getInstance().getMoney(target.getUniqueId()));
                                            new Poker(target.getUniqueId(), p.getUniqueId(), Plugin, PokerData.getInstance().getMoney(target.getUniqueId()));
                                            GambleLogger.getInstance().addLog(p.getName() + "님이 " + target.getName() + "님의 포커 요청 수락");
                                        }
                                        break;
                                    case NONE:
                                        p.sendMessage(header + "§c현재 들어온 요청이 없습니다.");
                                        break;
                                }


                            }
                            else if (arg[1].equalsIgnoreCase("거부")) {
                                if (PokerData.getInstance().findPlayer(p.getUniqueId()) != PokerData.Result.NONE) {
                                    PokerData.getInstance().remove(p.getUniqueId());
                                    p.sendMessage(header + "§c모든 요청을 제거하였습니다.");
                                    GambleLogger.getInstance().addLog(p.getName() + "님이 포커 요청 취소");
                                }
                                else
                                    p.sendMessage(header + "§c현재 들어온 요청이 없습니다.");
                            }
                            else if (arg[1].equalsIgnoreCase("족보")) {
                                p.sendMessage(" ");
                                p.sendMessage(header + "§4로얄 스트레이트 플러쉬 §7- §f같은 무늬 + 10, J, Q, K, A");
                                p.sendMessage(header + "§b백 스트레이트 플러쉬 §7- §f같은 무늬 + A, 2, 3, 4, 5");
                                p.sendMessage(header + "§a스트레이트 플러쉬 §7- §f같은 무늬 + 연속적인 값");
                                p.sendMessage(header + "§c포카드 §7- §f같은 값 4장");
                                p.sendMessage(header + "§b플러쉬 §7- §f전부다 같은 무늬");
                                p.sendMessage(header + "§b풀하우스 §7- §f같은 값 3장 + 같은 값 2장 (트리플 + 투페어)");
                                p.sendMessage(header + "§c마운틴 §7- §f10, J, Q, K, A");
                                p.sendMessage(header + "§b백 스트레이트 §7- §fA, 2, 3, 4, 5");
                                p.sendMessage(header + "§a트리플 §7- §f같은 값 3장");
                                p.sendMessage(header + "§c투페어 §7- §f같은 값 2장 + 같은 값 2장");
                                p.sendMessage(header + "§b원페어 §7- §f같은 값 2장");
                                p.sendMessage(header + "§4탑 §7- §f꽝");
                                p.sendMessage(" ");
                            }
                            else {
                                if (arg.length == 4) {
                                    if (checkIntParameter(arg[3])) {
                                        int value = Integer.parseInt(arg[3]);
                                        if (value < DataManager.getInstance().getPokerMin())
                                            p.sendMessage(header + "§c최소금액보다 적습니다.. §7[ §f" + DataManager.getInstance().getPokerMin() + " §7]");
                                        else if (value > DataManager.getInstance().getPokerMax())
                                            p.sendMessage(header + "§c최대금액보다 많습니다.. §7[ §f" + DataManager.getInstance().getPokerMax() + " §7]");
                                        else {
                                            Player target = Bukkit.getServer().getPlayer(arg[2]);
                                            if (target == null || p.getName().equalsIgnoreCase(target.getName()))
                                                p.sendMessage(header + "§4해당 플레이어는 온라인이 아닙니다..");
                                            else {
                                                if (checkPlayer(target) != DenyReason.NONE) {
                                                    p.sendMessage(header + "§c대상 플레이어가 게임에 참여할 수 있는 상태가 아닙니다.");
                                                }
                                                else if (EconomyAPI.getInstance().getMoney(p) < value || EconomyAPI.getInstance().getMoney(target) < value)
                                                    p.sendMessage(header + "§c대상 플레이어나 플레이어가 최대 금액을 보유하고 있지 않습니다.");
                                                else {
                                                    if (PokerData.getInstance().findPlayer(p.getUniqueId()) != PokerData.Result.NONE || PokerData.getInstance().findPlayer(target.getUniqueId()) != PokerData.Result.NONE)
                                                        p.sendMessage(header + "§c이미 게임을 요청한 사람이 존재합니다.");
                                                    else {
                                                        PokerData.getInstance().addValue(p.getUniqueId(), target.getUniqueId(), value);
                                                        p.sendMessage(header + "§b" + target.getName() + "§f님에게 §c포커 §f요청을 넣었습니다.");
                                                        target.sendMessage(header + "§b" + p.getName() + "§f님이 당신에게 포커를 요청하였습니다. §7[ §6" + value + "§f원 §7]");
                                                        target.sendMessage(header + "§b수락을 원하시면 /도박 포커 수락, §b거절을 원하시면 /도박 포커 거부");
                                                        GambleLogger.getInstance().addLog(p.getName() + "님이 " + target.getName() + "에게 포커 요청");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                        p.sendMessage(header + "§4숫자만 입력하세요..");
                                }
                                else
                                    p.sendMessage(header + "§f/도박 카드 시작 <대상> <최대금액>");

                            }
                        }
                    }
                    else if (arg[0].equalsIgnoreCase("주식")) {
                        if (arg.length >= 2 && p.isOp() && (arg[1].equalsIgnoreCase("추가") || arg[1].equalsIgnoreCase("삭제"))) {
                            if (arg[1].equalsIgnoreCase("추가")) {
                                if (arg.length == 5) {
                                    if (checkIntParameter(arg[3]) && checkIntParameter(arg[4])) {
                                        int money = Integer.parseInt(arg[3]);
                                        int width = Integer.parseInt(arg[4]);
                                        if (width <= 0 || money <= 0)
                                            p.sendMessage(header + "§f0이하의 값이 올 수 없습니다.");
                                        else if (StockManager.getInstance().getStockMap().size() == 45)
                                            p.sendMessage(header + "§f주식이 너무 많습니다.");
                                        else if (arg[2].contains("."))
                                            p.sendMessage(header + "§f주식이름에는 .이 올 수 없습니다.");
                                        else {
                                            StockManager.getInstance().addStock(arg[2], money, width);
                                            Bukkit.broadcastMessage(header + "§f새로운 주식이 상장되었습니다.");
                                        }
                                    }
                                    else
                                        p.sendMessage(header + "§c숫자만 입력해 주세요.");
                                }
                                else
                                    p.sendMessage(header + "§f/도박 주식 추가 <이름> <가격> <변동폭>");
                            }
                            else {
                                if (arg.length == 3) {
                                    if (StockManager.getInstance().isExist(arg[2])) {
                                        StockManager.getInstance().removeStock(arg[2]);
                                        p.sendMessage(header + "§f해당 주식이 제거되었습니다.");
                                    }
                                    else
                                        p.sendMessage(header + "§f존재하지 않는 주식입니다.");
                                }
                                else
                                    p.sendMessage(header + "§f/도박 주식 삭제 <이름>");
                            }
                        }
                        else if (p.isOp() && arg.length != 1)
                            p.sendMessage(header + "§f/도박 주식 <추가/삭제>");
                        else {
                            new Stock(p.getUniqueId());
                        }
                    }
                    else if (arg[0].equalsIgnoreCase("블랙리스트")) {
                        if (p.isOp()) {
                            if (arg.length == 1) {
                                p.sendMessage(header + "§f/도박 블랙리스트 <추가/삭제/목록/초기화>");
                            }
                            else if (arg[1].equalsIgnoreCase("추가") || arg[1].equalsIgnoreCase("삭제") || arg[1].equalsIgnoreCase("목록") || arg[1].equalsIgnoreCase("초기화")) {
                                if (arg[1].equalsIgnoreCase("추가")) {
                                    if (arg.length == 2) {
                                        p.sendMessage(header + "§b/도박 블랙리스트 추가 <닉네임>");
                                    }
                                    else {
                                        Player target = Bukkit.getServer().getPlayer(arg[2]);
                                        if (target == null) {
                                            p.sendTitle(header, "§c온라인 플레이어만 블랙리스트에 추가 할 수 있습니다..", 5, 50, 5);
                                        }
                                        else {
                                            DataManager.getInstance().addBlackList(target.getUniqueId());
                                            GambleLogger.getInstance().addLog(p.getName() + "님이 " + target.getName() + "님을 블랙리스트에 등재시킴.");
                                            p.sendTitle(header, "§f플레이어 §b" + target.getName() + "§f를 §7블랙리스트§f에 추가 하였습니다.", 5, 50, 5);
                                            p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 3f, 1f);
                                        }
                                    }
                                }
                                else if (arg[1].equalsIgnoreCase("삭제")) {
                                    if (arg.length == 2) {
                                        p.sendMessage(header + "§b/도박 블랙리스트 삭제 <번호>");
                                    }
                                    else if (checkIntParameter(arg[2])) {
                                        int input = Integer.parseInt(arg[2]) - 1;
                                        if (DataManager.getInstance().getBlacklist().size() <= input)
                                            p.sendMessage(header + "§c인덱스 범위를 벗어났습니다.");
                                        else {
                                            DataManager.getInstance().getBlacklist().remove(input);
                                            GambleLogger.getInstance().addLog(p.getName() + "님이 블랙리스트의 " + (input + 1) + "번째 인덱스를 삭제함.");
                                            p.sendTitle(header, "§f해당 §b플레이어§f를 §7블랙리스트§f에서 §f삭제하였습니다.", 5, 50, 5);
                                        }
                                    }
                                }
                                else if (arg[1].equalsIgnoreCase("목록")) {
                                    if (DataManager.getInstance().getBlacklist().size() == 0)
                                        p.sendMessage(header + "§c현재 블랙리스트 목록이 구성되어 있지 않습니다.");
                                    else {
                                        int i = 1;
                                        p.sendMessage(" ");
                                        for (UUID data : DataManager.getInstance().getBlacklist()) {
                                            Player target = Bukkit.getPlayer(data);
                                            p.sendMessage(" §f" + i++ + ". §b" + data + " §7- §c" + (target == null ? "Offline" : target.getName()));
                                        }
                                        p.sendMessage(" ");
                                    }
                                }
                                else if (arg[1].equalsIgnoreCase("초기화")) {
                                    DataManager.getInstance().getBlacklist().clear();
                                    p.sendTitle(header, "§4도박 블랙리스트가 초기화 되었습니다!", 5, 50, 5);
                                    GambleLogger.getInstance().addLog(p.getName() + "님이 블랙리스트를 초기화 함.");
                                }
                            }
                        }
                        else
                            p.sendMessage(header + "§c접근권한이 없습니다.");

                    }
                    else if (arg[0].equalsIgnoreCase("저장")) {
                        if (p.isOp()) {
                            ConfigManager.getInstance().saveData();
                            p.sendMessage(header + "§c데이터가 저장 되었습니다.");
                        }
                        else
                            p.sendMessage(header + "§c접근권한이 없습니다.");
                    }
                    else if (arg[0].equalsIgnoreCase("리로드")) {
                        if (p.isOp()) {
                            ConfigManager.getInstance().loadData();
                            p.sendMessage(header + "§b데이터가 리로드 되었습니다.");
                        }
                        else
                            p.sendMessage(header + "§c접근권한이 없습니다.");
                    }
                }
            }
            return true;
        }
        return false;
    }

    public DenyReason checkPlayer(Player p) {
        if (p.getInventory().getItemInMainHand().getType() != Material.AIR || p.getInventory().getItemInOffHand().getType() != Material.AIR)
            return DenyReason.HAND;
        else if (p.isSleeping())
            return DenyReason.SLEEP;
        else if (DataManager.getInstance().containBlackList(p.getUniqueId()))
            return DenyReason.BLACK;
        else
            return DenyReason.NONE;
    }

    public void sendDenyMessage(Player p, DenyReason reason) {
        switch (reason) {
            case HAND:
                p.sendTitle(header, "§b손에 아무것도 들지 않은 상태에서 진행해주세요", 5, 50, 5);
                break;
            case SLEEP:
                p.sendTitle(header, "§b잠을 자지 않는 상태에서 진행해주세요", 5, 50, 5);
                break;
            case JOIN:
                p.sendTitle(header, "§b게임에 참여하지 않은 상태에서 진행해주세요 </나가기>", 5, 50, 5);
                break;
            case BLACK:
                p.sendTitle(header, "§c당신은 도박 블랙리스트에 등재되어 있습니다.", 5, 50, 5);
                break;
        }
    }

    public boolean checkParameter(String value) {
        return value.equalsIgnoreCase("슬롯머신") || value.equalsIgnoreCase("주사위") || value.equalsIgnoreCase("블랙잭") || value.equalsIgnoreCase("룰렛") || value.equalsIgnoreCase("인디언포커") || value.equalsIgnoreCase("카드") || value.equalsIgnoreCase("동전") || value.equalsIgnoreCase("포커") || value.equalsIgnoreCase("주식") || value.equalsIgnoreCase("블랙리스트") || value.equalsIgnoreCase("저장") || value.equalsIgnoreCase("리로드");
    }

    public boolean checkIntParameter(String value) {
        try {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
