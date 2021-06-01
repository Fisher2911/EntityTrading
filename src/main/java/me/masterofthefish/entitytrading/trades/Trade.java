package me.masterofthefish.entitytrading.trades;

public class Trade {

    private final TradePoints tradePoints;
    private final TradeItem firstGivenItem;
    private final TradeItem secondGivenItem;
    private final TradeItem receivedItem;

    public Trade(final TradePoints tradePoints, final TradeItem firstGivenItem, final TradeItem secondGivenItem, final TradeItem receivedItem) {
        this.tradePoints = tradePoints;
        this.firstGivenItem = firstGivenItem;
        this.secondGivenItem = secondGivenItem;
        this.receivedItem = receivedItem;
    }

    public Trade(final TradePoints tradePoints, final TradeItem firstGivenItem, final TradeItem receivedItem) {
        this(tradePoints, firstGivenItem, null, receivedItem);
    }

    public boolean requiresTwoItems() {
        return secondGivenItem != null;
    }

    public TradePoints getTradePoints() {
        return tradePoints;
    }

    public TradeItem getFirstGivenItem() {
        return firstGivenItem;
    }

    public TradeItem getSecondGivenItem() {
        return secondGivenItem;
    }

    public TradeItem getReceivedItem() {
        return receivedItem;
    }

    public Trade getSetTrade() {
        final TradeBuilder tradeBuilder = TradeBuilder.create().
        setTradePoints(tradePoints).
        setFirstGivenItem(firstGivenItem.getSetTradeItem()).
                setReceivedItem(receivedItem.getSetTradeItem());
        if (requiresTwoItems()) {
            tradeBuilder.setSecondGivenItem(secondGivenItem.getSetTradeItem());
        }
        return tradeBuilder.build();
    }

    public static class TradeBuilder {

        private TradeBuilder() {

        }

        private TradePoints tradePoints;
        private TradeItem firstGivenItem;
        private TradeItem secondGivenItem;
        private TradeItem receivedItem;

        public static TradeBuilder create() {
            return new TradeBuilder();
        }

        public TradeBuilder setTradePoints(final TradePoints tradePoints) {
            this.tradePoints = tradePoints;
            return this;
        }

        public TradeBuilder setFirstGivenItem(final TradeItem firstGivenItem) {
            this.firstGivenItem = firstGivenItem;
            return this;
        }

        public TradeBuilder setSecondGivenItem(final TradeItem secondGivenItem) {
            this.secondGivenItem = secondGivenItem;
            return this;
        }

        public TradeBuilder setReceivedItem(final TradeItem receivedItem) {
            this.receivedItem = receivedItem;
            return this;
        }

        public Trade build() {
            return new Trade(tradePoints, firstGivenItem, secondGivenItem, receivedItem);
        }
    }
}
