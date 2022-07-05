package com.division.data;

import java.util.HashMap;
import java.util.UUID;

public class PokerData {

    private static PokerData instance;
    private HashMap<UUID, UUID> pokerRequest;
    private HashMap<UUID, Integer> pokerBet;

    public enum Result {
        REQUESTER,
        TARGET,
        NONE
    }

    static {
        instance = new PokerData();
    }

    private PokerData() {
        pokerRequest = new HashMap<>();
        pokerBet = new HashMap<>();
    }

    public static PokerData getInstance() {
        return instance;
    }

    public Result findPlayer(UUID target) {
        for (UUID data : pokerRequest.keySet()) {
            if (data.equals(target))
                return Result.REQUESTER;
            else if (pokerRequest.get(data).equals(target))
                return Result.TARGET;
        }
        return Result.NONE;
    }

    public void remove(UUID target) {
        UUID value = getTarget(target);
        pokerRequest.remove(target);
        pokerRequest.entrySet().removeIf(data -> data.getValue().equals(target));
        pokerBet.remove(target);
        if (value != null) {
            pokerRequest.remove(value);
            pokerRequest.entrySet().removeIf(data -> data.getValue().equals(value));
            pokerBet.remove(value);
        }
    }

    public UUID getTarget(UUID target) {
        Result type = findPlayer(target);
        switch (type) {
            case REQUESTER:
                return pokerRequest.get(target);
            case TARGET:
                for (UUID data : pokerRequest.keySet())
                    if (pokerRequest.get(data).equals(target))
                        return data;
            case NONE:
                return null;
        }
        return null;
    }

    public void addValue(UUID request, UUID target, int value) {
        pokerRequest.put(request, target);
        pokerBet.put(request, value);
    }

    public int getMoney(UUID request) {
        return pokerBet.get(request);
    }

}
