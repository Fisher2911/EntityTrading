package me.masterofthefish.entitytrading.trades;

import me.masterofthefish.entitytrading.villagerguiapi.masecla.villager.classes.VillagerTrade;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TradeLevel {

    private final int level;
    private final int pointsRequired;
    private final List<Trade> tradeList;
    private final int tradesShown;

    public TradeLevel(final int level, final int pointsRequired, final List<Trade> tradeList, final int tradesShown) {
        this.level = level;
        this.pointsRequired = pointsRequired;
        this.tradeList = tradeList;
        this.tradesShown = tradesShown;
    }

    public int getLevel() {
        return level;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public List<Trade> getTradeList() {
        return tradeList;
    }

    public int getTradesShown() {
        return tradesShown;
    }

    public Trade getTrade(int tradeNum) {
        return tradeList.get(tradeNum);
    }

    public Trade getTradeFromVillagerTrade(final VillagerTrade villagerTrade) {
        final ItemStack firstItem = villagerTrade.getItemOne();
        ItemStack secondItem = villagerTrade.getItemTwo();
        final ItemStack result = villagerTrade.getResult();

        for (final Trade trade : tradeList) {
            if (trade.getFirstGivenItem().getItemStack().isSimilar(firstItem) &&
                trade.getReceivedItem().getItemStack().isSimilar(result)) {
                if (trade.getSecondGivenItem() != null &&
                        !trade.getSecondGivenItem().getItemStack().isSimilar(secondItem)) {
                    continue;
                }
                return trade;
            }
        }
        return null;
    }

    public void addTrade(final Trade trade) {
        this.tradeList.add(trade);
    }

    public TradeLevel getSetTradeLevel() {
        final List<Trade> setTrades = new ArrayList<>();
        for (final Trade trade : tradeList) {
            setTrades.add(trade.getSetTrade());
        }
        return new TradeLevel(level, pointsRequired, setTrades, tradesShown);
    }

    // Example Id:
    // type=level_tradenum:itemcost,itemcost,itemcost-level_tradenum:itemcost,itemcost,itemcost
    // villager=1-1:5,3,6_

    public String serialize() {
        final StringBuilder builder = new StringBuilder(level);
        boolean allTradesAdded = false;

        final Set<Integer> tradesAdded = new HashSet<>();
        while (!allTradesAdded) {
            final int tradeNum = new Random().nextInt(tradeList.size());
            if(tradesAdded.contains(tradeNum)) {
                continue;
            }
            tradesAdded.add(tradeNum);
            final Trade trade = tradeList.get(tradeNum);
                final TradeItem firstItem = trade.getFirstGivenItem();
                final TradeItem secondItem = trade.getSecondGivenItem();
                final TradeItem receivedItem = trade.getReceivedItem();
                builder.append(level).append("_").append(tradeNum).
                        append(":").append(firstItem.getMinAmount()).append(",");
                if (secondItem != null) {
                    builder.append(secondItem.getMinAmount());
                } else {
                    builder.append(0);
                }
                builder.append(",");
                builder.append(receivedItem.getMinAmount());
                if (tradesAdded.size() != tradesShown) {
                    builder.append("-");
                } else {
                    allTradesAdded = true;
                }
        }
        return builder.toString();
    }
}