package me.masterofthefish.entitytrading.trades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTrades {

    private final String id;
    private final Map<Integer, TradeLevel> tradeLevels;

    public EntityTrades(final String id, final Map<Integer, TradeLevel> tradeLevels) {
        this.id = id;
        this.tradeLevels = tradeLevels;
    }

    public String getId() {
        return id;
    }

    public Map<Integer, TradeLevel> getTradeLevels() {
        return tradeLevels;
    }

    public TradeLevel getTradeLevel(final int level) {
        return tradeLevels.get(level);
    }

    public List<Integer> getAvailableLevels(final int points) {
        final List<Integer> levels = new ArrayList<>();
        for(Map.Entry<Integer, TradeLevel> entry : tradeLevels.entrySet()) {
            final int level = entry.getKey();
            final TradeLevel tradeLevel = entry.getValue();
            if (points >= tradeLevel.getPointsRequired()) {
                levels.add(level);
            }
        }
        return levels;
    }

    public EntityTrades getSetEntityTrades() {
        final Map<Integer, TradeLevel> setTradeLevels = new HashMap<>();
        for(Map.Entry<Integer, TradeLevel> entry : tradeLevels.entrySet()) {
            final int level = entry.getKey();
            final TradeLevel tradeLevel = entry.getValue();
            setTradeLevels.put(level, tradeLevel.getSetTradeLevel());
        }
        return new EntityTrades(id, setTradeLevels);
    }

    // Example Id:
    // type=level_tradenum:itemcost,itemcost,itemcost-level_tradenum:itemcost,itemcost,itemcost
    // villager=1-1:5,3,6_

    public String serialize() {
        final StringBuilder builder = new StringBuilder(id).append("=");
        final int size = tradeLevels.size();
        for(int i = 0; i < size; i++) {
            if (i != 0) {
                builder.append("-");
            }
            final TradeLevel tradeLevel = tradeLevels.get(i);
            if (tradeLevel == null) {
                continue;
            }
            builder.append(tradeLevels.get(i).serialize());
        }
        return builder.toString();
    }
}