package com.division.data;

import java.util.ArrayList;
import java.util.UUID;

public class DataManager {

    private static DataManager instance;
    private int slotMin;
    private double slotCount;
    private int diceMin;
    private int diceMax;
    private int blackjackMin;
    private int blackjackMax;
    private int rouletteMin;
    private int rouletteMax;
    private int coinMin;
    private int indianMin;
    private int indianMax;
    private int cardMin;
    private int cardMax;
    private int pokerMin;
    private int pokerMax;
    private String header;
    private ArrayList<UUID> blacklist;

    static {
        instance = new DataManager();
    }

    private DataManager() {
        slotMin = 0;
        slotCount = 0.0;
        diceMin = 0;
        diceMax = 0;
        blackjackMin = 0;
        blackjackMax = 0;
        rouletteMin = 0;
        rouletteMax = 0;
        coinMin = 0;
        indianMin = 0;
        indianMax = 0;
        cardMin = 0;
        cardMax = 0;
        pokerMin = 0;
        pokerMax = 0;
        blacklist = new ArrayList<>();
        header = "§6[ §f! §6] ";
    }

    public static DataManager getInstance() {
        return instance;
    }

    public int getSlotMin() {
        return slotMin;
    }

    public void setSlotMin(int slotMin) {
        this.slotMin = slotMin;
    }

    public double getSlotCount() {
        return slotCount;
    }

    public void setSlotCount(double slotCount) {
        this.slotCount = slotCount;
    }

    public int getDiceMin() {
        return diceMin;
    }

    public void setDiceMin(int diceMin) {
        this.diceMin = diceMin;
    }

    public int getDiceMax() {
        return diceMax;
    }

    public void setDiceMax(int diceMax) {
        this.diceMax = diceMax;
    }

    public int getBlackjackMin() {
        return blackjackMin;
    }

    public void setBlackjackMin(int blackjackMin) {
        this.blackjackMin = blackjackMin;
    }

    public int getBlackjackMax() {
        return blackjackMax;
    }

    public void setBlackjackMax(int blackjackMax) {
        this.blackjackMax = blackjackMax;
    }

    public int getRouletteMin() {
        return rouletteMin;
    }

    public void setRouletteMin(int rouletteMin) {
        this.rouletteMin = rouletteMin;
    }

    public int getRouletteMax() {
        return rouletteMax;
    }

    public void setRouletteMax(int rouletteMax) {
        this.rouletteMax = rouletteMax;
    }

    public int getCoinMin() {
        return coinMin;
    }

    public void setCoinMin(int coinMin) {
        this.coinMin = coinMin;
    }

    public int getIndianMin() {
        return indianMin;
    }

    public void setIndianMin(int indianMin) {
        this.indianMin = indianMin;
    }

    public int getIndianMax() {
        return indianMax;
    }

    public void setIndianMax(int indianMax) {
        this.indianMax = indianMax;
    }

    public int getCardMin() {
        return cardMin;
    }

    public void setCardMin(int cardMin) {
        this.cardMin = cardMin;
    }

    public int getCardMax() {
        return cardMax;
    }

    public void setCardMax(int cardMax) {
        this.cardMax = cardMax;
    }

    public int getPokerMin() {
        return pokerMin;
    }

    public void setPokerMin(int pokerMin) {
        this.pokerMin = pokerMin;
    }

    public int getPokerMax() {
        return pokerMax;
    }

    public void setPokerMax(int pokerMax) {
        this.pokerMax = pokerMax;
    }

    public void addBlackList(UUID data) {
        if (!blacklist.contains(data))
            blacklist.add(data);
    }

    public boolean containBlackList(UUID data) {
        return blacklist.contains(data);
    }

    public ArrayList<UUID> getBlacklist() {
        return blacklist;
    }

    public String getHeader() {
        return header;
    }
}
