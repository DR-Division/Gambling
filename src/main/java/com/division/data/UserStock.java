package com.division.data;

import java.util.HashMap;

public class UserStock {

    private HashMap<String, UserStockData> userMap;

    public UserStock() {
        userMap = new HashMap<>();
    }

    public boolean hasStock(String key) {
        return userMap.containsKey(key);
    }

    public int getAmount(String key) {
        return userMap.get(key).amount;
    }

    public double getAccumulate(String key) {
        return userMap.get(key).accumulate;
    }

    public void buyStock(String key, int amount, double current) {
        if (userMap.containsKey(key)) {
            userMap.get(key).amount += amount;
            userMap.get(key).accumulate += current;
        }
        else {
            userMap.put(key, new UserStockData(amount, current));
        }
    }

    public void sellStock(String key, int amount, double currentVal) {
        if (userMap.containsKey(key)) {
            userMap.get(key).amount -= amount;
            userMap.get(key).accumulate -= currentVal;
            if (userMap.get(key).amount <= 0)
                userMap.remove(key);
        }
    }

    private static class UserStockData{

        private int amount; //수량
        private double accumulate; //누적 구매값

        UserStockData(int amount, double current){
            this.amount = amount;
            this.accumulate = current;
        }
    }


}
