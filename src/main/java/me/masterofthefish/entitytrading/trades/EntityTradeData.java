package me.masterofthefish.entitytrading.trades;

public class EntityTradeData {

    private final EntityTrades entityTrades;
    private int points;

    public EntityTradeData(final EntityTrades entityTrades, final int points) {
        this.entityTrades = entityTrades;
        this.points = points;
    }

    public EntityTrades getEntityTrades() {
        return entityTrades;
    }

    public int getPoints() {
        return points;
    }
}
